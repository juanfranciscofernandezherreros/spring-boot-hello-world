package com.fernandez.scheduler.service;

import com.fernandez.scheduler.config.SchedulerTaskProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DynamicSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(DynamicSchedulerService.class);

    private final TaskScheduler taskScheduler;
    private final RestTemplate restTemplate;
    private final SchedulerTaskProperties properties;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public DynamicSchedulerService(TaskScheduler taskScheduler,
                                   RestTemplate schedulerRestTemplate,
                                   SchedulerTaskProperties properties) {
        this.taskScheduler = taskScheduler;
        this.restTemplate = schedulerRestTemplate;
        this.properties = properties;
    }

    @PostConstruct
    public void scheduleTasks() {
        Map<String, SchedulerTaskProperties.TaskConfig> tasks = properties.getTasks();
        if (tasks == null || tasks.isEmpty()) {
            log.info("No dynamic scheduling tasks configured.");
            return;
        }

        tasks.forEach((taskName, taskConfig) -> {
            if (taskConfig.isEnabled()) {
                scheduleTask(taskName, taskConfig);
            } else {
                log.info("Task '{}' is disabled, skipping.", taskName);
            }
        });
    }

    private void scheduleTask(String taskName, SchedulerTaskProperties.TaskConfig taskConfig) {
        log.info("Scheduling task '{}' with cron '{}' -> {}", taskName, taskConfig.getCron(), taskConfig.getUrl());
        try {
            CronTrigger cronTrigger = new CronTrigger(taskConfig.getCron());
            ScheduledFuture<?> future = taskScheduler.schedule(
                    () -> executeHttpCall(taskName, taskConfig.getUrl()),
                    cronTrigger
            );
            scheduledTasks.put(taskName, future);
            log.info("Task '{}' scheduled successfully.", taskName);
        } catch (IllegalArgumentException e) {
            log.error("Invalid cron expression '{}' for task '{}': {}", taskConfig.getCron(), taskName, e.getMessage());
        }
    }

    public void executeHttpCall(String taskName, String url) {
        log.info("Executing scheduled task '{}': calling {}", taskName, url);
        try {
            String response = restTemplate.getForObject(url, String.class);
            log.info("Task '{}' completed successfully. Response: {}", taskName, response);
        } catch (Exception e) {
            log.error("Task '{}' failed calling {}: {}", taskName, url, e.getMessage());
        }
    }

    public Map<String, ScheduledFuture<?>> getScheduledTasks() {
        return scheduledTasks;
    }

    public void cancelTask(String taskName) {
        ScheduledFuture<?> future = scheduledTasks.get(taskName);
        if (future != null) {
            future.cancel(false);
            scheduledTasks.remove(taskName);
            log.info("Task '{}' cancelled.", taskName);
        }
    }
}
