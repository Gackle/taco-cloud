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

# Chapter 3. Working with data

- JDBC (Java Database Connectivity)  
- JPA  (Java Persistence API)

## JDBC 
Spring 通过 `JdbcTemplate` class 提供 JDBC 支持。`JdbcTemplate` 让开发人员可以专注于关系数据库的 SQL 操作而不用编写那些使用 JDBC 时需要的样板代码；

Ingredient 的数据层 repository 需要实现某些方法(`findAll`、`findById`、`save`)，我们定义 `IngredientRepository` 接口，然后 `JdbcIngredientRepository` 类实现这个接口；

`JdbcIngredientRepository` 类被注解为 `@Repository` ，这个 annotation 是由 Spring 定义的一个非常方便的样板 annotation ，涵盖了 `@Controller` 以及 `@Component` 。注解为 `@Repository` 的 class 可以被 Spring component scanning 自动发现并实例化为 bean 添加到 Spring application context 中；  
当 Spring 创建了 `JdbcIngredientRepository` bean ，通过 `@Autowired` 注解他的构造函数，就可以注入 `JdbcTemplate` 。这个构造函数将 `JdbcTemplate` 分配到实例变量上，然后其他方法都可以使用这个变量来进行查询和插入；

`query()` 方法接收 SQL 语句的字符串以及一个 Spring `RowMapper` 接口的实现，后者将应用于结果集返回的每一行数据，把行数据变成对象 object ，此外还可以以数组的形式接收一系列 SQL 参数。  
在这里传递的是 `mapRowTOIngredient()` 方法的引用。Java8 的方法引用以及 lambda 表达式对于 `JdbcTemplate` 来说是相对显式实现 `RowMapper` 的一种更为方便的替代手段。如果你需要显式的 `RowMapper` 可以这样：

``` java
@Override
public Ingredient findById(String Id) {
    return jdbc.queryForObject(
        "select id, name, type from Ingredient where id=?",
        new RowMapper<Ingredient>() {
            public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Ingredient(
                    rs.getString("id"),
                    rs.getString("name"),
                    Ingredient.Type.valueOf(rs.getString("type")));
            };
        }, id);
}
```

对于 `update()` 方法的高级用法：`update` 接受一个 `PreparedStatementCreator` 类型的参数以及一个 `KeyHolder` 类型的参数，其中 `KeyHolder` 会提供生成的 ID ，但是你必须先创建一个 `PreparedStatementCreator` 参数。  
但是创建 `PreparedStatementCreator` 对象的过程并不简单：通常是通过 `PreparedStatementCreatorFactory` 来创建，需要传入你要执行的 SQL 语句字符串以及每个 SQL 中参数的类型。然后对 factory 类调用 `newPreparedStatementCreator()` 方法，传入实际执行 SQL 语句的相关参数，然后就会得到 `PreparedStatementCreator` 对象。   
当得到了 `PreparedStatementCreator` 对象后，你可以传入 `PreparedStatementCreator` 对象和 `KeyHolder` 对象（这里是一个 `GeneratedKeyHolder` 实例）来调用 `update` 方法，一旦 `update` 完成，你就会通过 `keyHolder.getKey().longValue()` 得到 ID 。

除了直接使用 `JdbcTemplate` 之外，对于简单的 update 可以使用 `SimpleJdbcInsert` 对象，例如 `new SimpleJdbcInsert(jdbc).withTableName("Taco_Order").usingGeneratedKeyColumns("id")` 表示插入将发生在表 *Taco_Order* 上同时会由数据库提供或者返回一个 *id* 的值。`SimpleJdbcInserter` 对象有很多方便的方法，比如 `execute` 和 `executeAndReturnKey` 。他们都接受一个 `Map<String, Object>` 参数，其中键表示表中的列名，而值则代表要插入的列值。

> 注意，使用了内置的 H2 数据库的话可以访问 [/h2-console](http://localhost:8080/h2-console) 打开数据库页面，同时 JDBC-URL 的信息会在 Spring Boot 启动时输出，一般为 `jdbc:h2:mem:xxxxx` 

## JPA 

### 添加 JPA 依赖

对于 Spring Boot 项目，Spring Data JPA 可以通过 JPA starter 引入，starter 依赖不仅包含 Spring Data JPA ，同时也会传递性地把 Hibernate 作为 JPA 的实现也引用进来：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
``` 

如果你想要使用其他 JPA 实现，你至少需要 exclude 掉 Hibernate 依赖同时 include 你所选择的 JPA 库。如下使用 EclipseLink 替换掉 Hibernate：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>hibernate-entitymanager</artifactId>
            <groupId>org.hibernate</groupId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.eclipse.persistence</groupId>
    <artifactId>eclipselink</artifactId>
    <version>2.5.2</version>
</dependency>
```

> 当然变更可能不止这么一点，这取决于你所选用的 JPA 实现。

## 将 domain 注解为 entity

为了将 domain 声明为 entity ，需要使用 `@Entity` 注解，同时对于 id 属性必须使用 `@Id` 注解来在数据库上唯一标识 entity 。

除了 JPA-specific annotation 之外，你需要注意在 class level 上也添加了一个 `@NoArgsConstructor` 注解。JPA 要求实体提供无参构造函数，所以 Lombok 的 `@NoArgsConstructor` 可以帮助你实现，当然你也可以不用这个构造函数，所以这里设置了 `access` attribute 为 `AccessLevel.PRIVATE` 。同时因为 `final` 属性需要被设置，因此需要设置 `force` attribute 为 true ，这样 Lombok-generated constructor 会将它们全部默认设置为 null 。

你同时也添加了 `@RequiredArgsConstructor` 注解。因为 `@Data` 隐含地要求是需要有参构造函数，不过由于使用了 `@NoArgsConstructor` ，这个 constructor 会被移除（Lombok 不会生成），因此显式的 `@RequiredArgsConstructor` 确保你除了 private 的无参构造函数外还依然会有一个有参构造函数。

对于 `@generatedValue` ，可以指定 `strategy` 为 `AUTO` ，这样可以依赖数据库让它自动生成 ID 的值。

为了表示两个实体的关系，你可以使用 `@ManyToMany` 或者 `@OneToMany` 等来表示；

对于 `@PrePersist` annotation （例子是用在 `void createdAt` 上），可以在 entity 持久化 (persist) 之前完成完成一部分工作（这里是将 createdAt 设置为当前时间）。

## CrudRepository

在 JDBC 小节里，你需要显式声明你的 repository 提供的方法，但对于 Spring Data ，你可以选择继承 `CrudRepository` 。`CrudRepository` 是一个泛型对象，接受两个参数：前者是需要持久化的 entity type ，后者是 entity ID type 。同时继承 `CrudRepository` 的接口甚至都不需要实现！当应用运行时，Spring Data JPA 会自动生成一个实现！到时候只需要在 Controller 像基于 JDBC 实现一样注入就可以使用。

## 踩过的巨坑

1. JPA 默认会有一套自己生成 Schema 的 DDL ，如果你要使用 `schema.sql + data.sql` 这一套的策略就不能使用 jpa 的 auto-ddl ，因此需要在 *application.properties* 中设置 `spring.jpa.hibernate.ddl-auto=none` 。
2. 关于 auto-ddl 再多说一句，默认在 Spring 2.0 以后不会自动去扫描 resource 目录下的 *schema.sql* 和 *data.sql* ，同样需要在 *application.properties* 设置：
  
    ```dtd
    # schema.sql中一般存放的是建表语句
    spring.datasource.schema = classpath:schema.sql
    # data.sql中一般存放的是需要插入更新等sql语句
    spring.datasource.data =  <classpath:data class="sql"></classpath:data>
    ```
3. 默认情况下 JPA 对 Enum 的设置是读取 Enum 的索引，因此如果你在数据库对于索引字段存储的不是索引值（0,1,2……）而是枚举值（Red, Blue, Green）等，需要在 Enum 字段上做注解:

    ```java
    @Enumerated(EnumType.STRING)
    private final Type type;
    ```
   
4. 对于 `@Id` 的值，上面提到对于 `GenerateValue` 可以使用策略，可选的策略有 `AUTO` `IDENTITY` `TABLE` 和 `SEQUENCE` 不过 AUTO 不是很智能，目前是直接强制转换为 `IDENTITY` 来适配 JDBC 的情况；
5. Hibernate 对于 Entity 映射到列名的命名规则是有自己一套的，也就是说，不管你 entity 命名成什么，一律都按配置的来，这里需要在 *application.properties* 中改配置：

    ```dtd
    # 表示 entity 怎么命名那么实体就怎么命名 
    spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
    # 这种是遇到大写字母加_命名
    # spring.jpa.hibernate.naming.physical-strategy=org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
    ```
6. 对于关联表的处理，以多对多为例，即使在 Entity 中定义了诸如其中的关系，还是要加上 annotation 。多说无益，直接看个例子：

    ```java
    @ManyToMany(targetEntity = Taco.class)
    @JoinTable(name = "Taco_Order_Tacos",
            joinColumns = {@JoinColumn(name = "tacoOrder")},
            inverseJoinColumns = {@JoinColumn(name = "taco")})
    private List<Taco> tacos = new ArrayList<>();
    ```