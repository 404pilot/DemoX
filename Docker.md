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

* sever 端就是类似 tomcat 之类处理请求，具体就是 docker daemon
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

## commands

``` plain
docker ps 

docker ps -a
```