

# NettyIm

Is a Instant Messaging demo that construct by zookeeper, redis and Protobuf

here is the tools I use in the project

| tool            | version | download address                                             |
| --------------- | ------- | ------------------------------------------------------------ |
| zookeeper 3.6.3 | 3.6.3   | http://archive.apache.org/dist/zookeeper/zookeeper-3.6.3/apache-zookeeper-3.6.3-bin.tar.gz |
| protobuf        | 3.6.1   | https://github.com/protocolbuffers/protobuf/releases/download/v3.6.1/protoc-3.6.1-win32.zip |
| redis           | 6.2.4   | https://download.redis.io/releases/redis-6.2.4.tar.gz?_ga=2.139565360.205355355.1626767453-1282014507.1626767453 |
| mysql(optional) | 5.7     |                                                              |



In case you are confused about the maven repos, the maven repos I use are as below (feel free to user the latest version and find out the new feature):

| groupId              | artifactId     | version      |
| -------------------- | -------------- | ------------ |
| io.netty             | netty-all      | 4.0.33.Final |
| com.google.protobuf  | protobuf-java  | 3.6.1        |
| com.google.code.gson | gson           | 2.6.2        |
| org.projectlombok    | lombok         | 1.16.10      |
| com.google.guava     | guava          | 17.0         |
| com.alibaba          | fastjson       | 1.2.29       |
| org.apache.zookeeper | zookeeper      | 3.4.8        |
| org.apache.curator   | curator-client | 4.0.0        |
| redis.clients        | jedis          | 2.9.0        |
| org.apache.commons   | commons-pool2  | 2.6.0        |

You can find out all repos  in the pom.xml

## ImCommon

This module contains the common tools that will be frequently used in the project.





## WebGate

This module give out the api of user login.

The load balance module in this project will help user to find out the best zk node which holds the least users.

Also, Swagger2 and MybatisGenerator are used in the module: 

​	`src/main/java/com/lzr/start/Swagger2.java` you can find out the configuration of Swagger2 in this file;

​	`WebGate/pom.xml` the "<build> ... </build>" part in the pom.xml shows how to utilize and config MybatisGenerator.

​	*since I don't complete the mysql part coding yet, you can skip this part or finish it in your own;



## ImServer

This module give out the code of NettyServer





## ImClient

Not finish yet........



# How To Start

1. setup your idea project and maven

2. build your zookeeper cluster (you can find the tutorial in this repo: https://github.com/LuxyRayn/curatorSimple)

3. start the redis server and change the config files below (change all ip and port to those of your zookeeper and redis):

   ```
   ImServer:
   src/main/resources/application.properties
   src/main/resources/application-redis.yml
   
   WebGate:
   src/main/resources/application.properties
   src/main/resources/redis.properties
   ```

4. start the ImServer: `src/main/java/com/lzr/server/startup/ServerApplication.java`

5. start the WebGate: `src/main/java/com/lzr/WebGateSpringApplication.java`

6. .......start the client (not finish yet)
