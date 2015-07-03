# Network

IPv4长度是32bit，每一段8bit [0,255] (2^8)

`ifconfig`列出的都是 network interface，有些是真实的（有对应的 network adapter），有些是虚拟的。实际的 network adapter 肯定都有真实的 mac address

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

## Example
ip + netmask 决定了一个 network segment，同一个 segment 视为一个物理网段，的可以直接通信，不在的就扔给 default gateway 转发处理，一般 default gateway 也就是 router

* `boardcast`: 这个地址代表这个 segment 里的所有 host
* `lo0`比较特殊，就是自己，这个网段也没必要有个 boardcast addr



> 16 进制 hex，f 表示 15 (0b 1111)
interface | ip | netmask | segment
--- | --- | --- | ---
mac & vbox `lo0` | 127.0.0.1 | 0xff000000 255.0.0.0
mac `en0` | 192.168.1.103 | 0xffffff00 255.255.255.0 | 192.168.1.x
vbox `docker0` | 172.17.42.1 | 255.255.0.0 | 172.17.x.x
vbox `en0` | 10.0.2.15 | 255.255.255.0 | 10.0.2.x

**mac**:

```
$ ifconfig lo0

lo0: flags=8049<UP,LOOPBACK,RUNNING,MULTICAST> mtu 16384
	options=3<RXCSUM,TXCSUM>
	inet6 ::1 prefixlen 128
	inet 127.0.0.1 netmask 0xff000000
	inet6 fe80::1%lo0 prefixlen 64 scopeid 0x1
	nd6 options=1<PERFORMNUD>

$ ifconfig en0

en0: flags=8863<UP,BROADCAST,SMART,RUNNING,SIMPLEX,MULTICAST> mtu 1500
	ether 60:03:00:00:7e:00
	inet6 fe80::6203:8ff:fe90:7e8c%en0 prefixlen 64 scopeid 0x4
	inet 192.168.1.103 netmask 0xffffff00 broadcast 192.168.1.255
	nd6 options=1<PERFORMNUD>
	media: autoselect
	status: active
```
* `ether` -> mac address

**virtual box**:

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
```


```
# router

LAN
MAC Address:	A0-F3-C1-9A-3E-9E
IP Address:	192.168.0.254
Subnet Mask:	255.255.255.0

Wireless
Wireless Mode:	Router Mode
Wireless Radio:	Enable
Name (SSID):	Nexus
Channel:	Auto(Current Channel 6)
Mode:	11bgn mixed
Channel Width:	Auto
MAC Address:	A0-F3-*1-*A-*3-*E
WDS Status:	Disable

WAN
MAC Address:	F0-1F-AF-24-2E-6C
IP Address:	72.179.132.252	Dynamic IP
Subnet Mask:	255.255.240.0
Default Gateway:	72.179.128.1
DNS Server:	209.18.47.61 , 209.18.47.62
```

```
$ netstat -nr
Routing tables

Internet:
Destination        Gateway            Flags        Refs      Use   Netif Expire
default            192.168.1.1        UGSc           55      109     en0
127                127.0.0.1          UCS             0        6     lo0
127.0.0.1          127.0.0.1          UH              2    51560     lo0
169.254            link#4             UCS             0        0     en0
192.168.1          link#4             UCS             2        0     en0
192.168.1.1/32     link#4             UCS             1        0     en0
192.168.1.1        a0:f3:c1:9a:3e:9e  UHLWIir        57       25     en0    670
192.168.1.102      6c:ad:f8:8c:2f:4a  UHLWIi          3     4834     en0    156
192.168.1.103/32   link#4             UCS             0        0     en0
192.168.1.104      38:b1:db:8a:91:5b  UHLWIi          1      223     en0    740

Internet6:
Destination                             Gateway                         Flags         Netif Expire
::1                                     ::1                             UHL             lo0
fe80::%lo0/64                           fe80::1%lo0                     UcI             lo0
fe80::1%lo0                             link#1                          UHLI            lo0
fe80::%en0/64                           link#4                          UCI             en0
fe80::6203:8ff:fe90:7e8c%en0            60:3:8:90:7e:8c                 UHLI            lo0
fe80::%awdl0/64                         link#9                          UCI           awdl0
fe80::9850:e9ff:fedb:64ea%awdl0         9a:50:e9:db:64:ea               UHLI            lo0
ff01::%lo0/32                           ::1                             UmCI            lo0
ff01::%en0/32                           link#4                          UmCI            en0
ff01::%awdl0/32                         link#9                          UmCI          awdl0
ff02::%lo0/32                           ::1                             UmCI            lo0
ff02::%en0/32                           link#4                          UmCI            en0
ff02::%awdl0/32                         link#9                          UmCI          awdl0
```
