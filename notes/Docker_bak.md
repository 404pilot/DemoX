# Docker

## Cases

### Check processes running in containers and also check which process is `PID1`
```
$ docker exec -it test ps aux
```

### Use shell in ENTRYPOINT

```
ENTRYPOINT ["/bin/sh", "-c", "while sleep 2; do echo thinking; done"]
```



# TODO

## Intro

Docker listens to local unix socket (`docker.sock`)

``` plain
$ ls -l /var/run

lrwxrwxrwx 1 root root 4 May  7 16:23 /var/run -> /run

$ ls -l /run

srw-rw---- 1 root docker 0 Jun  5 00:01 docker.sock
```

* permission中`l`表示 link，`s`表示 socket

可见`docker.sock`可以被 root 执行，也可以被 docker 这个 group 来运行 （可以把 user 加入 docker 这个 group 来避免每次都要 sudo）
``` plain
$ cat /etc/group

$ sudo passwd -a _username docker
```

docker 分 server 端和 client 端

* sever 端就是类似 tomcat 之类处理请求，具体就是 docker daemon/ docker engine
* client 端就是 CLI 发送请求
* 所以可以使用 client 端发送请求给docker server on another physical/virtual server

``` plain
// docker daemon listens to localhost on port 2375
$ docker -H 192.168.56.50:2375 -d

// check if daemon is listening or not
$ netstat -tlp

// on the other server, tell docker client to connect to remote 192.168.56.50：2375
$ export DOCKER_HOST="tcp://192.168.56.50:2375"
```

But be careful for doing it, since it will bind docker deamon to a network and it is not safe to do it. By default, docker daemon is restricted by sudoers and docker group.

``` plain
// inside a docker container (centos)

[root@505cba62efa1 /]# cat /etc/hosts
172.17.0.2	505cba62efa1
127.0.0.1	localhost

[root@505cba62efa1 /]# ip a
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host
       valid_lft forever preferred_lft forever
7: eth0: <BROADCAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP
    link/ether 02:42:ac:11:00:02 brd ff:ff:ff:ff:ff:ff
    inet 172.17.0.2/16 scope global eth0
       valid_lft forever preferred_lft forever
    inet6 fe80::42:acff:fe11:2/64 scope link
       valid_lft forever preferred_lft forever
```

`172.17.0.2`就像是一个 vm

``` plain
[root@505cba62efa1 /]# uname -a
Linux 505cba62efa1 3.13.0-52-generic #86-Ubuntu SMP Mon May 4 04:32:59 UTC 2015 x86_64 x86_64 x86_64 GNU/Linux
```

ubuntu上的 docker **centos** container 依然显示是 ubuntu linux kernel。containers 会 share 真实 server 的 kernel。

``` plain
root@docker:~# docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
root@docker:~# docker ps -a
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS                     PORTS               NAMES
505cba62efa1        centos:latest       "/bin/bash"         9 minutes ago       Exited (0) 8 seconds ago                       compassionate_almeida
f8868cc78c68        busybox:latest      "sleep 30"          5 hours ago         Exited (0) 5 hours ago                         busybox
root@docker:~# ls -l /var/lib/docker/aufs/diff
total 40
drwxr-xr-x 17 root root 4096 Jun  5 05:36 41459f052977938b824dd011e1f2bec2cb4d133dfc7e1aa0e90f7c5d337ca9c4
drwxr-xr-x  7 root root 4096 Jun  5 05:46 505cba62efa13a1fb000e390241706e3d8141d6000a4df90508efb6a2914b845
drwxr-xr-x  6 root root 4096 Jun  5 05:36 505cba62efa13a1fb000e390241706e3d8141d6000a4df90508efb6a2914b845-init
drwxr-xr-x  2 root root 4096 Jun  5 05:36 6941bfcbbfca7f4f48becd38f2639157042b5cf9ab8c080f1d8b6d047380ecfc
drwxr-xr-x 17 root root 4096 Jun  5 00:02 6ce2e90b0bc7224de3db1f0d646fe8e2c4dd37f1793928287f6074bc451a57ea
drwxr-xr-x  2 root root 4096 Jun  5 00:02 8c2e06607696bd4afb3d03b687e361cc43cf8ec1a4a725bc96e39f05ba97dd55
drwxr-xr-x  2 root root 4096 Jun  5 00:02 cf2616975b4a3cba083ca99bc3f0bf25f5f528c3c52be1596b30f60b0b1c37ff
drwxr-xr-x  4 root root 4096 Jun  5 00:02 f8868cc78c68e91e6b4141b4acb143731cd3c5b978236ae5e283e67ee556c551
drwxr-xr-x  6 root root 4096 Jun  5 00:02 f8868cc78c68e91e6b4141b4acb143731cd3c5b978236ae5e283e67ee556c551-init
drwxr-xr-x  2 root root 4096 Jun  5 05:36 fd44297e2ddb050ec4fa9752b7a4e3a8439061991886e2091e7c1f007c906d75

root@docker:~# ls -l /var/lib/docker/aufs/diff/505cba62efa13a1fb000e390241706e3d8141d6000a4df90508efb6a2914b845
total 16
-rw-r--r-- 1 root root   15 Jun  5 05:46 newfile.txt
dr-xr-x--- 2 root root 4096 Jun  5 05:46 root
drwxrwxrwt 2 root root 4096 Jun  5 05:36 tmp
drwxr-xr-x 3 root root 4096 Jun  5 05:41 var

root@docker:~# cat /var/lib/docker/aufs/diff/505cba62efa13a1fb000e390241706e3d8141d6000a4df90508efb6a2914b845/newfile.txt
this is a test

root@docker:~# docker start 505cba62efa1
505cba62efa1
root@docker:~# docker attach 505cba62efa1
```

在`505cba62efa1`中新增了newfile，还是可以在真实 machine 本地找到

同时可以重新 start 那个container，在 attach 上去

#### Image or Container

* image 是**静态**的，可以从网上直接 pull，也可以save 到本地
	* run `docker images` to show all images
* container 是动态的，在 launch 一个 image 之后，内部修改的话，会有自动构建多个containers，每个 container 可以 save 为一个新的 image。container 是 runtime instance of image
	* run `docker ps -a` to show all containers
	* run `docker commit` to create a image from the container
* 每次 container run 一下都会有个新的 container，只有 save 了才是 image

## commands

``` plain
// show all running containers
docker ps
// show all containers even it is not running
docker ps -a
// lastest container
docker ps -l

// attach to a running container, either to view its ongoing output
       or to control it interactively.
// 要不然不能进入操作，或者看到信息，更像是一个 debug 或者 view 的功能
docker attach

// default will pull lastest
docker pull fedora
// image:tag
docker pull fedora:latest

docker images fedora

// download to /var/lib/docker/aufs (advanced multi layered unification filesystem)
docker pull coreos/etcd

docker run

// interactive tty
docker run -it ubuntu /bin/bash

// <ctrl + p> + <ctrl + q> : detach current container without stopping it

docker images --tree

// create a image from a container
docker commit _container_id _tag

docker save -o /tmp/file.tar.gz _tag

docker load -i /tmp/file.tar.gz

docker run --cpu-shares=256
docker run memory=1g

docker stop _containerId/_containerName

docker info

docker rm _containerId
docker rm -f _runningContainerId
docker rmi _imageId

docker logs
// follow, like tail command
docker logs -f _container
```

---
---

Major Docker Components


* Docker Engine 底层支持运行的，就是 daemon
* Docker images 可以打包之类的镜像
* Docker Containers: launch container to run image

*未来 app 可以基于一个 os 开发，然后 build 一个 image，就可以在任意地方（win/macos/linux）上运行*


一个 image id 就对应一个 image


layers -> image & stacks of image

base image + image_A + image_B = image

就是类似git，app 是一步步 commit 过来的，但是这些个 image 很大，存储的应该是 diff

Image layering is accomplished through union mounts.

top layer拥有 conflicts 下的优先级， win conflict

layer 都是只读的，平常用的时候再最上层有个 R/W的 layer，只有这个可以做修改，所以launch container 之后一个小的修改就会 generate 一个新的 container

all layers will form a image

``` plain
root@docker:~# docker history centos
IMAGE               CREATED             CREATED BY                                      SIZE
fd44297e2ddb        6 weeks ago         /bin/sh -c #(nop) CMD ["/bin/bash"]             0 B
41459f052977        6 weeks ago         /bin/sh -c #(nop) ADD file:be2a22bb15fbbbf24b   215.7 MB
6941bfcbbfca        6 weeks ago         /bin/sh -c #(nop) MAINTAINER The CentOS Proje   0 B
```

一步步建立起来，每步还有一定大小

/var/lib/docker/aufs/layers/id/里面就是按顺序根据不同的 layer 构造 image

docker images --tree显示的就是上面那些内容

best practice 应该是各个程序都分 layer 来。这样每个 layer 可以随时替换，也可以被多个 containers 共享

``` plain
root@docker:~# docker pull centos
latest: Pulling from centos
6941bfcbbfca: Already exists
41459f052977: Already exists
fd44297e2ddb: Already exists
Digest: sha256:d601d3b928eb2954653c59e65862aabb31edefa868bd5148a41fa45004c12288
Status: Image is up to date for centos:latest
```

这么多 image id，一层层叠上来的，每次修改commit 的话就是对那个 r/w top layer进行 commit，因为其他的都只是只读的

ubuntu image 上就没有 wget 之类的命令，docker image 中只能非常少的功能，没用的不要。lightweight

``` plain
root@50d2edb9c121:/# uname -a
Linux 50d2edb9c121 3.13.0-52-generic #86-Ubuntu SMP Mon May 4 04:32:59 UTC 2015 x86_64 x86_64 x86_64 GNU/Linux
root@50d2edb9c121:/# wget
bash: wget: command not found
```


docker image | t
--- | ---
thin writable layer | s
layer A |
layer B |

container 最开始 load 一个 image（layer A + B），之后所做的修改都是在可写的那层。这个 image 是可以被多个 container 共享的

every container gets its own writable top layer



``` plain
docker run ubuntu /bin/bash -c "echo 'this is a test' > /tmp/test.txt"
```

`/bin/bash -c`用 bash 来 run 一些指令



container run 的东西结束了，container 也就停止了，要不然就一直在 running。结束之后也可以`docker attach _container_id`来重新load container

docker run  ubuntu:14.04

加上 tag 之后每次 explicitly run 会比较 readable，也可控

```
docker run -d ubuntu:14.04.01 /bin/bash -c "ping 8.8.8.8"

docker inspect _container_id

docker attach _container_name
```

```
root@docker:~# docker ps
CONTAINER ID        IMAGE               COMMAND                CREATED             STATUS              PORTS               NAMES
575c8be30e6f        ubuntu:latest       "/bin/bash -c 'ping    3 seconds ago       Up 3 seconds                            clever_hodgkin
root@docker:~# docker top 575c8be30e6f
UID                 PID                 PPID                C                   STIME               TTY                 TIME                CMD
root                27602               1024                0                   03:28               ?                   00:00:00            ping 8.8.8.8
```

docker top -> Display the running processes of a container


## Container Management

* `docker stop` sends `SIGTERM` to containers. Standard `SIGTERM` is able to gracefully shutdown application?
* `docker kill`: bruce force approach. no graceful termination
	* `docker kill -s <SIGNAL>`
* 都是对`PID1`操作

### PID1
对于 linux 来说，`pid=1`的 process 是 `init`

对于 docker container 来说，`pid=1`一般是我们认为的那个在 container 内部 running process

* 可以进入 docker container 内部，run `ps -ef`看看 **pid=1** 是什么

Best Practice:

* One process per Container --> 简单理解为就是`PID1`
	* One concern per container
	* Lean
	* Simple
* (btw It is possible to run multiple processes per Container)


`docker attach` will attaches to `PID1` inside the container. But in the real world, `PID1` inside a container will probably not be a shell. And also, most containers will not be running an SSH server.

所以怎么 debug 呢？得有个方法去进入到 container 内部去操作。

1. **nsenter**
	* allows us to enter namespaces
	* requires the containers PID (get from `docker inspect`)
	* `docker inspect _containerId | grep Pid`
	* `nsenter -m - u -n -p -i -t _pid /bin/bash`
2. `docker-enter _containerId`
3. `docker exec -it _containerId /bin/bash`

## Dockerfile

`docker build -t helloworld:0.1 .`

Every `RUN` instruction adds  a layer to our image

每个`run`都会弄出一个新的 container，回想之前的 layer 笔记

``` plain
docker tag _imageId _user/_name:_tag
docker push _user/_name:_tag
```

layer/image is shared. Only new layer (diff) will be uploaded.

build的时候一样，本地有某个 image cache 的话，也是直接用。估计是用的 hash id 来确定具体 image 的。It is called Build Cache


``` shell
apt-get clean \
&& rm -rf /var/lib/apt/list/* /tmp/* /var/tmp/*
```

适当的在 Dockerfile 中合并 image layers 可以减少 image 大小

### CMD or ENTRYPOINT
无论是`CMD`还是`ENTRYPOINT`，最后的命令都是需要一直运行了，container 才不会退出。

```
ENTRYPOINT service tomcat7 start && tail -f /var/lib/tomcat7/logs/catalina.out
```

或者可以`/tomcat/bin/start`

#### ENTRYPOINT
`Entrypoint`一般用来指定 image  的 default action，一般就是想要的那个服务了。

* 无法被`docker run`来 override（但是也是可以被`docker run --entrypoint`来 override的）
* any command at run-time is used as an argument to `ENTRYPOINT` （见下面的例子）
* 默认是 run `bin/sh -c`去执行`ENTRYPOINT`里的 command

``` plain
root@docker:~/demo# cat Dockerfile
FROM ubuntu
ENTRYPOINT ["echo"]

root@docker:~/demo# docker build -t demo .
Sending build context to Docker daemon 2.048 kB
Sending build context to Docker daemon
Step 0 : FROM ubuntu
 ---> fa81ed084842
Step 1 : ENTRYPOINT echo
 ---> Running in 5043cb975286
 ---> 32f1a1048308
Removing intermediate container 5043cb975286
Successfully built 32f1a1048308

root@docker:~/demo# docker run demo hellow world
hellow world

root@docker:~/demo# docker run -it demo /bin/bash
/bin/bash
```

`/bin/bash`被 interpret 成`ENTRYPOINT`的 argument

可以指指定一个`ENTRYPOINT`，然后在`docker run`传入不同给的 arguments 来带来不同的 behavior

docker 有个 default `ENTRYPOINT`， 就是`/bin/sh -c`，所以用`docker run /bin/bash`就是`/bin/sh -c /bin/bash`

``` plain
ENTRYPOINT ["executable"]
CMD ["param1","param2"]
```
这样就可以有个默认的带 arguments 的container，并且可以用`docker run`来修改 default arguments

``` plain
ENTRYPOINT ["executable","param0"]
CMD ["param1","param2"]
```
这样写会导致`param0`不会被`docker run`修改

#### CMD
* `docker run <args> <command>`会 override 那个在 Dockerfile 里的 CMD
* Dockerfile 里面 CMD 只会 run 最后一个（如果有多个  CMD 的话）

`CMD`后面可以跟：

1. Shell Form
	* Commands are expressed the same way as shell commands
	* Commands get prepended by `"bin/sh -c"`, e.g. `CMD echo "hello world"`(直接用 sh 来 run 这个 echo 命令)
	* variable expansion etc..., e.g. `CMD echo $var1`（见下面例子）
2. Exec Form (preferred form)
	 * JSON array style, e.g. `CMD ["echo", "hello world"]` (也是 get prepended by `bin/sh -c`)
	 * Containers don't need a shell
	 * Avoids string munging by the shell
	 * no shell features (no variable expansion, no special characters($$, ||, >>))

``` plain
ENV var1=ping var2=8.8.8.8
CMD $var1 $var2
```

###  ENV

* `ENV`写一行也就only one layer
* container 里面能用`ENV`环境变量
* Dockerfile 里面也能用

``` plain
ENV myName="John Doe" myDog=Rex\ The\ Dog \
    myCat=fluffy
```

### VOLUME

Dockerfile里的`VOLUME`只能指定 container 里的 mount point

TODO: docker rm -v

## debug commands

``` plain
docker images --tree

docker history _image
```

these commands can be used to see how a custom image is created steps by steps based on various image layers

`docker port _id`来查看 container 的 port mapping

``` plain
root@docker:~# ifconfig
docker0   Link encap:Ethernet  HWaddr 56:84:7a:fe:97:99
          inet addr:172.17.42.1  Bcast:0.0.0.0  Mask:255.255.0.0
          inet6 addr: fe80::5484:7aff:fefe:9799/64 Scope:Link
          UP BROADCAST MULTICAST  MTU:1500  Metric:1
          RX packets:106270 errors:0 dropped:0 overruns:0 frame:0
          TX packets:106229 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0
          RX bytes:8756552 (8.7 MB)  TX bytes:10240962 (10.2 MB)
```
用 docker0 来通信

主机 ip 有

1. `127.0.0.1`
2. 对外“真实”：`10.10.152.8`

ip `0.0.0.0`表示上面二种

所以经常有些配置文件比如 mysql.conf之类，如果要外部能访问的话，设置0.0.0.0

1. 这样本机能通过`127.0.0.1`来 access
2. 外部服务能通过`10.10.152.8`来 access

TODO: docker linking

`ls -1`


## Docker Daemon Logging/ Containers Logging

``` plain
$ service docker stop

$ docker -d -l debug &
```

`docker -d`应该会重新启动 docker server

``` plain
root@docker:~# docker ps

DEBU[0248] Calling GET /containers/json
INFO[0248] GET /v1.18/containers/json
INFO[0248] +job containers()
INFO[0248] -job containers() = OK (0)
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
```

CLI (client) send REST request to docker server

`docker -d >> <file> 2>&1`

也可以

``` plain
root@docker:~# vi /etc/default/docker

root@docker:~# cat /etc/default/docker
# Docker Upstart and SysVinit configuration file

# Customize location of Docker binary (especially for development testing).
DOCKER="/usr/bin/docker"


# Use DOCKER_OPTS to modify the daemon startup options.
DOCKER_OPTS=' --host=unix:///var/run/docker.sock --restart=false --log-level=debug'


# If you need Docker to use an HTTP proxy, it can also be specified here.

# This is also a handy place to tweak where Docker's temporary files go.


root@docker:~# ps -ef | grep docker
root     31218 30481  0 17:04 pts/0    00:00:00 docker -d -l debug
root     32141 30481  0 17:14 pts/0    00:00:00 grep --color=auto docker

root@docker:~# service docker stop
stop: Unknown instance:
root@docker:~# kill 31218
root@docker:~# INFO[0647] Received signal 'terminated', starting shutdown of docker...
DEBU[0647] starting clean shutdown of all containers...
INFO[0647] -job serveapi(unix:///var/run/docker.sock) = OK (0)

[1]+  Done                    docker -d -l debug

root@docker:~# service docker start
docker start/running, process 32258

root@docker:~# ps -ef | grep docker
root     32258     1  1 17:15 ?        00:00:00 /usr/bin/docker -d --host=unix:///var/run/docker.sock --restart=false --log-level=debug
root     32296 30481  0 17:15 pts/0    00:00:00 grep --color=auto docker
```

`/etc/default/docker`可以指定默认启动参数

TODO: `DOCKER_OPTS=' --host=unix:///var/run/docker.sock --restart=false --log-level=debug'`好像不起作用

## How to write Dockerfile


`docker run -it --name test ubuntu:15.04 /bin/bash`

一边用 container 里的 bash 调试，一边写（记录步骤）Dockerfile

troubleshooting 的时候，可以运行`docker images`去看哪些 layers 成功 build 了，哪些失败了，再对照 Dockerfile 来找。也可以在 build 的时候看 steps info

* `docker run -it _imageId /bin/bash`用 bash 来调试 image
* `docker exec -it _containerId /bin/bash`用 bash 来调试 container



One liner to stop / remove all of Docker containers:

``` plain
docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)
```

* `ADD`添加stuff到container里
* `WORKDIR`指定dockerfile里面命令的默认执行path


#### some thoughts

如何deploy到test

1. start elasticsearch and get its ip address
2. Glassfish或者tomcat的docker container
	* `VOLUME`: mount war file location
	* `VOLUME`: mount log files
	* `ENV`: set proper environmental properties (e.g. elasticsearch ip)
2. 打包source code成war file
3. 启动webserver container，mount 创建打包好的 war file 到 web server container
4. start web server container and return its address (static ip?)


TODO: docker compose?





1. https://training.docker.com/
2. network 直接内部 call 默认网关试试
3. busybox、debian
4. netstat -tulpn
5. debug 用 docker logs 看输出，或者用个 sh 去循环，在 docker exec 进去： 能不能总结下，while true; do echo a > /root/app/a.log; sleep 2; done
6. improve docker alias



## Run elasticsearch

*  specify logging.xml
```
docker run -d --name x-elasticsearch \
	-v "$PWD/elasticsearch/config":/usr/share/elasticsearch/config \
	-v "$PWD/elasticsearch/data":/usr/share/elasticsearch/data \
	-p 127.0.0.1:9200:9200 \
	-p 127.0.0.1:9300:9300 \
	elasticsearch:1.6
```

* without logging.xml

```
docker run -d --name x-elasticsearch \
	-v "$PWD/elasticsearch/config/elasticsearch.yml":/usr/share/elasticsearch/config/elasticsearch.yml \
	-v "$PWD/elasticsearch/data":/usr/share/elasticsearch/data \
	-p 127.0.0.1:9200:9200 \
	-p 127.0.0.1:9300:9300 \
	elasticsearch:1.6
```

## Run glassfish

* run generic glassfish

```
docker run -d -p 127.0.0.1:8080:8080 glassfish:latest
```

* run glassfish from custom glassfish image
```
docker run -d --name as-glassfish \
	-p 127.0.0.1:8080:8080  -p 127.0.0.1:8181:8181  -p 127.0.0.1:4848:4848 \
	-v "$PWD/logs":/usr/local/glassfish4/glassfish/domains/audit-service/logs \
	glass
```

> docker network reference: http://stackoverflow.com/questions/24319662/from-inside-of-a-docker-container-how-do-i-connect-to-the-localhost-of-the-mach

> another possible solution: https://docs.docker.com/articles/ambassador_pattern_linking/

* **bridge mode**:


1. use `docker --link`
2. previous commands will create a environmental variable in container
3. refer that env variable in properties file

```
docker run -d --name as-glassfish \
	-p 127.0.0.1:8080:8080  -p 127.0.0.1:8181:8181  -p 127.0.0.1:4848:4848 \
	--link localtest \
	-v "$PWD/logs":/usr/local/glassfish4/glassfish/domains/audit-service/logs \
	-v "$PWD/properties":/root/properties \
	glass
```

* **host mode**; port is explicitly exposed on host machine, need to resolve port conflicts manually

```
docker run -d --name as-glassfish --net=host \
	-v "$PWD/logs":/usr/local/glassfish4/glassfish/domains/audit-service/logs \
	-v "$PWD/properties":/root/properties \
	glass
```

docker run -d --name as-glassfish --net=host \
	-v "$PWD/config/domain.xml":/usr/local/glassfish4/glassfish/domains/domain1/config/domain.xml \
	-v "$PWD/audit-service-1.0-SNAPSHOT.war":/usr/local/glassfish4/glassfish/domains/domain1/autodeploy/audit-service-1.0-SNAPSHOT.war \
	-v "$PWD/logs":/usr/local/glassfish4/glassfish/domains/domain1/logs \
	-v "$PWD/properties":/root/properties \
	glassfish:4.1



## Artifact Container

* one possible solution with maven: https://github.com/spotify/docker-maven-plugin



…
	-v "$PWD/../artifact/artifact/docker/properties":/root/properties \
	glassfish:4.1
…

docker run -d --name as-glassfish --net=host \
	-v "$PWD/glassfish/config/domain.xml":/usr/local/glassfish4/glassfish/domains/domain1/config/domain.xml \
	-v "$PWD/artifact/app/audit-service-1.0-SNAPSHOT.war":/usr/local/glassfish4/glassfish/domains/domain1/autodeploy/audit-service-1.0-SNAPSHOT.war \
	-v "$PWD/glassfish/logs":/usr/local/glassfish4/glassfish/domains/domain1/logs \
	-v "$PWD/artifact/app/properties/docker":/root/properties \
…
	glassfish:4.1


## Artifact Container

docker run -it -v "$PWD/artifact":/root/app test /bin/bash
…

* one possible solution with maven: https://github.com/spotify/docker-maven-plugin

# CMD ["/bin/sh -c", "..." ]
# ENTRYPOINT ["echo"] : the default action is '/bin/sh -c ${entrypoint}'
…


// TODO https://docs.docker.com/articles/ambassador_pattern_linking/

// TODO https://docs.docker.com/compose/

rackspace docker 101


http://www.infoq.com/cn/articles/docker-integrated-test-and-deployment
