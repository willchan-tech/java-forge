package io.github.willchantech.forge.task.job.config;

import io.github.willchantech.forge.task.job.TaskScheduleDaemon;
import io.github.willchantech.forge.task.job.provider.ITaskDataProvider;
import io.github.willchantech.forge.task.job.service.ITaskJobManager;
import io.github.willchantech.forge.task.job.service.TaskJobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;

/**
 * 任务调度器自动配置类
 *
 * @author willchan-tech
 */
@Configuration
@EnableScheduling // 开启定时任务（任务调度框架）
@EnableConfigurationProperties(TaskJobAutoProperties.class)
//properties 配置：属性 enabled = true 时才会生效（创建 bean）。或缺省该属性项，也会生效（创建 bean）。
@ConditionalOnProperty(prefix = "java-forge.task-job", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TaskJobAutoConfig {

    private final Logger log = LoggerFactory.getLogger(TaskJobAutoConfig.class);

    /**
     * 创建线程池任务调度器实例，用于执行定时任务和异步任务调度。
     * 注：
     *      若不配置线程池，默认使用 SimpleTaskScheduler，线程数默认为 1（单线程），这会导致多个调度任务不能同时执行。
     *
     */
    @Bean("javaForgeTaskScheduler")
    public TaskScheduler taskScheduler(TaskJobAutoProperties properties) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(properties.getPoolSize());
        scheduler.setThreadNamePrefix(properties.getThreadNamePrefix());
        scheduler.setWaitForTasksToCompleteOnShutdown(properties.isWaitForTasksToCompleteOnShutdown()); // 在容器关闭时，不立即中断正在执行的任务，而是尝试等待它们执行完成
        scheduler.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds()); // 容器关闭时，等待任务执行完成，超时（n 秒）则强制中断
        scheduler.initialize();
        
        log.info("java-forge task-job 任务调度器初始化完成。线程池大小: {}, 线程名前缀: {}",
                properties.getPoolSize(), properties.getThreadNamePrefix());
        
        return scheduler;
    }

    /**
     * ITaskDataProvider 接口由使用者实现，并注册为 bean，可以有多个实例。List<ITaskDataProvider> 会自动收集这些实例。
     * @param javaForgeTaskScheduler 线程池任务调度器实例
     * @param taskDataProviders 任务数据提供者实例
     * @return 任务调度服务实例
     */
    @Bean
    public ITaskJobManager taskJobService(TaskScheduler javaForgeTaskScheduler, List<ITaskDataProvider> taskDataProviders) {
        // 实例化任务并初始化调度
        return new TaskJobManager(javaForgeTaskScheduler, taskDataProviders);
    }

    /**
     * 自动检测任务
     */
    @Bean
    public TaskScheduleDaemon taskJob(TaskJobAutoProperties properties, ITaskJobManager taskJobService) {
        log.info("java-forge task-job 任务调度作业初始化完成。刷新间隔: {}ms, 清理cron: {}", properties.getRefreshInterval(), properties.getCleanInvalidTasksCron());
        return new TaskScheduleDaemon(properties, taskJobService);
    }

}