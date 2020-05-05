# Chapter 1. Foundational Spring.

## `<dependencies>` 中 `starter` 的作用

在 POM.xml 文件的 `<dependencies>` 节点中， Spring 相关的包会有 `starter` 的单词。这些都是 Spring Boot starter dependency ，这些 dependency 的特点是它们本身都不包含任何库代码，而是直接从其他库中拉取。这些 starter dependency 有三个主要的好处：

- 你的 build 文件会显著变小而且易于管理，因为你不需要为每个你需要的库声明依赖；
- 你可以专注于你 dependency 所提供的 “能力” 而不是关注每个库的命名。如果你需要开发一个 Web 应用，你会更倾向于使用 web starter dependency 而不是一长串互不相关的 library 清单。
- 你可以从 library 的版本地狱中解脱出来。你可以信任 Spring Boot 所指定的版本，所以你只需要关心使用的 Spring Boot 的版本即可。

## `@SpringBootApplication` 作用

`@SpringBootApplication` 是其他三个注解 annotation 的混合物：

- `@SpringBootConfiguration` 指明这个 class 是一个 configuration class；尽管目前这个 class 里还没有多少配置，不过需要的化你可以添加基于 Java 的 Spring Framework configuration 到这个 class 中。这个 annotation 实际上是 `@Configuration` 这个 annotation 的特例化；
- `@EnableAutoConfiguration` 启用 Spring Boot 的自动配置
- `@ComponentScan` 启用组件扫描。Spring 会自动发现被注解为 `@Component` 、 `@Controller` 、 `@Service` 等其他的 class 并将它们注册到 Spring application context 。

## 样板代码中的 `main()` 函数

`main()` 函数会在 JAR 文件运行时被执行，这里 `main()` 函数会调用一个 `SpringApplication` 类的静态 `run()` 方法，来执行实际的应用程序 bootstrap ，即创建 Spring application context 。传递给 `run` 方法的两个参数，一个是 configuration class ，另一个是命令行参数。  
传递给 `run()` 的 configuration class 不一定和 bootstrap class 相同，不过一般来说这是最方便和最典型的操作。

## Spring Boot Test boilerplate code

在 test source 中我们会看到 `RunWith(SpringRunner.class)` 。 `RunWith` 是 JUnit 的 annotation ，用于提供一个 test runner 来指导 JUnit 来运行一个 test 。可以理解为给 JUnit 提供一个插件来自定义你的测试行为。在这里，给 JUnit 提供了 `SpringRunner` ，这是 Spring 框架提供的 test runner ，用于提供 Spring Application Context 来给到 test 案例运行。  
如果你已经熟悉了 Spring 的 test ，你可能会发现还有一些其他的基于 Spring 的 test class ，例如 `SpringJUnit4-ClassRunner` 。 `SpringRunner` 是 `SpringJUnit4ClassRunner` 的别名，在 Spring 4.3 被引入，同时去除了对特定 JUnit 版本号的依赖。显然这个别名更加易于阅读和拼写。  
修饰 class 的 `@SpringBootTest` annotation 会告诉 JUnit 用 Spring Boot 来 bootstrap 测试用例，可以理解为这个 test class 会在 `main()` 函数中调用 `SpringApplication.run()` 方法。  

# Chapter 2. Integrated Spring.

## @Slf4j

`@Slf4j` 是一个 Lombok 提供的 annotation ，它会在 class 中自动生成一个 SLF4J （Simple Logging Facade for Java， 简单日志门面），从而使得最终用户能够在部署的时候配置自己希望的 logging APIs 实现。这个小小的 annotation 实现的效果和你在 class 里写这一句的作用一样：

```java
private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DesignTacoController.class)
```

## @Controller 

`@Controller` annotation 足以（serve to）让这个 class 被识别为一个 controller ，同时将其标记为组件扫描的候选人，那样 Spring 就会自动发现它并创建一个 class 的实例，作为 bean 加入到 Spring application context 中。

## @RequestMapping

`@RequestMapping` annotation 如果应用在 class 级别，那么就指定这个 controller 全程负责这类 request 。在这里，`DesignTacoController` 会负责处理那些 path 以 `/design` 开头的请求。

