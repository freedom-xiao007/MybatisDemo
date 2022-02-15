# Mybatis Demo
***
## 简介
用于学习Mybatis，分析其源码，并尝试写写自己的Mybatis框架

## 自写的Demo框架展示
通过简单的Mapper接口定时即可使用：

```java
public interface PersonMapper {

    @Select("select * from person")
    List<Person> list();

    @Insert("insert into person (id, name) values ('1', '1')")
    void save();
}
```

测试代码：

```java
public class SelfMybatisTest {

    @Test
    public void test() {
        try(SelfSqlSession session = buildSqlSessionFactory()) {
            PersonMapper personMapper = session.getMapper(PersonMapper.class);
            personMapper.save();
            List<Person> personList = personMapper.list();
            for (Object person: personList) {
                System.out.println(person.toString());
            }
        }
    }

    public static SelfSqlSession buildSqlSessionFactory() {
        String JDBC_DRIVER = "org.h2.Driver";
        String DB_URL = "jdbc:h2:file:./testDb";
        String USER = "sa";
        String PASS = "";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.setDriverClassName(JDBC_DRIVER);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        DataSource dataSource = new HikariDataSource(config);

        SelfConfiguration configuration = new SelfConfiguration(dataSource);
        configuration.addMapper(PersonMapper.class);
        return new SelfSqlSession(configuration);
    }
}
```

最终的输出：

```text
add sql source: mapper.mapper.PersonMapper.list
add sql source: mapper.mapper.PersonMapper.save
executor
executor
Person(id=1, name=1)
```

## 工程运行说明
example 模块是MyBatis3的运行尝试对应，其中的例子用于源码分析debug

SelfMybatis 模块是自定义的 MyBatis Demo，用了一点时间实现了基本的功能

### SelfMybatis运行说明
目前采用的是H2 文件存储模式，运行需要下面步骤：

- 运行example模块中测试代码：MyBatisTest,生成数据库文件
  - testDb.mv.db
  - testDb.trace.db
- 将上面的两个文件移动到SelfMybatis跟目录下
- 运行SelfMybatisTest测试即可

## MyBatis Demo 编写规划
- [x] SQLSessionFactory 整合数据源：提供无参数无返回的SQL执行
  - 自定义 SQLSessionFactory：能获取Mapper
  - 自定义 MapperProxy：能执行无参无返回SQL
- [x] 结果处理

## MyBatis学习总结
- [Mybatis3 源码解析系列](https://juejin.cn/post/7065059747568812040/)

## 解析文章目录
- [MyBatis3源码解析（1）探索准备](https://juejin.cn/post/7058354949209456653)
- [MyBatis3源码解析(2)数据库连接](https://juejin.cn/post/7061031527001358349)
- [MyBatis3源码解析(3)查询语句执行](https://juejin.cn/post/7061427063793647647/)
- [MyBatis3源码解析(4)参数解析](https://juejin.cn/post/7061763240501444615)
- [MyBatis3源码解析(5)查询结果处理](https://juejin.cn/post/7062333998348894244/)
- [MyBatis3源码解析(6)TypeHandler使用](https://juejin.cn/post/7062858058535272478/)
- [MyBatis3源码解析(7)TypeHandler注册与获取](https://juejin.cn/post/7063234640848519175/)
- [MyBatis3源码解析(8)MyBatis与Spring的结合](https://juejin.cn/post/7063649335686201381/)

## Demo 编写
完整的工程已放到GitHub上：https://github.com/lw1243925457/MybatisDemo/tree/master/

- [MyBatis Demo 编写（1）基础功能搭建](https://juejin.cn/post/7064351012022124580/)
- [MyBatis Demo 编写（2）结果映射转换处理](https://juejin.cn/post/7064907905669005342/)

## 参考链接
- 《MyBatis3源码深度解析》：这本书确实不错，通读一两遍后，自己探索Debug，有很多的帮助