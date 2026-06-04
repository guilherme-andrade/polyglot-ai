package com.polyglotai.user.application;

/**
 * Input to {@link RegisterAccountService#register}. A command object bundles the raw inputs of one
 * use-case invocation, keeping the service signature stable as the use case grows.
 */
public record RegisterAccountCommand(String email, String rawPassword) {}
