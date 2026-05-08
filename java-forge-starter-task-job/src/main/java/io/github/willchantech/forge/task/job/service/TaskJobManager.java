package io.github.willchantech.forge.task.job.service;

import io.github.willchantech.forge.task.job.model.TaskHolder;
import io.github.willchantech.forge.task.job.model.TaskScheduleVO;
import io.github.willchantech.forge.task.job.provider.ITaskDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 任务调度服务实现类
 *
 * @author willchan-tech
 */
public class TaskJobManager implements ITaskJobManager, InitializingBean, DisposableBean {

    private final Logger log = LoggerFactory.getLogger(TaskJobManager.class);

    private final TaskScheduler taskScheduler;
    private final List<ITaskDataProvider> taskDataProviders;

    private final ReentrantLock lock = new ReentrantLock(); // 使用 tryLock 防止并发执行（非阻塞获取锁）

    /**
     * 任务ID与任务执行器的映射，用于记录已添加的任务
     */
    private final Map<Long, TaskHolder> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * 新的构造函数，不依赖ITaskExecutor
     */
    public TaskJobManager(TaskScheduler taskScheduler,
                          List<ITaskDataProvider> taskDataProviders) {
        this.taskScheduler = taskScheduler;
        this.taskDataProviders = taskDataProviders;
    }

    /**
     * 初始化任务调度配置
     * 在服务启动时加载所有有效的任务调度配置
     */
    private void initializeTasks() {
        log.info("java-forge task-job 开始初始化任务调度配置");
        try {
            // 聚合所有数据提供者的任务调度配置
            List<TaskScheduleVO> allTaskSchedules = new ArrayList<>();
            //迭代数据提供者
            for (ITaskDataProvider provider : taskDataProviders) {
                //查询所有的有效任务调度配置
                List<TaskScheduleVO> taskSchedules = provider.queryAllValidTaskSchedule();
                if (taskSchedules != null) {
                    allTaskSchedules.addAll(taskSchedules);
                }
            }
            
            // 处理每个任务调度配置
            for (TaskScheduleVO task : allTaskSchedules) {
                // 创建并调度新任务
                scheduleTask(task);
            }
            
            log.info("java-forge task-job 任务调度配置初始化完成，已加载任务数: {}", scheduledTasks.size());
        } catch (Exception e) {
            log.error("java-forge task-job 初始化任务调度配置时发生错误", e);
        }
    }

    @Override
    public boolean addTask(TaskScheduleVO task) {
        try {
            if (task == null || task.getId() == null) {
                log.warn("java-forge task-job 任务配置为空或任务ID为空，无法添加任务");
                return false;
            }

            // 如果任务已存在，先移除旧任务
            if (scheduledTasks.containsKey(task.getId())) {
                log.info("java-forge task-job 任务已存在，先移除旧任务，ID: {}", task.getId());
                removeTask(task.getId(), false);
            }

            // 调度新任务
            scheduleTask(task);

            log.info("java-forge task-job 任务添加成功，ID: {}, 描述: {}", task.getId(), task.getDescription());
            return true;
        } catch (Exception e) {
            log.error("java-forge task-job 添加任务时发生错误，ID: {}", task != null ? task.getId() : "null", e);
            return false;
        }
    }

    @Override
    public boolean removeTask(Long taskId, boolean force) {
        try {
            if (taskId == null) {
                log.warn("java-forge task-job 任务ID为空，无法移除任务");
                return false;
            }
            TaskHolder holder = scheduledTasks.remove(taskId);//从 map 移除并返回被移除的对象
            if (holder != null) {
                ScheduledFuture<?> future = holder.getFuture();
                if (future != null) {
                    // 参数 false： 停止后续调度，但不打断正在执行的任务。参数 true：立即中断正在执行的任务，停止后续调度。
                    future.cancel(force);
                    log.info("java-forge task-job 任务移除成功，ID: {}", taskId);
                }
                return true;
            } else {
                log.warn("java-forge task-job 未找到要移除的任务，ID: {}", taskId);
                return false;
            }
        } catch (Exception e) {
            log.error("java-forge task-job 移除任务时发生错误，ID: {}", taskId, e);
            return false;
        }
    }

    /**
     * 调度单个任务
     */
    private void scheduleTask(TaskScheduleVO task) {
        try {
            log.info("java-forge task-job 开始调度任务，ID: {}, 描述: {}, Cron表达式: {}", task.getId(), task.getDescription(), task.getCronExpression());
            // 把任务“注册到调度器”，等时间到了再执行
            ScheduledFuture<?> future = taskScheduler.schedule(
                    () -> executeTaskWithFunction(task),
                    new CronTrigger(task.getCronExpression())
            );
            // 记录 ScheduledFuture，该对象以后可以控制任务的取消
            scheduledTasks.put(task.getId(), new TaskHolder( future, task.getCronExpression()));

            log.info("java-forge task-job 任务调度成功（函数式），ID: {}", task.getId());
        } catch (Exception e) {
            log.error("java-forge task-job 调度任务时发生错误，ID: {}", task.getId(), e);
        }
    }

    /**
     * 使用函数式编程方式执行任务
     */
    private void executeTaskWithFunction(TaskScheduleVO task) {
        try {
            log.info("java-forge task-job 开始执行任务（函数式），ID: {}, 描述: {}", task.getId(), task.getDescription());

            // 获取并执行任务
            Runnable taskRunnable = task.getTaskExecutor().get();
            taskRunnable.run();

            log.info("java-forge task-job 任务执行完成（函数式），ID: {}", task.getId());
        } catch (Exception e) {
            log.error("java-forge task-job 执行任务时发生错误（函数式），ID: {}", task.getId(), e);
        }
    }
    
    @Override
    public void refreshTasks() {
        // 从稳固工程的角度看，刷新任务调度是只允许一个线程来执行的，因此加锁很有必要，防范风险。
        if (!lock.tryLock()) {
            log.debug("java-forge task-job 任务刷新正在执行，跳过本次触发。");
            return;
        }
        log.info("java-forge task-job 开始刷新任务调度配置（动态更新）");
        try {
            // 聚合所有数据提供者的任务调度配置
            List<TaskScheduleVO> allTaskSchedules = new ArrayList<>();
            for (ITaskDataProvider provider : taskDataProviders) {
                List<TaskScheduleVO> taskSchedules = provider.queryAllValidTaskSchedule();
                if (taskSchedules != null) {
                    allTaskSchedules.addAll(taskSchedules);
                }
            }

            // 记录当前配置中的任务ID
            Set<Long> currentTaskIds = new HashSet<>();

            // 处理每个任务调度配置
            for (TaskScheduleVO task : allTaskSchedules) {
                Long taskId = task.getId();
                currentTaskIds.add(taskId);

                // 如果任务已经存在，则判断 Cron 表达式是否变化
                TaskHolder existing = scheduledTasks.get(taskId);
                if (existing != null) {
                    String oldCron = existing.getCronExpression();
                    // 判断 Cron 表达式是否变化
                    if (oldCron != null && !oldCron.equals(task.getCronExpression())) {
                        existing.getFuture().cancel(true); //强制中断，避免和更新后的任务并行运行
                        //更新任务
                        scheduleTask(task);
                    }
                    continue;
                }

                // 任务不存在，则创建并调度新任务
                scheduleTask(task);
            }

            // 移除已不存在的任务
            scheduledTasks.keySet().removeIf(taskId -> {
                if (!currentTaskIds.contains(taskId)) {
                    ScheduledFuture<?> future = scheduledTasks.get(taskId).getFuture();
                    if (future != null) {
                        // 参数 false： 停止后续调度，但不打断正在执行的任务。参数 true：立即中断正在执行的任务，停止后续调度。
                        future.cancel(false);
                        log.info("java-forge task-job 已移除任务，ID: {}", taskId);
                    }
                    return true; // 交给 removeIf 删除
                }
                return false;
            });

            log.info("java-forge task-job 任务调度配置刷新完成，当前活跃任务数: {}", scheduledTasks.size());
        } catch (Exception e) {
            log.error("java-forge task-job 刷新任务调度配置时发生错误", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stopAllTasks() {
        log.info("java-forge task-job 开始停止所有任务");
        scheduledTasks.forEach((id, holder) -> {
            if (holder != null) {
                ScheduledFuture<?> future = holder.getFuture();
                if (future != null) {
                    // 参数 false： 停止后续调度，但不打断正在执行的任务。参数 true：立即中断正在执行的任务，停止后续调度。
                    future.cancel(false);
                    log.info("java-forge task-job 已取消任务，ID: {}", id);
                }
            }
        });
        scheduledTasks.clear();
        log.info("java-forge task-job 所有任务已停止");
    }

    /**
     * 获取当前任务数量
     * @return 任务数量
     */
    @Override
    public int getActiveTaskCount() {
        return scheduledTasks.size();
    }

    /**
     * 实现DisposableBean接口：
     *      当 Spring 容器关闭时，执行“收尾清理逻辑”。
     */
    @Override
    public void destroy() {
        stopAllTasks();
    }

    /**
     * Bean 初始化完成后自动执行
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        initializeTasks();
    }
}