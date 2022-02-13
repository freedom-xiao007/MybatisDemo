# Mybatis Demo
***
## 简介
用于学习Mybatis，分析其源码，并尝试写写自己的Mybatis框架

## MyBatis Demo 思路
- [x] SQLSessionFactory 整合数据源：提供无参数无返回的SQL执行
  - 自定义 SQLSessionFactory：能获取Mapper
  - 自定义 MapperProxy：能执行无参无返回SQL