package com.fernandez.scheduler;

import com.fernandez.scheduler.config.SchedulerTaskProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SchedulerTaskPropertiesTest.TestConfig.class)
@TestPropertySource(properties = {
        "component-config.dynamic-scheduling.tasks.test-alert.enabled=true",
        "component-config.dynamic-scheduling.tasks.test-alert.cron=0 0/30 * * * *",
        "component-config.dynamic-scheduling.tasks.test-alert.url=https://localhost:8443/api/test",
        "component-config.dynamic-scheduling.tasks.disabled-task.enabled=false",
        "component-config.dynamic-scheduling.tasks.disabled-task.cron=0 0 8 * * *",
        "component-config.dynamic-scheduling.tasks.disabled-task.url=https://localhost:8443/api/disabled"
})
class SchedulerTaskPropertiesTest {

    @EnableConfigurationProperties(SchedulerTaskProperties.class)
    static class TestConfig {
    }

    @Autowired
    private SchedulerTaskProperties properties;

    @Test
    void shouldBindTaskPropertiesFromConfiguration() {
        assertThat(properties.getTasks()).isNotEmpty();
        assertThat(properties.getTasks()).containsKey("test-alert");

        SchedulerTaskProperties.TaskConfig alertConfig = properties.getTasks().get("test-alert");
        assertThat(alertConfig.isEnabled()).isTrue();
        assertThat(alertConfig.getCron()).isEqualTo("0 0/30 * * * *");
        assertThat(alertConfig.getUrl()).isEqualTo("https://localhost:8443/api/test");
    }

    @Test
    void shouldBindDisabledTaskProperties() {
        SchedulerTaskProperties.TaskConfig disabledConfig = properties.getTasks().get("disabled-task");
        assertThat(disabledConfig).isNotNull();
        assertThat(disabledConfig.isEnabled()).isFalse();
    }
}
