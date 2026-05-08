# 🚀 Java-Forge

> 一个基于 Spring Boot Starter 的轻量级增强框架，聚焦于提升后端系统的**可扩展性、可维护性与工程效率**

---

## 📌 项目简介

**Java-Forge** 是一个个人开发的模块化框架，基于 Spring Boot Starter 自动装配机制构建，旨在解决中小型项目中常见的工程问题。目前包含 5 大模块：

- **配置动态化**
- **日志安全（脱敏）**
- **接口限流**
- **任务调度**
- **常用设计模式**

项目强调：

- **低侵入性**
- **高扩展性**
- **开箱即用**

---

## 🧱 核心模块

### 1️⃣ 动态配置中心（Dynamic Config Center）

支持在**不重启应用的情况下动态修改配置**

**特性：**

- 支持配置热更新
- 基于 `BeanPostProcessor` 实现自动注入
- 支持自定义配置源扩展

**使用场景：**

- 动态开关（Feature Toggle）
- 灰度发布
- 运行时参数调整

---

### 2️⃣ 日志脱敏（Log Masking）

对敏感信息进行自动脱敏处理，避免日志泄露风险。支持 2 种日志框架， 4 种脱敏类型，可覆盖脱敏规则，也可扩展自定义类型

**日志类型：**

- logback
- log4j2

**脱敏类型：**

- 手机号
- 邮箱
- 身份证
- 护照

**示例：**

```text
手机号：13812345678 → 脱敏后：138****5678
邮箱：zhangsan88@qq.com → 脱敏后：z***@qq.com
身份证：440103199001011234 → 脱敏后： 4401**********1234
护照：E12345678 → 脱敏后： E****5678
```
**特点：**
- 基于正则 + 可扩展策略
- 可覆盖默认策略
- 支持边界污染防护

---

### 3️⃣ 限流模块（Rate Limiter）

用于保护系统接口，防止高并发下的流量冲击或恶意攻击

**支持策略：**

- 全局限流
- IP 限流（黑名单机制）
- 自定义 Key 限流（黑名单机制）

**特点：**
- 使用简单，仅需几个注解和参数设置。
- 插拔式的限流策略，可任意搭配策略一起使用，灵活性强
- 易扩展新的限流维度

---

### 4️⃣ 任务调度系统（Task Scheduler）

轻量级任务调度实现，支持**动态任务管理**

**能力：**

- 动态注册任务
- 动态更新任务 cron 表达式
- 动态删除任务

**设计优势：**
- 全量对齐任务
- 解耦任务类型与执行逻辑
- 支持灵活扩展任务类型

---

### 5️⃣ 设计模式基础框架（Design Patterns）

提供多种设计模式基础框架，包含三种责任链模式，一种规则树模式

**使用场景：**

- 项目流程解耦，扩展
- 增强项目代码架构的弹性
- 协调代码规范管理

---

## 📦 快速开始

### 1. 引入依赖

项目实现了标准 BOM（Bill of Materials）依赖导入，可以先导入 bom 包，统一版本管理

```xml
<dependency>
    <groupId>io.github.willchantech.forge</groupId>
    <artifactId>java-forge-bom</artifactId>
    <version>1.0.0</version>
</dependency>
```

然后，按需导入其他功能模块（省略 version）

```xml
<!-- java-forge dcc 动态配置中心 -->
<dependency>
  <groupId>io.github.willchantech.forge</groupId>
  <artifactId>java-forge-starter-dynamic-config-center</artifactId>
</dependency>

<!-- java-forge design framework 设计模式 -->
<dependency>
  <groupId>io.github.willchantech.forge</groupId>
  <artifactId>java-forge-starter-design-framework</artifactId>
</dependency>

<!-- java-forge rate limiter 限流 -->
<dependency>
  <groupId>io.github.willchantech.forge</groupId>
  <artifactId>java-forge-starter-rate-limiter</artifactId>
</dependency>

<!-- java-forge log mask 日志脱敏 -->
<dependency>
  <groupId>io.github.willchantech.forge</groupId>
  <artifactId>java-forge-starter-log-masking</artifactId>
</dependency>

<!-- java-forge task job 任务调度 -->
<dependency>
  <groupId>io.github.willchantech.forge</groupId>
  <artifactId>java-forge-starter-task-job</artifactId>
</dependency>
```
---

### 🟥 动态配置中心（Dynamic Config Center）

#### Spring Boot yml 配置文件：
```yaml
java-forge:
  dynamic-config:
    # 不同的项目，system 应该不同。这是用来区分不同的消息主题上下文，避免冲突。
    system: [system name]
    redis:
      # redis host
      host: [your host]
      # redis port
      port: [your port]
```   
然后，在需要被动态配置的类（spring 注册类）字段上面添加注解 @DCCConfig:
```
    @DCCValue("toggle:open") // [key]:[value] 格式，key 可以随意填写，只要不冲突即可。
    private String toggle;
```

这样的话，当项目启动后，字符串字段 toggle 会被注入 "open" 值。

#### 动态修改值：

在项目不重启的情况下动态修改配置，需要给 redis 发送主题消息：
```java
@Resource
private RTopic dynamicConfigCenterRedisTopic;

@Test
public void dynamicConfig() {
    // 推送
    dynamicConfigCenterRedisTopic.publish(new AttributeVO("toggle", "off"));
}
```
---

### 🟥 日志脱敏（Log Masking）

#### 首先，在 Spring Boot yml 配置文件中启用 “脱敏” 开关：
```yaml
java-forge:
  # 日志脱敏配置，默认所有的属性值是 false（关闭），需要手动开启。
  log-mask:
    enabled: true # 总开关
    phone: true
    email: true
    idCard: true
    passport: true
```
#### 接入 logback 日志框架：
logback 的 xml 配置文件中添加一个 conversionRule 标签:
```xml
<conversionRule conversionWord="mask" converterClass="io.github.willchantech.forge.log.masking.adapter.logback.LogbackMaskingConverter"/>
```
然后，在 pattern 标签中将日志变量替换成 %mask，例如：
```xml
<pattern>%d{yy-MM-dd.HH:mm:ss.SSS} [%-16t] %-5p %-22c{0}%X{ServiceId} -%X{trace-id} %mask%n</pattern>
```
#### 接入 log4j2 日志框架：
log4j2 的 xml 配置文件：
```xml
<Configuration packages="io.github.willchantech.javaforge.logmask.adapter.log4j2">

    <Appenders>

        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d %p %c - %msg%n"/>
        </Console>

        <Rewrite name="MaskRewrite">
            <LogMaskRewritePolicy/>
            <AppenderRef ref="Console"/>
        </Rewrite>

    </Appenders>

    <Loggers>
        <Root level="info">
            <AppenderRef ref="MaskRewrite"/>
        </Root>
    </Loggers>

</Configuration>
```
#### 重写脱敏策略：

log-mask 模块提供了 4 种默认脱敏策略，你可以重写其中的策略。分别有 4 个类：AbstractEmailMaskRule， AbstractIdCardMaskRule， AbstractPhoneMaskRule， AbstractPassportMaskRule。例如：

```java
@Component
public class MyEmailMaskRule extends AbstractEmailMaskRule {
    /**
     * 日志脱敏规则
     * @param text 日志内容（不保证是原生日志，有可能是经过其他脱敏策略过滤后的日志）
     * @return 脱敏后的日志内容
     */
    @Override
    public String mask(String text) {
        return text.replaceAll(
                "([a-zA-Z0-9._%+-]{1})[a-zA-Z0-9._%+-]*(@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})",
                "$1呼呼呼$2"
        );
    }
}
```
你也可以重写 supports 方法，这样的话，脱敏开关就会由你的代码来控制，而不是来自配置文件。
```java
@Component
public class MyEmailMaskRule extends AbstractEmailMaskRule {
    private boolean toggle; // true 表示开启，false 表示关闭
    /**
     * 日志脱敏规则
     * @param text 日志内容（不保证是原生日志，有可能是经过其他脱敏策略过滤后的日志）
     * @return 脱敏后的日志内容
     */
    @Override
    public String mask(String text) {
        return text.replaceAll(
                "([a-zA-Z0-9._%+-]{1})[a-zA-Z0-9._%+-]*(@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})",
                "$1呼呼呼$2"
        );
    }
    /**
     * 脱敏开关是否开启。若不重写 supports 方法，则脱敏开关由配置文件控制。
     * @param flags 所有脱敏开关（来自配置文件）的位运算值之和
     * @return true 启用，false 禁用
     */
    @Override
    public boolean supports(long flags) { // flags 是所有脱敏开关（来自配置文件）的位运算值之和
        return toggle;
    }
}
```
#### 扩展脱敏策略：
如果你想定义一个全新的脱敏策略，你可以实现 MaskRule 接口。例如：
```java
public class MyMaskRule implements MaskRule {
    private boolean toggle; // true 表示开启，false 表示关闭

    /**
     * 日志脱敏规则
     * @param text 日志内容（不保证是原生日志，有可能是经过其他脱敏策略过滤后的日志）
     * @return 脱敏后的日志内容
     */
    @Override
    public String mask(String text) {
        // 正则表达式
        String regex = "";
        return regex;
    }

    /**
     * 脱敏开关是否开启。
     * 可以不重写 supports 方法，默认是 true（开启）的。
     * @param flags 所有脱敏开关（来自配置文件）的位运算值之和
     * @return true 启用，false 禁用
     */
    @Override
    public boolean supports(long flags) {
        return toggle;
    }
}
```
日志开关配置也可以配合 **动态配置中心（Dynamic Config Center）** 一起使用。这样的话，你就能得到一个可以动态开启关闭的日志脱敏功能。

---

### 🟥 限流模块（Rate Limiter）

Rate Limiter 模块有三大限流策略：global rate limiter（全局限流），IP rate limiter （IP 限流），key rate limiter（自定义 key 限流）。它们的拦截顺序具有优先级别，全局限流最优先， 然后是 IP 限流，最后是自定义 key 限流。你可以只使用其中任意一个或任意数个限流策略组合，但优先级顺序不变。

**被某个限流策略触发了拦截，随即返回，不会继续执行后面的限流策略**。

只有全局限流策略需要 Spring Boot yml 配置：
```yaml
java-forge:
    # 限流配置
    rate-limiter:
      global:
        permits-per-second: 2 # 每秒允许的请求数
```
使用上很简单，Rate Limiter 模块提供了 3 个限流策略注解，@GlobalLimiterRule，@KeyLimiterRule，@RequestMapping。例如：
```java
@GlobalLimiterRule
@IPLimiterRule(permitsPerSecond = 2, blacklistCount = 2)
@KeyLimiterRule(key = "userId", permitsPerSecond = 2, blacklistCount = 2)
@RequestMapping(value = "draw", method = RequestMethod.GET)
public String draw(String userId) {
    return "test";
}
```
- @GlobalLimiterRule 注解没有参数，统一配置在 Spring Boot yml 文件中，作用于所有的 @GlobalLimiterRule 注解。
- @IPLimiterRule 注解有独立参数。permitsPerSecond 表示每秒允许的请求数，超过阈值即被拦截。客户端 IP 作为黑名单上的记账名，被拦截一次记账 +1。blacklistCount 表示黑名单记账阈值，达到阈值就会进入黑名单，默认 24 小时后解封。
- @KeyLimiterRule 注解有独立参数。key 表示自定义 key，被检测的字段，作为黑名单上的记账名，被拦截一次记账 +1。permitsPerSecond 表示每秒允许的请求数，超过该阈值即被拦截。blacklistCount 表示当被记账（拦截）的次数达到该阈值时，就会进入黑名单，默认 24 小时后解封。

注意，当贴上 @KeyLimiterRule 注解后，被拦截的方法上如果有多个入参且第一个参数为字符串，则只取第一个参数用作匹配 key 的检测。此外，将会迭代所有的入参，直到找到匹配 key 的字段。

#### 被限流拦截后的处理：

当系统触发了限流拦截，将会向上抛出 RateLimiterException 异常对象。该对象提供了三种不同类型的错误码提示：
- type: GLOBAL_LIMIT（超频次访问）
- type: IP_LIMIT, code: 001（超频次访问） / 002（黑名单）
- type: KEY_LIMIT, code: 001（超频次访问） / 002（黑名单）

你可以通过拦截 RateLimiterException 异常来进行一个拦截后处理：
```java
@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(RateLimiterException.class)
    public ResponseEntity<?> handle(RateLimiterException e) {
        // ip 类型
        if (e.getType() == RateLimiterRuleType.IP) {
            String msg = "";
            if (StringUtils.equals(RateLimiterTreatmentType.BLACKLIST_LIMIT.getCode(), e.getCode())) {
                msg = "IP访问过于频繁，请稍后再试。";
            } else {
                msg = "IP访问过于频繁。";
            }
            return ResponseEntity.status(429)
                    .body(msg);
        }
        // key 类型
        if (e.getType() == RateLimiterRuleType.KEY) {
            String msg = "";
            if (StringUtils.equals(RateLimiterTreatmentType.BLACKLIST_LIMIT.getCode(), e.getCode())) {
                msg = "用户请求过快，请稍后再试。";
            } else {
                msg = "用户请求过快。";
            }
            return ResponseEntity.status(429)
                    .body(msg);
        }
        // global 类型
        return ResponseEntity.status(429)
                .body("系统繁忙，请稍后再试。");
    }
}
```

---

### 🟥 任务调度系统（Task Job）
当你有一些需要定时或规律性执行的代码，你就可以使用 Task Job 模块。以一个任务作为最小的执行单元，你可以同时提供多个不同执行时间的任务。Task Job 模块的特别之处在于，采取全量对齐的方式来定时监测所有任务的变动，因此你可以动态的变更，增加或删除任务。

#### Spring Boot yml 配置文件：
```yml
java-forge:
  task-job:
      enabled: true # 是否开启定时刷新任务
      refresh-interval: 30000 # 刷新任务间隔（ms）
      pool-size: 5 # 任务线程池大小
      thread-name-prefix: "test-task-scheduler-" # 任务线程名称前缀
      wait-for-tasks-to-complete-on-shutdown: true # 是否等待任务执行完成再关闭容器
      await-termination-seconds: 30 # 容器关闭时，等待任务执行完成，超时（n 秒）则强制中断
```
#### 提供任务
你需要提供一个 TaskDataProvider（提供的数量不限）, 该类需要实现 ITaskDataProvider 接口。TaskScheduleVO 类封装了任务信息，包括任务ID，任务描述，任务执行表达式，任务参数，任务逻辑，作为任务的一个单元。

queryAllValidTaskSchedule 方法需要返回一个 TaskScheduleVO 集合，Task Job 模块会定时刷新任务的变动。你可以修改 yml 配置文件里的 enabled 属性，启动或关闭定时检测。或者修改 refresh-interval 属性来改变任务刷新的间隔。

不同的任务的 ID 必须唯一。全量对齐任务是 Task Job 模块的最大特色。删除一个任务，意味着你需要把该任务从 List 中剔除；添加一个任务，直接往 List 中添加即可。修改一个任务，你需要把该任务从 List 中剔除，然后添加一个新的任务（具有新的 ID），这样才能被 Task Job 模块识别到任务列表的变更。也就是说，你无法去修改一个任务中的描述，任务参数，任务逻辑，因为修改这些不会被识别，但有一种情况例外，就是修改任务的执行表达式。
```java
@Service
public class TestTaskDataProvider01 implements ITaskDataProvider { // 你可以创建多个任务数据提供者
    
    @Override
    public List<TaskScheduleVO> queryAllValidTaskSchedule() {
        List<TaskScheduleVO> tasks = new ArrayList<>();
        
        // 任务1
        TaskScheduleVO task1 = new TaskScheduleVO();
        task1.setId(1L);
        task1.setDescription("测试任务1 - 数据处理");
        task1.setCronExpression("0/10 * * * * ?"); // 每10秒执行一次
        task1.setTaskParam("{\"type\":\"data_process\",\"batch_size\":100}");
        
        // 使用BiConsumer方式设置任务逻辑
        BiConsumer<Long, String> task1Logic = (taskId, taskParam) -> {
            try {
                Thread.sleep(500); // 模拟任务执行时间
                log.info("测试任务1执行完成");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("测试任务1执行被中断", e);
            }
        };
        task1.buildTaskLogic(task1Logic);
        tasks.add(task1);
        // 任务2
        TaskScheduleVO task2 = new TaskScheduleVO();
        task2.setId(2L);
        task2.setDescription("测试任务2 - 报表生成");
        task2.setCronExpression("0/5 * * * * ?"); // 每5秒钟执行一次
        task2.setTaskParam("{\"report_type\":\"daily\",\"format\":\"pdf\"}");

        // 使用BiConsumer方式设置任务逻辑
        BiConsumer<Long, String> task2Logic = (taskId, taskParam) -> {
            try {
                Thread.sleep(1000); // 模拟耗时操作
                log.info("测试任务2执行完成");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("测试任务2执行被中断", e);
            }
        };
        task2.buildTaskLogic(task2Logic);
        tasks.add(task2);
        
        return tasks;
    }

}
```
#### 任务管理
TaskJobManger 类封装了任务管理功能。你可以通过 TaskJobManger 类来添加、移除、刷新、停止任务调度配置，并获取当前活跃任务数量。
```java
public interface ITaskJobManger {

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
```
引入 TaskJobManger
```java
@Resource
private ITaskJobManager taskJobManager;
```
