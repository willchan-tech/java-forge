package io.github.willchantech.forge.task.job.service;

import io.github.willchantech.forge.task.job.model.TaskScheduleVO;

/**
 * 任务调度服务接口
 *
 * @author willchan-tech
 */
public interface ITaskJobManager {

    /**
     * 添加单个任务
     * @param task 任务调度配置
     * @return 是否添加成功
     */
    boolean addTask(TaskScheduleVO task);

    /**
     * 移除单个任务
     * @param taskId 任务ID
     * @param force 是否强制移除
     * @return 是否移除成功
     */
    boolean removeTask(Long taskId, boolean force);

    /**
     * 刷新任务调度配置：
     *      全量对齐 TaskDataProvider 提供的任务
     */
    void refreshTasks();
    
    /**
     * 停止所有任务
     */
    void stopAllTasks();
    
    /**
     * 获取当前活跃任务数量
     * @return 活跃任务数量
     */
    int getActiveTaskCount();
    
}