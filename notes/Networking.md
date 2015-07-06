# Network

IPv4长度是32bit，每一段8bit [0,255]  255 = 2 ^ 8 -1

`ifconfig`列出的都是**本机** network interface，有些是真实的（有对应的 network adapter），有些是虚拟的。实际的 network adapter 肯定都有真实的 mac address

mac 就有 (`networksetup -listallhardwareports`)

1. `lo0` : localhost
2. `gif0` & `stf0` : used for translating between IPv4 and IPv6
4. `en0` -> wifi
5. `en1`&`en2`: 二个 thunderbolt 接口，可以直接接网线，显示器之类，在左边
6. `bridge0`: mac thunderbolt bridge network
7. `p2p0`: android air droid
8. `awdl0`: apple wireless direct link

```
$ networksetup -listallhardwareports

Hardware Port: Wi-Fi
Device: en0
Ethernet Address: 60:03:00:00:7e:00
```

## ip, netmask, net segment
ip + netmask 决定了一个 network segment，同一个 segment 视为一个物理网段，可以直接通信，不在的就扔给 default gateway 转发处理。

## gateway & router
一般 default gateway 也就是 router。每一层不会了都是丢给默认网关，可见网关就是中转处理中心。

gateway 的能力是连接二个相邻的 network segment，比如 route 连接的是`192.168.1.x`与通往外界连接运营商的内网。一般会有多个 network adapter，每个 adapter 都会有相应的 mac address，每一块负责一个 network segment。

gateway 表示 **next hop** 这个概念，`192.168.1.103`去连接`192.168.1.104`是不会去先连接 gateway 的，这些个是直接连接的。

```
mac $ traceroute 192.168.1.104
traceroute to 192.168.1.104 (192.168.1.104), 64 hops max, 52 byte packets
^C

mac $ netstat -nr
Destination        Gateway            Flags        Refs      Use   Netif Expire
192.168.1          link#4             UCS             2        0     en0
```

* `192.168.1.x`没有 specific 的 gateway，直接从`en0`发出去，表示这个 segment 是直接互相连接的

gateway信息是不会出现在 ifconfig中，因为 ifconfig 都是本机信息，gateway 是独立的第三方 device。


## common addresses

* `boardcast`: 这个地址代表这个 segment 里的所有 host
* `lo0`比较特殊，就是自己，这个网段也没必要有个 boardcast addr。more than 私有地址，任何时候连接`127.0.0.1`都是自己

private network addr

* `10.x.x.x`: 公司用的多，因为数量多
* 172.16.x.x - 172.31.x.x
* `192.168.x.x`: 家用

## example
> 16 进制 hex，f 表示 15 (0b 1111)

interface | ip | netmask | segment | boardcast
--- | --- | --- | --- | ---
**all** `lo0` | 127.0.0.1 | 0xff000000 255.0.0.0
mac `en0` | 192.168.1.103 | 0xffffff00 255.255.255.0 | `192.168.1.x` | 192.168.1.255
vbox `docker0` | 172.17.42.1 | 255.255.0.0 | `172.17.x.x` | 0.0.0.0
vbox `en0` | 10.0.2.15 | 255.255.255.0 | `10.0.2.x` | 10.0.2.255
docker  `eth0` | 172.17.0.2 | 255.255.0.0 | `172.17.x.x` | 0.0.0.0


* `192.168.1.x`: mac 所在的 network
* `10.0.2.x`: 虚拟机所在的 network
* `172.17.x.x`: docker 所在的 network

## communication

### router
```
# router

LAN
MAC Address:   A0-F3-C1-9A-3E-9E
IP Address:         192.168.1.1
Subnet Mask:   255.255.255.0

Wireless
Wireless Mode: Router Mode
Wireless Radio:     Enable
Name (SSID):   Nexus
Channel:       Auto(Current Channel 6)
Mode:               11bgn mixed
Channel Width: Auto
MAC Address:   A0-F3-C1-9A-3E-9E
WDS Status:         Disable

WAN
MAC Address:   F0-1F-AF-24-2E-6C
IP Address:         72.179.132.252 Dynamic IP
Subnet Mask:   255.255.240.0
Default Gateway:72.179.128.1
DNS Server:         209.18.47.61 , 209.18.47.62
```

**router**用`LAN`连接内网`192.168.1.x`，用`WAN`连接运营商的内网

### mac `192.168.1.103`

`en0`在`192.168.1.x`中表示**mac**

```
mac $ ifconfig lo0

lo0: flags=8049<UP,LOOPBACK,RUNNING,MULTICAST> mtu 16384
     options=3<RXCSUM,TXCSUM>
     inet6 ::1 prefixlen 128
     inet 127.0.0.1 netmask 0xff000000
     inet6 fe80::1%lo0 prefixlen 64 scopeid 0x1
     nd6 options=1<PERFORMNUD>
```
```
mac $ ifconfig en0

en0: flags=8863<UP,BROADCAST,SMART,RUNNING,SIMPLEX,MULTICAST> mtu 1500
     ether 60:03:00:00:7e:00
     inet6 fe80::6203:8ff:fe90:7e8c%en0 prefixlen 64 scopeid 0x4
     inet 192.168.1.103 netmask 0xffffff00 broadcast 192.168.1.255
     nd6 options=1<PERFORMNUD>
     media: autoselect
     status: active
```
```
mac $ ifconfig vboxnet0
vboxnet0: flags=8842<BROADCAST,RUNNING,SIMPLEX,MULTICAST> mtu 1500
     ether 0a:00:27:00:00:00
```
* `ether` -> mac address

```
mac $ netstat -nr
Routing tables

Internet:
Destination        Gateway            Flags        Refs      Use   Netif Expire
default            192.168.1.1        UGSc           40      114     en0
127                127.0.0.1          UCS             0        6     lo0
127.0.0.1          127.0.0.1          UH              4    52242     lo0
169.254            link#4             UCS             0        0     en0
192.168.1          link#4             UCS             2        0     en0
192.168.1.1/32     link#4             UCS             1        0     en0
192.168.1.1        a0:f3:c1:9a:3e:9e  UHLWIir        42       26     en0   1046
192.168.1.102      6c:ad:f8:8c:2f:4a  UHLWIi          3     5783     en0    559
192.168.1.103/32   link#4             UCS             0        0     en0
192.168.1.104      38:b1:db:8a:91:5b  UHLWI           0      223     en0     97
```
* 可见默认网关是 `192.168.1.1`，打通外部第一站也是它
* `192.168.1.1`就是 router 地址
* `192.168.1.x`的 gateway 是 link#4，没有gateway 的，直接通过`en0`发出去就行了
* 虚拟机网络`10.2.x.x`不在 rule 里，直接走`192.168.1.1`，所以不会成功

```
mac $ traceroute www.google.com
traceroute: Warning: www.google.com has multiple addresses; using 173.194.64.99
traceroute to www.google.com (173.194.64.99), 64 hops max, 52 byte packets
 1  192.168.1.1 (192.168.1.1)  1.035 ms  3.300 ms  0.738 ms
 2  * * *
```

### virtual box (default NAT) (`10.0.2.15`)

```
vagrant@vagrant-ubuntu-trusty-64:~$ ifconfig

docker0   Link encap:Ethernet  HWaddr 72:40:7f:02:25:e8
          inet addr:172.17.42.1  Bcast:0.0.0.0  Mask:255.255.0.0
          inet6 addr: fe80::7040:7fff:fe02:25e8/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:8 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0
          RX bytes:0 (0.0 B)  TX bytes:648 (648.0 B)

eth0      Link encap:Ethernet  HWaddr 08:00:27:3a:0b:5b
          inet addr:10.0.2.15  Bcast:10.0.2.255  Mask:255.255.255.0
          inet6 addr: fe80::a00:27ff:fe3a:b5b/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:127363 errors:0 dropped:0 overruns:0 frame:0
          TX packets:55458 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000
          RX bytes:110866680 (110.8 MB)  TX bytes:3436031 (3.4 MB)

lo        Link encap:Local Loopback
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope:Host
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0
          RX bytes:0 (0.0 B)  TX bytes:0 (0.0 B)

veth97d699a Link encap:Ethernet  HWaddr da:21:a7:04:e2:a6
          inet6 addr: fe80::d821:a7ff:fe04:e2a6/64 Scope:Link
          UP BROADCAST RUNNING  MTU:1500  Metric:1
          RX packets:7 errors:0 dropped:0 overruns:0 frame:0
          TX packets:6 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000
          RX bytes:598 (598.0 B)  TX bytes:508 (508.0 B)
```

* `HWaddr` is the mac address
* `veth97d699a`只会在 container running 的时候出现，比如`docker -it run ubuntu:14.04 /bin/bash`

```
vagrant@vagrant-ubuntu-trusty-64:~$ netstat -nr
Kernel IP routing table
Destination     Gateway         Genmask         Flags   MSS Window  irtt Iface
0.0.0.0         10.0.2.2        0.0.0.0         UG        0 0          0 eth0
10.0.2.0        0.0.0.0         255.255.255.0   U         0 0          0 eth0
172.17.0.0      0.0.0.0         255.255.0.0     U         0 0          0 docker0

vagrant@vagrant-ubuntu-trusty-64:~$ ip route
default via 10.0.2.2 dev eth0
10.0.2.0/24 dev eth0  proto kernel  scope link  src 10.0.2.15
172.17.0.0/16 dev docker0  proto kernel  scope link  src 172.17.42.1
```

* `Destination: 0.0.0.0`就是表示任意
* `Genmask`就是子网掩码
* `Gateway: 0.0.0.0`表示 unspecified。一般来说就是这个网段的 network 是直接连接的，没必要通过 gateway 中转了。就是类似于`192.168.1.103`去连接`192.168.1.104`，不会连接路由器了。
* 默认任意不在 rule 里面的请求，会从`etho0`转发到`10.0.2.2`
*  `10.0.2.x`的请求，会从`eth0`发送出去，没有指定 gateway
* `172.17.x.x`的请求，会从 `docker0`发送出去，没有指定 gateway

**也就是说vbox:eth0 和 docker0 都可以直接分别处理 虚拟机内网请求 和 docker内网请求**

```
vagrant@vagrant-ubuntu-trusty-64:~$ tracepath www.google.com
 1?: [LOCALHOST]                                         pmtu 1500
 1:  10.0.2.2                                              0.165ms
 1:  10.0.2.2                                              0.231ms
 2:  192.168.1.1                                           5.985ms asymm 64
 3:  no reply
```

* 第一站是`default gateway`
* 第二站是`192.168.1.1`
* 所以`vbox(nat)`可以连接`10.0.2.x`和`192.168.1.x`的，虚拟机是可以连接到 host 的
* 但是 host（mac） 无法知晓虚拟机的存在（rule 中`10.0.2.x`走的是default gateway`192.168.1.1`）

```
vagrant@vagrant-ubuntu-trusty-64:~$ ping 192.168.1.103
PING 192.168.1.103 (192.168.1.103) 56(84) bytes of data.
64 bytes from 192.168.1.103: icmp_seq=1 ttl=63 time=0.181 ms
64 bytes from 192.168.1.103: icmp_seq=2 ttl=63 time=0.454 ms
64 bytes from 192.168.1.103: icmp_seq=3 ttl=63 time=0.547 ms
64 bytes from 192.168.1.103: icmp_seq=4 ttl=63 time=0.353 ms
^C
--- 192.168.1.103 ping statistics ---
4 packets transmitted, 4 received, 0% packet loss, time 3004ms
rtt min/avg/max/mdev = 0.181/0.383/0.547/0.137 ms

vagrant@vagrant-ubuntu-trusty-64:~$ tracepath 192.168.1.103
 1?: [LOCALHOST]                                         pmtu 1500
 1:  10.0.2.2                                              0.847ms
 1:  10.0.2.2                                              0.203ms
 2:  192.168.1.103                                         0.305ms reached
     Resume: pmtu 1500 hops 2 back 64
```

```
mac $ ping 10.0.2.15
PING 10.0.2.15 (10.0.2.15): 56 data bytes
Request timeout for icmp_seq 0
^C
--- 10.0.2.15 ping statistics ---
2 packets transmitted, 0 packets received, 100.0% packet loss

mac $ traceroute 10.0.2.15
traceroute to 10.0.2.15 (10.0.2.15), 64 hops max, 52 byte packets
 1  192.168.1.1 (192.168.1.1)  2.421 ms  0.758 ms  4.980 ms
^C
```

vbox 默认是 NAT，虚拟机会虚拟出一个 gateway，供内部使用，应该跟`vboxnet0`没有关系。这个虚拟的 gateway 知道如何通过 host machine 连接网络，是一个 “NAT gateway”

那 vagrant 如何通过 ssh 连接的，通过 port forward。

```
config.vm.network :forwarded_port, guest: 8080, host: 8080
```


### docker container (default: bridge) `172.17.0.3`

```
root@144ab6ded0c1:/# ifconfig
eth0      Link encap:Ethernet  HWaddr 02:42:ac:11:00:03
          inet addr:172.17.0.3  Bcast:0.0.0.0  Mask:255.255.0.0
          inet6 addr: fe80::42:acff:fe11:3/64 Scope:Link
          UP BROADCAST RUNNING  MTU:1500  Metric:1
          RX packets:9764 errors:0 dropped:0 overruns:0 frame:0
          TX packets:9592 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0
          RX bytes:21542832 (21.5 MB)  TX bytes:591136 (591.1 KB)

lo        Link encap:Local Loopback
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope:Host
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0
          RX bytes:0 (0.0 B)  TX bytes:0 (0.0 B)
```

container running 的时候，多出一块 interface

```
vagrant@vagrant-ubuntu-trusty-64:~$ ifconfig

veth97d699a Link encap:Ethernet  HWaddr da:21:a7:04:e2:a6
          inet6 addr: fe80::d821:a7ff:fe04:e2a6/64 Scope:Link
          UP BROADCAST RUNNING  MTU:1500  Metric:1
          RX packets:7 errors:0 dropped:0 overruns:0 frame:0
          TX packets:6 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000
          RX bytes:598 (598.0 B)  TX bytes:508 (508.0 B)
```

```
root@144ab6ded0c1:/# netstat -nr
Kernel IP routing table
Destination     Gateway         Genmask         Flags   MSS Window  irtt Iface
0.0.0.0         172.17.42.1     0.0.0.0         UG        0 0          0 eth0
172.17.0.0      0.0.0.0         255.255.0.0     U         0 0          0 eth0

root@144ab6ded0c1:/# ip route
default via 172.17.42.1 dev eth0
172.17.0.0/16 dev eth0  proto kernel  scope link  src 172.17.0.3
```

* 默认走`172.17.42.1`
* container 直接连接

```
root@144ab6ded0c1:/# tracepath www.google.com
 1?: [LOCALHOST]                                         pmtu 1500
 1:  172.17.42.1                                           0.108ms
 1:  172.17.42.1                                           0.044ms
 2:  10.0.2.2                                              0.198ms
```
* `10.0.2.2`就是 virtualbox 的默认网关

vbox 可以连接 docker container
```
vagrant@vagrant-ubuntu-trusty-64:~$ tracepath 172.17.0.3
 1?: [LOCALHOST]                                         pmtu 1500
 1:  172.17.0.3                                            0.062ms reached
 1:  172.17.0.3                                            0.059ms reached
     Resume: pmtu 1500 hops 1 back 1
```

docker 也可以连接 host machine（docker 的 host 就是虚拟机）

```
root@144ab6ded0c1:/# ping 10.0.2.15
PING 10.0.2.15 (10.0.2.15) 56(84) bytes of data.
64 bytes from 10.0.2.15: icmp_seq=1 ttl=64 time=0.042 ms

root@144ab6ded0c1:/# tracepath 10.0.2.15
 1?: [LOCALHOST]                                         pmtu 1500
 1:  10.0.2.15                                             0.056ms reached
 1:  10.0.2.15                                             0.052ms reached
```

* 直接连接的，没有走 gateway！！
* 奇怪的是 docker‘s default gateway `172.17.42.1`，跟`vbox's docker0`有相同的 IP，而之前`mac's vboxnet0`没有IP，`vbox's en0`却有IP。

实际上`docker0`只是 docker 虚拟出来的 gateway，docker container 用的是`veth`直接桥接到host（vbox）网络。所以能直接连接宿主机vbox， 跟`docker0`是一个层次的，所以container能看到的 default gateway 才有跟`docker0`相同的 IP。

* docker 可以直接连接宿主机，但是宿主机的 ip 是 dhcp 动态分配的，所以实际automation配置中 container 想访问宿主机还需要点手段
* container 的 default gateway 是`docker0`，丢给它就好，之后在转发到`host gateway`，有点绕，不能直接使用`host gateway`，多了一层network virtualization
* `docker0`同时可以处理 host 到 container 的请求

> Mode: bridge
>
With the networking mode set to `bridge` a container will use docker’s default networking setup. A bridge is setup on the host, commonly named **docker0**, and **a pair of veth interfaces** will be created for the container. One side of the veth pair will remain on the host attached to the bridge while the other side of the pair will be placed inside the container’s namespaces in addition to the loopback interface. An IP address will be allocated for containers on the bridge’s network and traffic will be routed though this bridge to the container.

也许可以这么理解，bridge 模式下，docker 先创造出`docker0`这个 bridge，然后 container 都归这个 bridge 管，`docker0`在 host 网络中和 containers 网络中的 ip 是一样的。访问 host 的话，container 会通过`veth`来直接访问。


### docker container (host)

```
vagrant@vagrant-ubuntu-trusty-64:~$ docker run -it --net="host" ubuntu:14.04 /bin/bash
root@vagrant-ubuntu-trusty-64:/# ifconfig
docker0   Link encap:Ethernet  HWaddr 00:00:00:00:00:00
          inet addr:172.17.42.1  Bcast:0.0.0.0  Mask:255.255.0.0
          inet6 addr: fe80::9cc0:6dff:fe8b:16bf/64 Scope:Link
          UP BROADCAST MULTICAST  MTU:1500  Metric:1
          RX packets:9636 errors:0 dropped:0 overruns:0 frame:0
          TX packets:9777 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0
          RX bytes:463816 (463.8 KB)  TX bytes:21547784 (21.5 MB)

eth0      Link encap:Ethernet  HWaddr 08:00:27:3a:0b:5b
          inet addr:10.0.2.15  Bcast:10.0.2.255  Mask:255.255.255.0
          inet6 addr: fe80::a00:27ff:fe3a:b5b/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:108584 errors:0 dropped:0 overruns:0 frame:0
          TX packets:46714 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000
          RX bytes:95861046 (95.8 MB)  TX bytes:3140434 (3.1 MB)

lo        Link encap:Local Loopback
          inet addr:127.0.0.1  Mask:255.0.0.0
          inet6 addr: ::1/128 Scope:Host
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:24 errors:0 dropped:0 overruns:0 frame:0
          TX packets:24 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0
          RX bytes:202797 (202.7 KB)  TX bytes:202797 (202.7 KB)
```

可见 host 模式直接共享了 host machine，`vet`那块 interface 也没有出现

```
vagrant@vagrant-ubuntu-trusty-64:~$ docker run -it --net="host" debian /bin/bash
root@vagrant-ubuntu-trusty-64:/#
```

名字跟 host（这时是虚拟机）一模一样

```
root@vagrant-ubuntu-trusty-64:/# netstat -nr
Kernel IP routing table
Destination     Gateway         Genmask         Flags   MSS Window  irtt Iface
0.0.0.0         10.0.2.2        0.0.0.0         UG        0 0          0 eth0
10.0.2.0        0.0.0.0         255.255.255.0   U         0 0          0 eth0
172.17.0.0      0.0.0.0         255.255.0.0     U         0 0          0 docker0
```

> Mode: host
With the networking mode set to host a container will share the host’s network stack and all interfaces from the host will be available to the container. The container’s hostname will match the hostname on the host system. **Publishing ports and linking to other containers will not work** when sharing the host’s network stack. Note that `--add-host --hostname --dns --dns-search and --mac-address` is invalid in host netmode.

>Compared to the default bridge mode, the host mode gives significantly better networking performance since it uses the host’s native networking stack whereas the bridge has to go through one level of virtualization through the docker daemon. It is recommended to run containers in this mode when their networking performance is critical, for example, a production Load Balancer or a High Performance Web Server.

> Note: --net="host" gives the container full access to local system services such as D-bus and is therefore considered insecure.

host模式下的 container，做不了 port mapping，Dockerfile 里的 expose 过的 port 会直接占用 host machine，run 的时候被占用过的话，container 不会运行成功

这个不太 insecure，不过效率会好，networking 中 少用了一层 virtualization

### Docker Containers
containers 互相连接其某个服务，因为 ip不固定，所以不太好直接连接

* docker bridge，用`--link`，在 container 中可以使用 environmental variable，有具体的env=anotherContainerIP
* docker host-only，绑定 docker 端口到 host，直接通过 host 端口访问各自服务

### summary

* bridge 下，host 和 guest 可以互联，各自都有 IP（可以都是局域网 IP）「实际 automation 要注意这些 ip 都是动态分配的，不是很好处理」
* bridge 下，guest 跟 host 是直接连接，guest 连接外部网络需要走 guest 的 default gateway 和 host 的 gateway，多了一层 network virtualization，效率比起 host-only 会低一点
* NAT 下，host 只能通过 port forwarding 来访问 guest 服务，guest 可以通过 IP 直接连接 host
* 虚拟机和 docker 都可以虚拟网关，来完成网络转发，`vboxnet`和`docker0`相当于是辅助的，都可以虚拟出一个子网供 box 和 container 使用。也可以理解`vboxnet`和`docker0`为交换机，连接二个不同的局域网，每个交换机可以连接多个相同内网的 vboxes 和 containers。
* bridge 走的是 host 网络，NAT 走的是port forwarding，如果二个虚拟机，一个 bridge vbox，一个 NAT vbox，bridge vbox 是无法连接 NAT vbox的， 正如同host 无法连接 NAT vbox 一样
* docker网络跟 vbox 网络有类似的地方，也有不同。vbox bridge的 machine 跟 host 是一个网段的，而 docker bridge 的 container 跟 `docker0`是一个网段的

### 127.0.0.1 and 0.0.0.0

* 127.0.0.1 就是 localhost，永远是自己，网络传输中不会出现127.0.0.1的数据包
* `127.0.0.1/8`整个都是环回地址，用来测试本机的TCP/IP协议栈，发往这段A类地址数据包不会出网卡，网络设备不会对其做路由。
* 0.0.0.0 表示的是本地任意地址，匹配本机所有 IP
* In the context of servers, 0.0.0.0 means "all IPv4 addresses on the local machine". If a host has two ip addresses, 192.168.1.1 and 10.1.2.1, and a server running on the host listens on 0.0.0.0, it will be reachable at both of those IPs. 也就是说一台机器，它可能有内网（局域网）地址和外网地址，`bind-address`改为`0.0.0.0`之后，一个数据包是请求`10.1.2.1`这个公网地址也可以访问 mysql 了，而之前`bind-address=127.0.0.1`的时候，是只会监听 localhost 的请求，不会理会网络请求（目的地是公网对外地址的本机 IP`10.1.2.1`）。bind-adress 是监听地址，监听的地址，只有别人能访问到，才有可能监听到那个人的访问。也就是说，bind 的是局域网地址，外网用户是不可能访问到的，自然也就不能访问到数据库了。反之亦然。

### Switch and Router

router更多的是路由转发，有规则

switch 是分发，一根线变为多根线，第二层的东西，连接的是局域网

## TODO

http://www.cnblogs.com/rainman/archive/2013/05/06/3063925.html

vbox 中的 bridge，nat，host-only

1. bridge 创建的guest，跟 host 是一个地位，相当于在局域网又加入了一个主机，所以 host 跟 guest 是可以互联的。用的是 vmnet0 网络
2. nat 创建的 guest，在外界看来就是 host，走的是端口转发，host 只能通过 port forwarding 连接 guest 的服务，guest 可以连接 host。用的是 vmnet8 网络
3. host-only 创建的 guest，跟 host 一起是一个私有网络，guest 不能访问 internet，guest 和 host 可以互联
