package io.github.willchantech.forge.task.job.model;

import java.util.concurrent.ScheduledFuture;

/**
 * @Desc :  任务执行句柄 + cron 表达式
 * @Author : Will Chan
 * @Date : 2026/5/4 20:28
 */
public class TaskHolder {
    private ScheduledFuture<?> future;
    private String cronExpression;

    public TaskHolder() {
    }

    public TaskHolder(ScheduledFuture<?> future, String cronExpression) {
        this.future = future;
        this.cronExpression = cronExpression;
    }

    public ScheduledFuture<?> getFuture() {
        return future;
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
