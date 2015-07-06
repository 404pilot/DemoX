# Docker

## Cases

#### How many processes are running & which process is `PID1`
```
$ docker exec -it test ps aux
```
* only `PID1` process is able to receive `SIGTERM`(graceful shutdown), `SIGKILL`

#### run shell command in Dockerfile
```
ENTRYPOINT ["echo", "hello world!"]
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



## Explanation

```
ENTRYPOINT ["echo", "hello"]
CMD ["world"]

$ docker logs $(docker run -d test)
hello ls -l
$ docker logs $(docker run -d test world)
hello world
```

* CMD 和 ENTRYPOINT 一起出现的时候，CMD 全部作为 ENTRYPOINT 的 parameter
* **CMD 主要是提供给 ENTRYPOINT 默认参数**

```
ENTRYPOINT "echo" "hello"
CMD "ls" "-l"

$ docker logs $(docker run -d test)
hello
$ docker logs $(docker run -d test world)
hello
```

* CMD 作为 Entrypoint 默认参数的话，必须使用**exec form**

---

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

---

```
$ docker logs $(docker run -d debian:stable echo $PATH)
/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
```
* 「个人之言」实际上 shell form 和 exec form 能直接运行的，只要是在这个 image 的 path 里都行，否则就要特别 specify full path 了
* 「官方」CMD 默认是被`/bin/sh -c`执行
* 「官方」exec 就要给 executable 的 full path
* 一般`docker run -d debian:stable`这样，image 中都是没有 ENTRYPOINT 和 CMD 的，默认是被`/bin/sh -c` 运行，但必须都是命令


```
CMD "while sleep 2; do echo thinking; done"

$ docker logs $(docker run -d --name demo test)

$ docker ps -l
CONTAINER ID        IMAGE               COMMAND                CREATED             STATUS                       PORTS               NAMES
67f2de05b76b        test                "/bin/sh -c '\"while   9 seconds ago       Exited (127) 8 seconds ago                       demo
```

```
CMD "bin/bash" "-c" "while sleep 2; do echo thinking; done"

$ docker logs $(docker run -d --name demo test)
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

$ docker logs $(docker run -d --name demo test)
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
