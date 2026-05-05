package io.github.willchantech.forge.task.job.model;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * 任务调度值对象
 * @author willchan-tech
 */
public class TaskScheduleVO {

    /** 任务ID */
    private Long id;
    
    /** 任务描述 */
    private String description;
    
    /** Cron表达式 */
    private String cronExpression;
    
    /** 任务参数 */
    private String taskParam;

    /**
     * 任务执行器函数式接口
     *      双重嵌套函数式接口，最外层是Supplier（get 方法），最内层是 Runnable（run 方法）
     */
    private Supplier<Runnable> taskExecutor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getTaskParam() {
        return taskParam;
    }

    public void setTaskParam(String taskParam) {
        this.taskParam = taskParam;
    }

    public Supplier<Runnable> getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(Supplier<Runnable> taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public TaskScheduleVO() {
    }

    /**
     * 便捷方法：设置任务执行逻辑
     * @param taskLogic 任务执行逻辑
     */
    public void buildTaskLogic(Runnable taskLogic) {
        this.taskExecutor = () -> taskLogic;
    }
    
    /**
     * 便捷方法：设置带参数的任务执行逻辑
     * @param taskLogic 接收 taskId 和 taskParam 两个参数，执行任务逻辑
     */
    public void buildTaskLogic(BiConsumer<Long, String> taskLogic) {
        this.taskExecutor = () -> () -> taskLogic.accept(this.id, this.taskParam);
    }

    @Override
    public String toString() {
        return "TaskScheduleVO{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", taskParam='" + taskParam + '\'' +
                ", hasTaskExecutor=" + (taskExecutor != null) +
                '}';
    }
}