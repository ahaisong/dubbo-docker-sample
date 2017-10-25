有些部署场景需要动态指定服务注册的地址，如docker bridge网络模式下要指定注册宿主机ip已实现外网通信。dubbo提供了两个启动阶段的系统属性，用于设置对外通信的ip、port地址   
* DUBBO_IP_TO_REGISTRY --- 注册到注册中心的ip地址  
* DUBBO_PORT_TO_REGISTRY --- 注册到注册中心的端口，同时也是TCP监听端口  
不论注册哪个ip，dubbo默认始终是绑定0.0.0.0:port地址，其中port受具体配置影响 

[dubbo-docker-sample](https://github.com/dubbo/dubbo-docker-sample)工程本地运行流程： 
 
1. clone工程到本地 
```sh
git clone git@github.com:dubbo/dubbo-docker-sample.git
cd dubbo-docker-sample
```
2. 本地maven打包  
```sh
mvn clean install  
```
3. docker build构建镜像  
```sh
docker build --no-cache -t dubbo-docker-sample . 
```
Dockerfile
```sh
FROM openjdk:8-jdk-alpine
ADD target/dubbo-docker-sample-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
```
4. 从镜像创建容器并运行
```sh
# 由于我们使用zk注册中心，先启动zk容器
docker run --name zkserver --restart always -d zookeeper:3.4.9
```
```sh
docker run -e DUBBO_IP_TO_REGISTRY=30.5.97.6 -e DUBBO_PORT_TO_REGISTRY=20880 -p 30.5.97.6:20880:20880 --link zkserver:zkserver -it --rm dubbo-docker-sample
```
> 假设宿主机ip为30.5.97.6。    
> 通过环境变量 `DUBBO_IP_TO_REGISTRY=30.5.97.6` `DUBBO_PORT_TO_REGISTRY=20880` 设置provider注册到注册中心的ip、port    
> 通过`-p 30.5.97.6:20880:20880`做端口映射  
> 启动后provider的注册地址为：30.5.97.6:20880，容器的监听地址为：0.0.0.0:20880  

5. 测试
从另外一个宿主机或容器执行
```sh
telnet 30.5.97.6 20880
ls
invoke com.alibaba.dubbo.test.docker.DemoService.hello("world")
```
