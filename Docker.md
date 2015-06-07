# Docker

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

## debug commands

``` plain
docker images --tree

docker history _imageId
```