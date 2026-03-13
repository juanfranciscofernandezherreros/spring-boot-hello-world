package com.fernandez.scheduler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "component-config.dynamic-scheduling")
public class SchedulerTaskProperties {

    private Map<String, TaskConfig> tasks = new HashMap<>();

    @Data
    public static class TaskConfig {
        private boolean enabled;
        private String cron;
        private String url;
    }
}
