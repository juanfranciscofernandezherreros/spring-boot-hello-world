package com.fernandez.scheduler;

import com.fernandez.scheduler.config.SchedulerTaskProperties;
import com.fernandez.scheduler.service.DynamicSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicSchedulerServiceTest {

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ScheduledFuture<?> scheduledFuture;

    private SchedulerTaskProperties properties;

    private DynamicSchedulerService service;

    @BeforeEach
    void setUp() {
        properties = new SchedulerTaskProperties();
    }

    @Test
    void shouldScheduleEnabledTasks() {
        SchedulerTaskProperties.TaskConfig taskConfig = new SchedulerTaskProperties.TaskConfig();
        taskConfig.setEnabled(true);
        taskConfig.setCron("0 0/30 * * * *");
        taskConfig.setUrl("https://localhost:8443/api/test");

        Map<String, SchedulerTaskProperties.TaskConfig> tasks = new HashMap<>();
        tasks.put("test-task", taskConfig);
        properties.setTasks(tasks);

        doReturn(scheduledFuture).when(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));

        service = new DynamicSchedulerService(taskScheduler, restTemplate, properties);
        service.scheduleTasks();

        verify(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));
        assertThat(service.getScheduledTasks()).containsKey("test-task");
    }

    @Test
    void shouldNotScheduleDisabledTasks() {
        SchedulerTaskProperties.TaskConfig taskConfig = new SchedulerTaskProperties.TaskConfig();
        taskConfig.setEnabled(false);
        taskConfig.setCron("0 0/30 * * * *");
        taskConfig.setUrl("https://localhost:8443/api/test");

        Map<String, SchedulerTaskProperties.TaskConfig> tasks = new HashMap<>();
        tasks.put("disabled-task", taskConfig);
        properties.setTasks(tasks);

        service = new DynamicSchedulerService(taskScheduler, restTemplate, properties);
        service.scheduleTasks();

        verify(taskScheduler, never()).schedule(any(Runnable.class), any(CronTrigger.class));
        assertThat(service.getScheduledTasks()).isEmpty();
    }

    @Test
    void shouldHandleEmptyTaskConfiguration() {
        service = new DynamicSchedulerService(taskScheduler, restTemplate, properties);
        service.scheduleTasks();

        verify(taskScheduler, never()).schedule(any(Runnable.class), any(CronTrigger.class));
        assertThat(service.getScheduledTasks()).isEmpty();
    }

    @Test
    void shouldExecuteHttpCallSuccessfully() {
        service = new DynamicSchedulerService(taskScheduler, restTemplate, properties);

        when(restTemplate.getForObject("https://localhost:8443/api/test", String.class))
                .thenReturn("OK");

        service.executeHttpCall("test-task", "https://localhost:8443/api/test");

        verify(restTemplate).getForObject("https://localhost:8443/api/test", String.class);
    }

    @Test
    void shouldHandleHttpCallFailureGracefully() {
        service = new DynamicSchedulerService(taskScheduler, restTemplate, properties);

        when(restTemplate.getForObject("https://localhost:8443/api/test", String.class))
                .thenThrow(new RuntimeException("Connection refused"));

        service.executeHttpCall("test-task", "https://localhost:8443/api/test");

        verify(restTemplate).getForObject("https://localhost:8443/api/test", String.class);
    }

    @Test
    void shouldCancelTask() {
        SchedulerTaskProperties.TaskConfig taskConfig = new SchedulerTaskProperties.TaskConfig();
        taskConfig.setEnabled(true);
        taskConfig.setCron("0 0/30 * * * *");
        taskConfig.setUrl("https://localhost:8443/api/test");

        Map<String, SchedulerTaskProperties.TaskConfig> tasks = new HashMap<>();
        tasks.put("cancel-task", taskConfig);
        properties.setTasks(tasks);

        doReturn(scheduledFuture).when(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));

        service = new DynamicSchedulerService(taskScheduler, restTemplate, properties);
        service.scheduleTasks();

        assertThat(service.getScheduledTasks()).containsKey("cancel-task");

        service.cancelTask("cancel-task");

        verify(scheduledFuture).cancel(false);
        assertThat(service.getScheduledTasks()).doesNotContainKey("cancel-task");
    }

    @Test
    void shouldScheduleMultipleEnabledTasks() {
        SchedulerTaskProperties.TaskConfig task1 = new SchedulerTaskProperties.TaskConfig();
        task1.setEnabled(true);
        task1.setCron("0 0/30 * * * *");
        task1.setUrl("https://localhost:8443/api/alert");

        SchedulerTaskProperties.TaskConfig task2 = new SchedulerTaskProperties.TaskConfig();
        task2.setEnabled(true);
        task2.setCron("0 0 8 * * MON,TUE,WED,THU,FRI");
        task2.setUrl("https://localhost:8443/api/item33");

        SchedulerTaskProperties.TaskConfig task3 = new SchedulerTaskProperties.TaskConfig();
        task3.setEnabled(false);
        task3.setCron("0 0 7 * * MON,TUE,WED,THU,FRI");
        task3.setUrl("https://localhost:8443/api/item35");

        Map<String, SchedulerTaskProperties.TaskConfig> tasks = new HashMap<>();
        tasks.put("alert", task1);
        tasks.put("item33", task2);
        tasks.put("item35", task3);
        properties.setTasks(tasks);

        doReturn(scheduledFuture).when(taskScheduler).schedule(any(Runnable.class), any(CronTrigger.class));

        service = new DynamicSchedulerService(taskScheduler, restTemplate, properties);
        service.scheduleTasks();

        assertThat(service.getScheduledTasks()).hasSize(2);
        assertThat(service.getScheduledTasks()).containsKey("alert");
        assertThat(service.getScheduledTasks()).containsKey("item33");
        assertThat(service.getScheduledTasks()).doesNotContainKey("item35");
    }
}
