#!/usr/bin/env node

/**
 * Linter for OpenSpec format in docs/specs/*.md and docs/architecture/adr/*.md.
 *
 * Enforces:
 *   - "## Purpose" section present and >= 50 characters
 *   - "## Requirements" section present (specs only, ADRs use their own format)
 *   - Each "### Requirement:" has text containing SHALL or MUST
 *   - Each requirement has at least one "#### Scenario:" with GIVEN/WHEN/THEN/AND
 *
 * Usage:
 *   node scripts/lint-specs.js [--adr] [files...]
 *   If no files given, lints docs/specs/*.md (and docs/architecture/adr/*.md if --adr).
 */

const fs = require("fs");
const path = require("path");

const SPECS_DIR = path.resolve(__dirname, "..", "docs", "specs");
const ADR_DIR = path.resolve(__dirname, "..", "docs", "architecture", "adr");

let errors = 0;
let warnings = 0;

function report(level, file, line, message) {
  const prefix = level === "error" ? "ERROR" : "WARNING";
  const loc = line ? `${file}:${line}` : file;
  process.stderr.write(`${prefix}: ${loc} — ${message}\n`);
  if (level === "error") errors++;
  else warnings++;
}

function lintSpec(filePath) {
  let content;
  try {
    content = fs.readFileSync(filePath, "utf-8");
  } catch {
    report("error", filePath, 0, `Cannot read file`);
    return;
  }

  const lines = content.split("\n");

  // Check for Purpose section
  const purposeMatch = content.match(/^## Purpose\s*$/m);
  if (!purposeMatch) {
    report("error", filePath, 1, 'Missing "## Purpose" section');
    return; // Can't meaningfully lint the rest
  }

  // Extract Purpose content (everything between ## Purpose and next ## header)
  const purposeStart = content.indexOf(purposeMatch[0]) + purposeMatch[0].length;
  const restAfterPurpose = content.slice(purposeStart);
  const nextH2 = restAfterPurpose.match(/^## /m);
  const purposeBody = nextH2
    ? restAfterPurpose.slice(0, nextH2.index).trim()
    : restAfterPurpose.trim();

  // Find line number of Purpose header
  const purposeLine = lines.findIndex((l) => /^## Purpose\s*$/.test(l)) + 1;

  if (purposeBody.length < 50) {
    report(
      "warning",
      filePath,
      purposeLine,
      `Purpose section is ${purposeBody.length} chars (minimum 50 recommended)`,
    );
  }

  // Check for Requirements section
  if (!/^## Requirements\s*$/m.test(content)) {
    report("error", filePath, 1, 'Missing "## Requirements" section');
    return;
  }

  // Find all requirements
  const reqPattern = /^### Requirement:\s*(.+)$/gm;
  const requirements = [...content.matchAll(reqPattern)];

  if (requirements.length === 0) {
    report("error", filePath, 1, 'No "### Requirement:" blocks found under ## Requirements');
    return;
  }

  for (const reqMatch of requirements) {
    const reqTitle = reqMatch[1];
    const reqLine = content.slice(0, reqMatch.index).split("\n").length;

    // Find the requirement text (first non-empty line after the header)
    const afterHeader = content.slice(reqMatch.index + reqMatch[0].length);
    const nextHeaderMatch = afterHeader.match(/^###?\s/m);
    const reqSection = nextHeaderMatch
      ? afterHeader.slice(0, nextHeaderMatch.index)
      : afterHeader;
    const reqTextLine = reqSection.trim().split("\n")[0] || "";

    // Requirement text must contain SHALL or MUST
    if (reqTextLine && !/\b(SHALL|MUST)\b/.test(reqTextLine)) {
      report(
        "error",
        filePath,
        reqLine + 1,
        `Requirement "${reqTitle}" text must contain SHALL or MUST`,
      );
    } else if (!reqTextLine) {
      report(
        "error",
        filePath,
        reqLine,
        `Requirement "${reqTitle}" has no body text`,
      );
    }

    // Check for scenarios
    const scenarioPattern = /^#### Scenario:\s*(.+)$/gm;
    const scenarioSection = reqSection;
    const scenarios = [...scenarioSection.matchAll(scenarioPattern)];

    if (scenarios.length === 0) {
      report(
        "error",
        filePath,
        reqLine,
        `Requirement "${reqTitle}" has no scenarios`,
      );
      continue;
    }

    for (const scenarioMatch of scenarios) {
      const scenarioTitle = scenarioMatch[1];
      const scenarioLine =
        content.slice(0, reqMatch.index).split("\n").length +
        scenarioSection.slice(0, scenarioMatch.index).split("\n").length;

      // Find the scenario body (until next #### or ### or ##)
      const afterScenario = scenarioSection.slice(
        scenarioMatch.index + scenarioMatch[0].length,
      );
      const nextBlock = afterScenario.match(/^#{2,4}\s/m);
      const scenarioBody = nextBlock
        ? afterScenario.slice(0, nextBlock.index).trim()
        : afterScenario.trim();

      // Each scenario must have at least one GIVEN/WHEN/THEN/AND
      if (!/\b(GIVEN|WHEN|THEN)\b/.test(scenarioBody)) {
        report(
          "error",
          filePath,
          scenarioLine,
          `Scenario "${scenarioTitle}" must contain GIVEN, WHEN, or THEN`,
        );
      }

      // Warn about empty scenario body
      if (scenarioBody.length < 10) {
        report(
          "warning",
          filePath,
          scenarioLine,
          `Scenario "${scenarioTitle}" body seems too short`,
        );
      }
    }

    // Check scenario count (warn only)
    if (scenarios.length < 2) {
      const reqLineNum =
        content.slice(0, reqMatch.index).split("\n").length;
      report(
        "warning",
        filePath,
        reqLineNum,
        `Requirement "${reqTitle}" has only ${scenarios.length} scenario (2+ recommended)`,
      );
    }
  }
}

function lintAdr(filePath) {
  let content;
  try {
    content = fs.readFileSync(filePath, "utf-8");
  } catch {
    report("error", filePath, 0, `Cannot read file`);
    return;
  }

  // ADRs must have Status and Date
  if (!/\*\*Status\*\*:\s*(draft|proposed|accepted|deprecated|superseded)/i.test(content)) {
    report("error", filePath, 1, 'Missing or invalid "**Status**" field');
  }
  if (!/\*\*Date\*\*:\s*\d{4}-\d{2}-\d{2}/.test(content)) {
    report("error", filePath, 1, 'Missing or invalid "**Date**" field');
  }

  // ADRs must have Context, Decision(s), and Consequences sections
  if (!/^## Context\s*$/m.test(content)) {
    report("error", filePath, 1, 'Missing "## Context" section');
  }
  if (!/^## Decisions?\s*$/m.test(content)) {
    report("error", filePath, 1, 'Missing "## Decision" or "## Decisions" section');
  }
  if (!/^## Consequences\s*$/m.test(content)) {
    report("error", filePath, 1, 'Missing "## Consequences" section');
  }
}

function main() {
  const args = process.argv.slice(2);
  let files = [];
  let includeAdr = false;

  for (const arg of args) {
    if (arg === "--adr") {
      includeAdr = true;
    } else {
      files.push(arg);
    }
  }

  if (files.length === 0) {
    // Default: all spec files
    if (fs.existsSync(SPECS_DIR)) {
      const specFiles = fs
        .readdirSync(SPECS_DIR)
        .filter((f) => f.endsWith(".md"))
        .map((f) => path.join(SPECS_DIR, f));
      files.push(...specFiles);
    } else {
      process.stderr.write(`Specs directory not found: ${SPECS_DIR}\n`);
      process.exit(1);
    }

    if (includeAdr && fs.existsSync(ADR_DIR)) {
      const adrFiles = fs
        .readdirSync(ADR_DIR)
        .filter((f) => f.endsWith(".md"))
        .map((f) => path.join(ADR_DIR, f));
      files.push(...adrFiles);
    }
  }

  for (const file of files) {
    const basename = path.basename(file);
    const isAdr = /^\d{4}-/.test(basename) || file.includes("/adr/");
    if (isAdr) {
      lintAdr(file);
    } else {
      lintSpec(file);
    }
  }

  if (errors > 0) {
    process.stderr.write(
      `\n${errors} error(s), ${warnings} warning(s)\n`,
    );
    process.exit(1);
  }

  if (warnings > 0) {
    process.stderr.write(`\n0 errors, ${warnings} warning(s)\n`);
  }

  process.exit(0);
}

main();
