package com.polyglotai.bootstrap;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * Temporary resolver backing the root schema's {@code ping} field.
 *
 * <p>Exists only so {@code spring-boot-starter-graphql} has a valid {@code Query} type
 * to bind at startup. Remove this class and the corresponding field in
 * {@code graphql/schema.graphqls} as soon as any bounded context defines a real query
 * in its own {@code interfaces} layer.
 */
@Controller
public class PingGraphqlController {

    @QueryMapping
    public String ping() {
        return "pong";
    }
}
