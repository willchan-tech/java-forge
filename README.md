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

对敏感信息进行自动脱敏处理，避免日志泄露风险。支持 4 种脱敏类型，可覆盖脱敏规则，也可自定义扩展类型

**已有类型：**

- 手机号
- 邮箱
- 身份证
- 护照

**示例：**

```text
原始日志：
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
- 动态支持任务新增 / 更新

**设计优势：**
- 解耦任务类型与执行逻辑
- 支持灵活扩展任务类型

---

### 5️⃣ 设计模式基础框架（Design Patterns）

提供多种设计模式基础框架，包含三种责任链模式，规则树模式

**使用场景：**

- 项目流程解耦，扩展
- 增强项目代码架构的弹性
- 协调代码规范管理

---

## 📦 快速开始

### 1. 引入依赖

项目实现了标准 BOM（Bill of Materials）依赖导入，可以先导入 bom 包，实现统一版本管理

```
  <dependency>
    <groupId>io.github.willchantech.forge</groupId>
    <artifactId>java-forge-bom</artifactId>
    <version>1.0.0</version>
  </dependency>
```

然后，按需导入其他功能模块（省略 version）

```
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
