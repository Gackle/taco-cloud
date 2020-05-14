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
   
# Chapter 4. Securing Spring.

当添加了依赖:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Spring Application 就自动配置了基本的安全验证即 HTTP basic authentication 。默认用户名是 *user* ，密码会在 log file 中出现形如 `Using generated security password: c3bf031f-b649-4e8d-8447-48c2c057282c` 的文本。默认的安全功能包括：

- 全部的 HTTP 请求路径都要求 authentication
- 不需要指定 roles 角色和 authorities 权限
- 没有登陆页面
- authentication 通过 HTTP basic authentication 提供
- 只有一个用户，用户名为 user 

Spring Security 提供了若干种选项来配置 user store ：

- in-memory user store
- JDBC-based user store
- LDAP-backed user store
- custom user detail service

无论你选择何种 user store ，你可以通过 override 掉 `WebSecurityConfigurerAdapter` 配置基类定义的 `confingure()` 方法来配置。

## in-memory user store

对于 in-memory user store 配置如下所示，设计上采用了 建造者模式 ：

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("buzz")
                .password("infinity")
                .authorities("ROLE_USER")
                .and()
                .withUser("woody")
                .password("bullseye")
                .authorities("ROLE_USER");
    }
// 配置了两个用户以及他们的密码以及 authorities 授权
}
```

## JDBC-based user store

上面的配置如果要新增用户和角色，需要改动代码并重新打包发布，使用场景非常有限。大多数情况下我们都会把用户数据维护在一个关系数据库中，这样看来 JDBC-based user store 更加适合。下面的代码则是启用了 JDBC-based user store ：

```java
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    DataSource dataSource; // 必须指定 dataSource ，同时使用了 magic of  autowiring

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource);
    }
}
```

虽然这部分代码可以工作，不过它是假定了你的 data schema 的。从下面的代码 snippet 可以看出 Spring Security 是如何去查找你的用户信息的

```java
// Spring Security 默认的 SQL Query

// 1. 查找用户的用户名、密码以及是否可用
public staic final String DEF_USERS_BY_USERNAME_QUERY = 
    "select username, password, enabled " +
    "from users " + 
    "where username = ?";
// 2. 查找用户被授予的权限
public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY = 
    "select username, authority " + 
    "from authorities " + 
    "where username = ?";
// 3. 查看用户作为组成员被授予的权限
public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY = 
    "select g.id, g.group_name, ga.authority " +
    "from groups g, group_members gm, group_authorities ga " +
    "where gm.username = ? " +
    "and g.id = ga.group_id " +
    "and g.id = gm.group_id";
```

要重写以上默认的语句，你可以使用 `.usersByUsernameQuery(/* SQL 语句*/)`，`authoritiesByUsernameQuery(/* SQL 语句*/)` 以及 `groupAuthoritiesByUsername(/* SQL 语句*/)` 这三个方法。但注意使用这三个方法时要遵守规范约束，即都只接受 username 作为参数，同时返回的结果集的 schema 也必须和默认语句的一致。


除此之外，一般我们在数据库中存储的都不是明文密码，所以就需要我们对 password 进行 encode ，我们可以使用 `passwordEncoder()` 指定一个 password encoder 。

`passwordEncoder()` 方法接受任何实现了 Spring Security `PasswordEncoder` 接口的实现。Spring Security 的加密模块包含了以下的实现：

- BCryptPasswordEncoder 使用 bcrypt 强哈希加密
- NoOpPasswordEncoder 不使用任何编码
- Pbkdf2PasswordEncoder 使用 PBKDF2 加密
- SCryptPasswordEncoder 使用 scrypt 哈希加密
- StandardPasswordEncoder 使用 SHA-256 哈希加密

当然你也可以自己实现这个 `PasswordEncoder` 接口来满足你特定的需求。这个接口也非常简单:

```java
public interface  PasswordEncoder {
    String encode(CharSequence rawPassword);
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
```

注意 `StandardPasswordEncoder` `MessageDigestPasswordEncoder` 以及 `NoOpPasswordEncoder` 都标记为 **Deprecated**

## LDAP-based user store

要配置 Spring Security 的 LDAP-based authentication ，你可以使用 `ldapAuthentication()` 方法，这个方法类似于 `jdbcAuthentication()` ：

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.ldapAuthentication()
        .userSearchBase("ou=people")
        .userSearchFilter("(uid={0})")
        // .groupSearchBase("ou=groups")
        .groupSearchFilter("member={0}");
    // 后两个方法会使用提供的过滤条件作用在 LDAP query 上，用于查找 user 和 group 
    // search 会在 LDAP 的 root 层次上开始，如果要改变这个行为请配置 *SearchBase
}
```

LDAP 默认的鉴权策略是通过绑定一个操作，然后由 LDAP 服务器直接校验 user 。另一个是执行比较操作，这回要求发送密码到 LDAP 目录然后询问服务器比对密码是否正确，因为这部分是在 LDAP 服务器中完成的，所以实际的密码依然是保密的。  
如果你希望自己来做这个密码比对，你可以声明 `passwordCompare()` 方法；默认情况下，login 表单传回来的密码会和 user 的 LDAP entry 的 `userPassword` 属性比对，如果你的 LDAP 将密码保存在别的属性上，可以用 `passwordAttribute()` 指定密码所在的属性名 :

```java
...
.passwordCompare()
.passwordEncoder(new BCrptPasswordEncoder())
.passwordAttribute("passcode");
``` 

注意这里也用上了 `passwordEncoder` 来保证在 server-side 的 password comparison 过程中密码依然保密：输入的密码依然会直接传递给 LDAP ，这个过程有可能会被 hacker 截获，因此需要使用 `passwordEncoder` 来加密后传输，不过要求 LDAP 服务器也要采用同样的方法解密。

最大的问题是，LDAP 服务器应该在哪？Spring Security LDAP authentication 假设 LDAP 服务器监听 `localhost:33389` ，不过如果你的服务器在另外一台机器上，可以使用 `contextSource()` 来配置这个 location ：

```java
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .ldapAuthentication()
        .userSearchBase("ou=people")
        .userSearchFilter("(uid={0})")
        .groupSearchBase("ou=groups")
        .groupSearchFilter("member={0}")
        .passwordCompare()
        .passwordEncoder(new BCryptPasswordEncoder())
        .passwordAttribute("passcode")
        // 返回一个 ContextSourceBuilder
        .contextSource()
        // 通过 url() 指定 location
        .url("ldap://tacocloud.com:389/dc=tacocloud,dc=com");
}
```

如果你没有 LDAP 服务器但是又想使用它的功能，Spring Security 提供了内置的 LDAP 服务器，只要将上面的 `.url` 替换为 `.root("dc=tacocloud, dc=com")` 即可；  
当 LDAP 服务器启动时，他会试图从 classpath 路径的任何 LDIF 文件中加载数据。 LDIF(LDAP Data Interchange Format)是一个普通文本，用标准方式表示 LDAP 数据。每条记录由一行或多行组成，每个都包含一个 `name:value` 键值对。记录由空行分隔；  
如果你不想 Spring 在 classpath 下查找任何 LDIF 文件，可以通过在 `.root()` 之后调用 `ldif(/* LDIF 文件全路径 */)` 方法显式指定 LDIF 文件路径。

## Customizing user authentication

这里给出的用户自定义身份验证服务是利用了 Spring Data JPA 作为持久化，当然你也可以直接使用 JDBC-based user store 。

### 1. 定义 user domain 以及 persistence

这里实现的 `User` domain 实现了 Spring Security 的 `UserDetails` 接口，它提供了框架所需要的某些必要的用户信息，比如这个 user 会获得什么 authorities 以及账号是否 enabled 或者 locked 等，因此 `getAuthorities()` 需要返回用户被授予的 authorities 的 `Collection` ，而形如 `is___Expired()` 的方法则要返回一个 `boolean` 的值。  

定义了 `User` domain 之后，我们可以接着定义 repository interface ，由于使用 JPA ，所以可以考虑继承 `CrudRepository` interface ，同时必须定义一个 `User findByUsername(String username)` 的方法，因为 **user details service** 会用这个方法通过用户名找到用户。

> 由于使用了 CrudRepository 接口，你可以不用提供 interface 的具体实现了。

### 2. 创建一个 user details service.

Spring Security 的 `UserDetailsService` 是相当直截了当的 interface 。

```java UserDetailsService.java
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```

非常简单，它只有唯一的 `loadUserByUsername` 方法，需要传入 username 然后返回 `UserDetails` 对象或者抛出 `UsernameNotFoundException` 异常。由于我们的 `User` domain 已经继承了 `UserDetails` ，因此不用再另外实现这个接口 。

所以我们定义一个 `UserRepositoryUserDetailsService` 类，实现 `UserDetailsService` 接口，同时将 `UserRepository` 注入进来实现 `loadUserByUsername` 方法。

实现了 `UserRepositoryUserDetailsService` 类之后，我们还需要修改 Spring Security 配置，让它启用我们自定义的 UserDetails ：

```java
// 将 PasswordEncoder 注解为 Bean ，那么之后就可以被自动 scan 并依赖注入了
@Bean
public PasswordEncoder encoder() {
    return new StandardPasswordEncoder("53cr3t");
}
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
 auth.userDetailsService(userDetailsService)
     .passwordEncoder(encoder());  // 这里我们对密码进行哈希加密
}
```


## Securing web requests

尽管加了安全限制，但是对于应用来说并不是所有的页面都需要安全配置，因此需要我们定义自己的 security rule 。在 `WebSecurityConfigurerAdapter` 的另一个 `configure(HttpSecurity http)` 方法中，我们可以在 Web 层面上控制安全级别。对于 `HttpSecurity` 你可以配置以下内容：

- 在请求被处理之前要求必须达到某种安全条件 security condition
- 配置一个自定义登录页
- 允许用户登出应用
- 配置跨域伪装请求的保护

参考下面的例子：

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/design", "/orders")
        .hasRole("ROLE_USER")
        .antMatchers("/", "/**").permitAll();
    // 这里调用了 authorizeRequest() 返回一个 ExpressionInterceptUrlRegistry 对象，可以指定 URL 路径或者 pattern 以及对于这些指定的安全要求
    // 这里规定了 /design 和 /orders 需要用户获得 ROLE_USER 的授权
    // 除此之外所有的请求对于所有的用户来说都是允许的 
}
```

注意这些 rule 的顺序非常重要，一般先声明的优先级比后声明的要高。  

`hasRole()` 和 `permitAll()` 只是对于请求路径声明安全性要求的一些组合方法，除此之外还有以下这些：

> 开启了 Spring Security 之后即使配置了 Security rule ， `/h2-console` 依然会存在登不上的情况，主要是因为 h2Console 需要跨域请求，同时 iframe 里面也要配置同源策略。代码如下所示：
>  ```java
> http.csrf().ignoringAntMatchers("/h2-console/**")
>     .and()
>     .headers().frameOptions().sameOrigin()
>  ```


| 方法 | 描述 |
|:---|:---|
| `access(String)` | 当传入的 SpEL expression 计算等价为 `true` 时可以访问 |
| `anonymous()` | 允许匿名用户访问 |
| `authenticated()` | 允许通过验证的用户访问 |
| `denyAll()` | 无条件地拒绝一切访问 |
| `fullyAuthenticated()` | 允许完全通过身份校验的用户访问（不包括通过 remember-me 的用户） |
| `hasAnyAuthority(String...)` | 允许拥有任何给定权限的用户访问 |
| `hasAnyRole(String...)` | 允许任何拥有任何给定角色身份的用户访问 |
| `hasAuthority(String)` | 允许拥有指定权限的用户访问 |
| `hasIpAddress(String)` | 允许指定 IP 地址的请求访问 |
| `hasRole(String)` | 允许拥有指定角色身份的用户访问 | 
| `not()` | 对任何访问方法的判断进行取反 |
| `permitAll()` | 无条件地允许任何访问 |
| `rememberMe()` | 允许通过 remember-me 验证的用户访问 |

> **SpEL** （Spring Expression Language） Spring 表达式语言，在Spring产品组合中，它是表达式计算的基础。它支持在运行时查询和操作对象图，它可以与基于XML和基于注解的Spring配置还有bean定义一起使用。由于它能够在运行时动态分配值，因此可以为我们节省大量Java代码。

为了可以让你更加丰富的定义 Spring requirement ，可以使用 `access()` 方法并提供 SpEL 表达式，为此 Spring Security 还扩充了 SpEL ，提供了几个 security-specifi value ：

| Security expression | description |
|:---|:---|
|`authentication`| 用户的 authentication 对象 |
|`denyAll`| 计算结果总是返回 `false` |
|`hasAnyRole(list of roles)`| 如果用户拥有任何列表中指定的 role 则返回 `true` |
|`hasRole(role)`| 如果用户是指定的 role 则返回 `true` |
|`hasIpAddress(IP address)`| 如果请求来自指定的 IP 地址则返回 `true` |
|`isAnonymous()`| 如果用户是匿名访问则返回 `true` |
|`isAuthenticated()`| 如果用户通过了身份验证则返回 `true` |
|`isFullyAuthenticated()`| 如果用户通过完全身份校验则返回 `true` |
|`isRememberMe()`| 如果用户通过 remember-me 途径通过身份校验则返回 `true` |
|`permitAll`| 计算结果总是返回 `true` |
|`principal` | 用户的 principal 对象 |

下面是一个 `access()` 的例子：

```java
@Override
// 要求身份为 ROLE_USER 且当天是星期二才能访问
protected void configure(HttpSecurity http) throws Exception {
 http.authorizeRequests()
    .antMatchers("/design", "/orders")
    .access("hasRole('ROLE_USER') && " +
            "T(java.util.Calendar).getInstance().get("+
            "T(java.util.Calendar).DAY_OF_WEEK) == " +
            "T(java.util.Calendar).TUESDAY")
    .antMatchers("/", "/**").access("permitAll");
}
```

## Creating a custom login page

我们需要自定义 `/login` 路劲为登录页，因此需要添加这样一个 Controller 。但是由于这个 Controller 只是负责渲染视图，因此可以使用 `WebConfig` 来定义简单的路由：

```java
@Override
public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("home");
    registry.addViewController("/login");
}
```

剩下的工作就是在 Security Config 上配置了，常用的配置如下所示：

```java
.and()
 .formLogin() // 表示自定义登陆页
 .loginPage("/login")  // 登陆页需要访问的 url （get 请求）
 .loginProcessingUrl("/authenticate") // 回传登陆信息的 url （post 请求）
 .usernameParameter("user")  // username 所在的 field name 自定义
 .passwordParameter("pwd")  // password 所在的 field name 自定义
 .defaultSuccessUrl("/design", true)  // 登陆成功返回的页面，后面为传递的位置参数
```

## Logging Out

为了完善整个应用的登入登出，我们需要在 `HttpSecurity` 上补充 logout 的逻辑：

```java
.and()
  .logout()
    .logoutSuccessUrl("/")  // 登出成功返回首页
```

## Preventing CSRF

CSRF 是一种常见的 Web 攻击手段，Spring Security 有内置的针对 CSRF 的保护机制，在 request 的 attribute 中有一个 `_csrf` 的值，包含一个 token 。你可以通过在页面的 POST 上以 hidden 的形式配置这个值以防止 CSRF 攻击：

```html
<input type="hidden" name="_csrf" th:value="${_csrf.token}" />
```

如果使用 Spring MVC 的 JSP tag library 或者 Thymeleaf ，则已经默认内置这一个标签，不需要显式配置，如 Thymeleaf ，只需要给 form 配置了 `th:action` 即可：

```html
<form method="POST" th:action="@{/login}" id="loginForm " >
```

> 注意，开启了 Spring Security 之后 POST 页面默认都由于 CSRF 的保护机制而变成了 403 ，因此对于表单内容可以采用以上做法，或者关闭 csrf 保护： `.and().csrf().disable()` ，也可以单独配置某些页面不需要 CSRF ：
>  ```java
>   http.csrf().ignoringAntMatchers("/h2-console/**")
>  ```

## Knowing your user

在 Controller 上有多种方法获取 authentication User 的信息：

1. 在 Controller 中注入 `Principal` 对象
    ```java
    @PostMapping
    public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, Principal principal) {
        ...
        User user = userRepository.findByUsername(
        principal.getName());
        order.setUser(user);
        ...
    }
    ```

2. 在 Controller 中注入 `Authentication` 对象
    ```java
    @PostMapping
    public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, Authentication authentication) {
       ...
       User user = (User) authentication.getPrincipal();
       order.setUser(user);
       ...
    }
    ```

3. 通过 `SecurityContextHolder` 获得 security context ，一般推荐使用这个，可以在应用的任何地方使用而不仅仅是 controller 的 handler 方法中使用。

    ```java
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = (User) authentication.getPrincipal();
    ```
4. 通过`@AuthenticationPrincipal` 注解方法的参数

    ```java
    @PostMapping
    public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, @AuthenticationPrincipal User user) {
       if (errors.hasErrors()) {
           return "orderForm";
       }
       order.setUser(user);
       orderRepo.save(order);
       sessionStatus.setComplete();
       return "redirect:/";
    }
    ```
