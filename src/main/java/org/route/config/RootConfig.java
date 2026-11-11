package org.route.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.route.repository", "org.route.service"})
public class RootConfig {
}
