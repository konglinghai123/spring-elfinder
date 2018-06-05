### 基于Spring-boot 和 elfinder 的在线文件管理系统

#### 效果图
https://www.jianshu.com/p/67a1e7725dae

#### 功能
- 支持在线文件下载
- 支持目录上传
- 支持zip tar Gzip 的在线解压和压缩文件夹
- 支持多种文本格式的高亮显示和在线编辑
- 支持在线文件预览
- 支持文件夹权限设置
- 支持国际化

#### 配置 application.yml
```
file-manager:
     thumbnail:
        width: 80 # 缩略图宽
     volumes:
        - Node: # 可配置多个节点
          source: fileSystem # 暂时只支持本地文件系统
          alias: Tomcat目录 # 目录别名
          path: /D:/Java # 映射目录
          _default: true # 是否默认打开
          locale:
          constraint:
            locked: false # 文件夹是否锁定
            readable: true # 是否可读
            writable: true # 是否可写
```

#### 运行
```
    git clone https://github.com/konglinghai123/spring-elfinder.git
    mvn install
    cd spring-web
    mvn spring-boot:run
```
#### war包运行（放在tomcat ROOT 目录运行即可）
```
      <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>

            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>
```
#### 访问
http://127.0.0.1:8080