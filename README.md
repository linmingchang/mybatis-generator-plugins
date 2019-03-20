## mybatis-generator-plugins
```xml
<dependency>
    <groupId>me.ifelseif</groupId>
    <artifactId>mybatis-generator-plugins</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
## 已实现功能
### 1. 整合swagger2，自动生成对应注解
```xml
<plugin type="me.ifelseif.mgr.plugins.swagger.GeneratorSwagger2Annotation">
     <!--swagger不处理一些敏感字段，表名.字段，会同时打上@JsonIgnore标签-->
     <property name="swaggerHiddenField" value="user.password,user.sign"/>
</plugin>
```