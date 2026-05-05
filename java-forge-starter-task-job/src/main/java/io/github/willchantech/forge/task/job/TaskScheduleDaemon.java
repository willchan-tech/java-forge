package io.github.willchantech.forge.task.job;

import io.github.willchantech.forge.task.job.config.TaskJobAutoProperties;
import io.github.willchantech.forge.task.job.service.ITaskJobService;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 后台守护式任务：
 *      定时刷新任务调度配置
 *          将有效的任务保持与 DataProvider 中提供的一致 （全量对齐的方式）
 *      定时清理无效任务
 *          显式删除无效任务
 * @author willchan-tech
 */
public class TaskScheduleDaemon {

    private final TaskJobAutoProperties properties;
    private final ITaskJobService taskJobService;

    public TaskScheduleDaemon(TaskJobAutoProperties properties, ITaskJobService taskJobService) {
        this.properties = properties;
        this.taskJobService = taskJobService;
    }

    /**
     * java-forge.task-job.refresh-interval 是来自 yml 的配置，60000 毫秒 = 60 秒 是该配置的默认值。
     * 定时刷新任务调度配置
     * 注：
     *      参数fixedRateString，表示该方法每间隔固定时间执行一次。从上一次开始就计时。（可能会触发并行执行）
     *      参数fixedDelayString，表示该方法每执行一次，间隔固定时间。从上一次结束开始计时。
     */
    @Scheduled(fixedDelayString = "${java-forge.task-job.refresh-tasks-interval:60000}")
    public void refreshTasks() {
        if (!properties.isEnabled()) {
            return;
        }
        taskJobService.refreshTasks();
    }
}