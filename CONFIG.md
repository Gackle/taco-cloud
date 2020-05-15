# 自定义配置相关

## 1. 自定义端口号

```yaml
server:
  port: 9090  # 指定开放端口号为 9090
```

## 2. 使用随机端口号

```yaml
server:
  port: 0   # Spring Boot 会随机选择一个非占用的端口使用，这有利于避免端口冲突导致应用 crash
```

## 3. 配置 HTTPS 访问

要配置使用 HTTPS ，首先要先生成 keystore ，可以使用 JDK 的 `keytool` 命令行工具:

```shell script
$ keytool -keystore mykeys.jks -genkey -alias tomcat -keyalg RSA
```

> 记得过程中设置的密钥库口令

通常会推荐转为 PKCS12 的行业标准格式:

```shell script
$ keytool -importkeystore -srckeystore mykeys.jks -destkeystore mykeys.jks -deststoretype pkcs12
```

旧的 *mykeys.jks* 会备份为 *mykeys.jks.old* 。

```yaml
server:
  port: 8443  # 开发常用的 HTTPS 开放端口
  ssl:
    key-store: file:///path/to/mykeys.jks   # 如果需要打包进应用的 JAR 文件，可以使用 `classpath:` URL 来引用它
    key-store-password: letmein
    key-password: letmein
```

## 4. 配置 dataSource

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tacocloud
    username: tacodb
    password: tacopassword
    driver-class-name: com.mysql.cj.jdbc.Driver
    schema:
    - order-schema.sql
    - ingredient-schema.sql
    - taco-schema.sql
    - user-schema.sql
    data:
    - ingredients.sql
```

## 5. 配置日志

通常情况下，Spring Boot 会通过 `Logback` 配置日志，将 INFO 级别的日志写到控制台上。  
如果你需要完全控制你的日志配置，你需要在在 `classpath` 的 root 路径下（`src/main/resources`）创建一个 **logback.xml** 文件：

```xml logback.xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>
                %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
            </pattern>
        </encoder>
    </appender>
    <logger name="root" level="INFO" />
    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

> 关于 `logback.xml` 的编写不在此讨论范围，详细请查阅 Logback 的文档。

对于 Spring Boot 来说，可以通过 configuration properties 而不是 logback.xml 来改变日志行为。  
如果要设置日志级别，你可以 create 一个 properties 以 `logging.level` 开头，紧接着你想修改其日志级别的 logger 的命名。在本例中，假设你要将默认记录 DEBUG 日志的 root logger 改为 WARN 级别日志：

```yaml
logging:
  path: ~/logs/
  file: TacoCloud.log
  level:
    root: WARN
    org:
      springframework:
        security: DEBUG
    # org.springframework.security: DEBUG # 可以将上述层次结构 flat 掉  
```

> path 和 file properties 可以让你将日志记录到文件上，此外日志文件会在达到 10MB 大小的时候进行 rotate 。


## 6. 自定义配置

`@ConfigurationProperties(/* prefix */)` 注解的 Bean 可以获取到 Spring Configuration 的值，具体请参考 `OrderProps` class 的使用

## 7. 多环境下不同的 Configuration 

一般来说，对于不同环境需要不同的 Configuration （例如 database connection）我们不会固定在 application.properties 或者 application.yml 而是通过 environmental variable 的形式配置。但这种情况在需要配置较多的情况下不方便处理，其次如果出现问题无法快速回滚，而且这些配置大多数情况下难以查看。

相较而言 Spring profile 会是一个更好的选择。

有两种方法定义 Profile Properties:
1. 除了 application.yml(或 application.properties )之外再建立一个 `application-{profile-name}.yml/properties` 文件表示对应 profile 的配置（只需要编辑需要变更的配置）
2. 用同一份 application.yml/properties ，但是用 `---` 分隔为多个 section ，除了第一个 section 外，其余的都要补充 `spring.profiles: {profile-name}` 的配置。

那么怎么才能激活指定的 profile 呢？这里又有三种途径：

1. 最坏的办法，直接在 application.yml 中指定：

    ```yaml
    spring:
      profiles:
        active:
          - prod
    ```
2. 通过环境变量指定：`% export SPRING_PROFILES_ACTIVE=prod`
3. 如果通过可执行 JAR 文件执行，可以设置 `active profile` 的命令行参数：

    ```shell script
    % java -jar taco-cloud.jar --spring.profiles.active=prod
    ```
   
> 可以同时指定激活多个 profiles，对于 yml 文件，即 `- {profile name}` 多行多个；对于环境变量和可执行文件命令行参数，则使用 `,` 分隔。

## 8. 根据 profiles 来创建 Bean

所有在 Java configuration class 中声明的 bean 都会被创建。对于某些情况下你希望只有在特定的 profiles 下才创建 bean ，那么可以使用 `@Profile` annotation 。如下的例子：

```java
@Bean
@Profile("dev")
// @Profile({"dev", "qa"}) // 如果 dev 或 qa profile 被激活
// @Profile("!prod") // 除了 prod profile 以外都能被激活
// @Profile({"!prod", "!qa"}) // 在既不是 prod 也不是 qa 的 profile 下被激活
public CommandLineRunner dataLoader(IngredientRepository repo, userRepository userRepo, PasswordEncoder encoder) {
    ...
}
```
