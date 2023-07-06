# 项目名称

该项目是一个基于Spring Boot框架的开发模板，旨在帮助开发者迅速的进行开发，不必花费时间在大量的重复工作。

## 版本信息

包含了主要项的版本信息，更多内容请参考**pom.xml**

| 项            | 版本     |
|--------------|--------|
| JDK          | 1.8    |
| springboot   | 2.7.7  |
| MySQL        | 8.0.31 |
| Redis        | 6.2.6  |
| Maven        | 3.8.6  |
| Mybatis-plus | 3.5.3  |
| knife4j      | 4.0.13 |

## 使用

### 相关配置

需要修改以下配置信息来配置您自己的服务

> 需要修改的内容已在代码文件中使用`TODO`标识

1. Redis
2. MySQL
3. aliyun AK SK
4. knife4j

## 内容

项目主要包含以下内容：

- Logback日志配置
- 阿里云SMS配置
- 线程池配置
- Mybatis-plus
- 统一返回结果
- 全局异常处理、统一错误枚举
- Dockerfile文件
- AOP权限认证以及日志处理
- 基本的用户CRUD、登录注册等逻辑、对应的SQL文件
- JWT双Token登录
- 常用工具类
- Knife4j接口文档: 访问`http://localhost:8080/api/doc.html`
- Springboot`@Scheduled`定时任务


## 如何运行

1. `git clone https://github.com/adorabled4/springboot-template.git`
2. 修改数据库和Redis配置信息：`application.yml`
3. 构建项目：`mvn clean package`
4. 运行项目：`java -jar target/template.jar`
5. 构建镜像：`docker build -f Dockerfile -t template:v1 .`
6. 运行容器：`docker run -d -p 8080:8080 --name tempalte template:v1`


## 许可证

该项目基于MIT许可证开源，详情请查看[LICENSE文件](./LICENSE)。