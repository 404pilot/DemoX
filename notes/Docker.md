# Docker

## Best Practice

* CMD 做 ENTRYPOINT 的默认参数
* 为了`docker run -it image /bin/bash`可以方便调试，ENTRYPOINT 使用`docker-entrypoint.sh`，并非直接使用 executable
* Dockerfile 显式指明 EXPOSE port，易读，而且`docker run --link`需要它来生成 env variable
* EXPOSE, VOLUME 其实都是可以在 docker run 里运行动态配置，但是写在 Dockerfile 里更加易读和理解
* 尽量一行写 Dockerfile，ENV 和 EXPOSE 都一样
* VOLUME 写在最后，否则 VOLUME 声明之后的再对 VOLUME 的行为都没有效果

``` sh
#!/bin/bash

set -e

# Add elasticsearch as command if needed
if [ "${1:0:1}" = '-' ]; then
	set -- elasticsearch "$@"
fi

# Drop root privileges if we are running elasticsearch
if [ "$1" = 'elasticsearch' ]; then
	# Change the ownership of /usr/share/elasticsearch/data to elasticsearch
	chown -R elasticsearch:elasticsearch /usr/share/elasticsearch/data
	exec gosu elasticsearch "$@"
fi

# As argument is not related to elasticsearch,
# then assume that user wants to run his own process,
# for example a `bash` shell to explore this image
exec "$@"
```

```
ENV myName="John Doe" \
    myCat=fluffy

EXPOSE 8080 80
```

## Cheat sheet
```
docker rmi -f $(docker images | grep "<none>" | awk "{print \$3}")

docker stop $(docker ps -a -q) ; docker rm $(docker ps -a -q)

docker run -i -t -u root -v "$PWD":/itheboss ubuntu /bin/bash
```

## Cases

#### How many processes are running & which process is `PID1`
```
$ docker exec -it test ps aux
```
* only `PID1` process is able to receive `SIGTERM`(graceful shutdown), `SIGKILL`

#### switch user in container
```
su - jenkins
```
* since default user is root, sudo is not required.

#### run container with a specific user
```
docker run -it -u root jenkins:1.609.1 /bin/bash
```

#### Keep container running

```
docker run -d debian:stable /bin/sh -c "while true; do echo hello world; sleep 1; done"
```
```
CMD "bin/bash" "-c" "while sleep 2; do echo thinking; done"
```
```
CMD ["/bin/bash", "-c", "while sleep 2; do echo thinking; done"]
```

#### run container inside vagrant

```
# Explictly specify 0.0.0.0:6379 to be exposed, otherwise service is not able to be exposed to application outside that vm
docker run docker run -d --name redis-db -p 6379:6379 redis
```

## Explanation

### ENTRYPOINT & CMD
1. 二者都是 container running 的时候才调用
2. 一般都是用 exec form，ENTRYPOINT 用 script，CMD 配默认参数
3. 或者直接用 CMD，`docker run`也可以很容易的覆盖

```
ENTRYPOINT ["echo", "hello"]
CMD ["world"]

$ docker logs $(docker run -d test)
hello world

$ docker logs $(docker run -d test alien)
hello alien
```

* CMD 和 ENTRYPOINT 一起出现的时候，CMD 全部作为 ENTRYPOINT 的 parameter
* `docker run <image> command`会覆盖 CMD 的参数

```
ENTRYPOINT "echo" "hello"
CMD "ls" "-l"

$ docker logs $(docker run -d test)
hello
$ docker logs $(docker run -d test world)
hello
```

* CMD 作为 Entrypoint 默认参数的话，二者必须使用**exec form**
* 为什么 CMD 做参数，要是**exec form**，因为**shell form**是带`/bin/sh -c`，理应只做单独一个 command
* **shell form** 的作用就是来执行 shell command


```
ENTRYPOINT ["echo", "hello"]
CMD "ls" "-l"

$ docker logs $(docker run -d test)
hello /bin/sh -c "ls" "-l"

$ docker logs $(docker run -d test alien)
hello alien
```

* 再次证明，CMD shell form 是带`/bin/sh -c`的
* Entrypoint 不用 exec form，`docker run image command`中的command 会失效
* 「猜测」: **exec form** 是直接找$PATH的 executable command，**shell form** 是去用`/bin/sh -c`运行


```
CMD ["echo", "${HOME}"]

$ docker logs $(docker run -d test)
${HOME}
```

```
CMD "echo" "${HOME}"

$ docker logs $(docker run -d test)
/root
```

* Unlike the shell form, the exec form does not invoke a command shell.
* **shell form** 可以直接执行命令，也能interpreter env variables


```
CMD [ "sh", "-c", "echo", "$HOME" ]

$ docker logs $(docker run -d test)

$
```

* 不知道为什么这个 echo 不出来，$HOME 就是 empty，跟 sh -c 有关？
* 推荐就用 exec form，必须要用 environmental variable 再去选择用 shell form


```
$ docker logs $(docker run -d debian:stable echo $PATH)
/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
```
* 「个人之言」实际上 shell form 和 exec form 能直接运行的，只要是在这个 image 的 $path 里都行，否则就要特别 specify full path 了（遇到错误无法找到 executable 的东西）
* 「个人之言」CMD 和 ENTRYPOINT 都是谈论的 executable，while-loop 虽然可以在 shell 里运行，但算不上 executable command，要运行 while-loop，就必须吧 while-loop 当做是一个命令（`/bin/sh -c`）的参数
* 「个人之言」文档里都是谈论的 executable，while-loop并不算一个很好的例子
* 「官方」CMD shell form 默认是被`/bin/sh -c`执行
* 「官方」exec 就要给 executable 的 full path
* 一般`docker run -d debian:stable`这样，image 中都是没有 ENTRYPOINT 和 CMD 的，默认是被`/bin/sh -c` 运行，但必须都是命令


```
CMD "while sleep 2; do echo thinking; done"

$ docker logs $(docker run -d --name demo test)
[nothing]
$ docker ps -l
CONTAINER ID        IMAGE               COMMAND                CREATED             STATUS                       PORTS               NAMES
67f2de05b76b        test                "/bin/sh -c '\"while   9 seconds ago       Exited (127) 8 seconds ago                       demo
```

```
CMD "bin/bash" "-c" "while sleep 2; do echo thinking; done"

$ docker run -d --name demo test

$ docker ps
CONTAINER ID        IMAGE               COMMAND                CREATED             STATUS              PORTS               NAMES
7a7b799dcba7        test                "/bin/sh -c '\"bin/b   3 seconds ago       Up 3 seconds                            demo

$ docker logs demo
thinking
thinking
thinking
```

* `/bin/sh -c` 显然运行了  `/bin/bash`


```
CMD ["/bin/bash", "-c", "while sleep 2; do echo thinking; done"]

$ docker run -d --name demo test
$ docker ps
CONTAINER ID        IMAGE               COMMAND                CREATED             STATUS              PORTS               NAMES
710ca19b0512        test                "/bin/bash -c 'while   3 seconds ago       Up 2 seconds                            demo
$ docker logs demo
thinking
thinking
thinking
```

```
$ docker run -d --name demo debian:stable "while sleep 2; do echo thinking; done"
cd6c06dca9878dde4c256eb0b2ad2e0b037c55f0faec07737c6fc72c27ac736c
Error response from daemon: Cannot start container cd6c06dca9878dde4c256eb0b2ad2e0b037c55f0faec07737c6fc72c27ac736c: [8] System error: exec: "while sleep 2; do echo thinking; done": executable file not found in $PATH
```

```
$ docker run -d --name demo debian:stable /bin/bash -c "while sleep 2; do echo thinking; done"
665f18ffe91f44566179e80235d9fe65fe6c909f9a620997d0ffd98a9068491f

$ docker logs demo
thinking
thinking
```
* CMD 里都必须是一个 command，不能是 while-loop 这样

### shell

```
vagrant@vagrant-ubuntu-trusty-64:~/share/test$ docker logs $(docker run -d busybox:ubuntu-14.04 ls /bin ) | grep sh
ash
sh
sha1sum
sha256sum
sha512sum
static-sh
```

* busybox用的`ash`

### EXPOSE

```
# EXPOSE 8080

$ docker run -d -p 0.0.0.0:8080:8080 test

$ telnet 127.0.0.1 8080
Trying 127.0.0.1...
Connected to 127.0.0.1.
Escape character is '^]'.

^C^C^C^C
Connection closed by foreign host.

$ docker stop $(docker ps -a -q) ; docker rm $(docker ps -a -q)
4aa60252eb73
4aa60252eb73

$ telnet 127.0.0.1 8080
Trying 127.0.0.1...
telnet: Unable to connect to remote host: Connection refused
```

* `0.0.0.0`表示本机所有的 IP 地址，这样监听的方式，内网外网都可以访问
* 就是不显式 expose 端口，run 的时候显示指定，也一样连接成功
* 但是`docker run --link`的时候就需要了，默认的 env variable 会依据 `EXPOSE` 产生
* 「猜测」`docker run --net="host"`会直接 expose 端口到主机，需要 Dockerfile 显示指明
* 「官方」`EXPOSE` doesn’t define which ports can be exposed to the host or make ports accessible from the host by default. To expose ports to the host, at runtime, use the -p flag or the -P flag.
* 所以还是显示 EXPOSE 比较好，也比较易读

```
$ docker run -d -p 8080 tomcat:8
eae4c0c80b367e4876324467241ab40d5dceaab5ec4a95d0757be73803d5626a

$ docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                     NAMES
eae4c0c80b36        tomcat:8            "catalina.sh run"   1 seconds ago       Up 1 seconds        0.0.0.0:32768->8080/tcp   drunk_wilson
```
* 就算 Dockerfile 里定义了 EXPOSE，但还是需要`docker run`去指明
* `docker run -p container_port`会自动 expost 一个 host port

### VOLUME
```
FROM debian:stable
RUN mkdir /myvol && echo "hello world" > /myvol/greeting
VOLUME /myvol

$ docker logs $(docker run -d test cat /myvol/greeting)
hello world
```
* VOLUME 会事先声明，在真正被 mount 之前，这个 volume 在 container 里还是存在的
* `-v`使用之后，原先 VOLUME 里的东西有**丢失**的可能

`-v host-stuff:container-stuff`中`host-stuff`如果不存在，则 docker **默认**会在 host 中 create 一个 `host-stuff` 的  **directory**，所以 host 永远都是指向一个存在的文件或者文件夹

-v | host | container | result
--- | --- | --- | ---
-v | folder | non-existed-folder | mount folder
-v | folder | existed-folder | files in existed folder will all be lost
-v | folder | existed-file | error
-v | file | dir/existed-folder | error
-v | file | dir/existed-file | rename to `existed-file` and rewrite this file with the content in host file
-v | file | dir/non-existed-file | mount file to a file named `non-existed-file` under `dir`


```
$ docker logs $(docker run -d -v "$PWD/it-is-not-a-file.txt:/myvol" test ls -alF /myvol)
total 4
drwxr-xr-x  1 1000 1000   68 Jul 27 06:29 ./
drwxr-xr-x 28 root root 4096 Jul 27 06:29 ../

vagrant@vagrant-ubuntu-trusty-64:~/share/test$ ll
total 4
drwxr-xr-x 1 vagrant vagrant 204 Jul 27 06:29 ./
drwxr-xr-x 1 vagrant vagrant 238 Jul 10 06:48 ../
drwxr-xr-x 1 vagrant vagrant  68 Jul 27 04:44 data/
-rw-r--r-- 1 vagrant vagrant  90 Jul 27 04:41 Dockerfile
drwxr-xr-x 1 vagrant vagrant 102 Jul 27 06:15 folder/
drwxr-xr-x 1 vagrant vagrant  68 Jul 27 06:29 it-is-not-a-file.txt/
```

* 直接 create 一个新的 **directory**

```
docker logs $(docker run -d -v "$PWD/folder/file.md":/myvol test ls -alF /myvol)
Error response from daemon: Cannot start container a9b33a976eb1d9799a502d0753390557e7039260bfea0b66aa20defc9ee2d100: [8] System error: not a directory
Timestamp: 2015-07-27 05:54:18.433955074 +0000 UTC
Code: System error

Message: not a directory
```
* 因为`myvol`在 container 中已经存在（Dockerfile）

```
$ cat folder/file.md
# Test

$ docker logs $(docker run -d -v "$PWD/folder/file.md":/file test cat  /file)
# Test

$ docker logs $(docker run -d -v "$PWD/folder/file.md":/myvol/greeting test cat /myvol/greeting)
# Test
```
* host 文件可以直接 mount 到 container 里去（container 指明的是完整文件路径+文件名）
