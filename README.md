# Mybatis Demo
***
## 简介
用于学习Mybatis，分析其源码，并尝试写写自己的Mybatis框架

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