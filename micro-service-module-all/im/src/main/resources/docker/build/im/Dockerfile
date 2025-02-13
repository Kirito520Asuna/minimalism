#指定基础镜像为带有 Alpine Linux 的 OpenJDK 8。
FROM openjdk:8-jdk-alpine
#设置 Dockerfile 的作者或维护者。
MAINTAINER kirito-asuna
#设置一个名为 JAR_ENV 的环境变量，值为 "dev"。可以用来指定应用程序的配置文件。
ENV JAR_ENV dev
#设置时区为 Asia/Shanghai。
ENV TZ=Asia/Shanghai
#通过创建符号链接将系统时区设置为 /usr/share/zoneinfo/Asia/Shanghai，并将时区保存在 /etc/timezone 文件中。
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo '$TZ' > /etc/timezone
#如果目录 /run/im 不存在，则创建该目录
RUN mkdir -p /run/im
#将工作目录设置为 /run/im。
WORKDIR /run/im
#暴露容器的 9500 端口。
EXPOSE 9500
EXPOSE 8004
#将本地目录中的 imlearn-0.0.1-SNAPSHOT.jar 文件复制到容器，并将其重命名为 app.jar。
ADD ./im-0.0.1-SNAPSHOT.jar ./app.jar
#指定容器运行时的入口命令。使用指定的参数执行 java 命令。
#-Djava.security.egd=file:/dev/./urandom 是 Java 虚拟机（JVM）的系统属性之一。这个属性指定了用于生成随机数的熵源。
#在 Docker 容器中，由于容器的隔离性，可能会导致 /dev/random 这个熵源的阻塞情况，从而影响到应用程序的性能。
# 为了避免这个问题，可以将 -Djava.security.egd 设置为 /dev/./urandom，它是一个非阻塞的熵源，可以更快地生成随机数。
#简单来说，这个属性的作用是告诉 JVM 使用 /dev/./urandom 作为熵源，以获得更好的随机数性能和避免可能的阻塞情况。
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
#设置容器的默认命令和参数。根据 JAR_ENV 环境变量的值来设置 Spring 的活动配置文件。
CMD ["--spring.profiles.active=${JAR_ENV}"]
