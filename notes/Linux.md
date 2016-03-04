#### Download files
```
# file
scp _my_account@_server_ip:_file_location _local_file_location
# folder
scp -r _my_account@_server_ip:/opt/scripts ~/scripts/
```

###### files with different user
```
# server
cp -r /target /tmp/whatever && chmod 777 /tmp/whatever

# host
scp -r _my_account@_server_ip:/tmp/whatever ~/Downloads/

```
[chmod all files under directory](https://github.com/404pilot/DemoX/blob/master/notes/Linux.md#permission)


#### test port is opened or not

    telnet 127.0.0.1 8080

    exec 6<>/dev/tcp/127.0.0.1/8080 || echo "No one is listening"

    netstat -tulpn | grep :80
    
    netstat -nat | grep 3306
    netstat -nat | grep LISTEN

    lsof -n -P -i TCP -s TCP:LISTEN

#### all users

    cut -d: -f1 /etc/passwd

    cat /etc/passwd

#### disk usage for a folder

    du -h --max-depth=0 jenkins_home

#### exit ssh connection

type `enter` + `~` + `.`

#### os version

```
uname -r
cat /etc/*-release
```

#### if ;else ;then; fi

    if ! id -u vagrant; then echo "not exist" ;else echo 'exist' ;fi;

#### run command under another directory without switching

    ( cd another_dir && ./run_shell.sh )

#### permission

```
find /dir -type d -exec chmod 755 {} \;
find /dir -type f -exec chmod 644 {} \;
find /dir -type f -name "*.sh" -exec chmod 744 {} \;

# X means only giving eXecutable permission for directories 
# and don't change a file's executable permission (it is what it was)
chmod -R u=rwX,g=rX,o=rX .
```

Directory (permission for its sub-directories and sub-files):

r - ls (read dir lists)
w - touch, mkdir, rm
x - cd (enter into dir)

#### files under a folder and its subfolders

```
# delete all
$ find . -name 'usagestand.*' -type f -delete

# delete current location
find *1.mp3 -delete

$ find . -name 'usagestand.atomFeedUrls.properties' -type f -exec cat {} + | grep ord1
```

#### configs for shell

* `ssh` and `su - ` -> **interactive login shell**
    1. `/etc/profile`
    2. `~/.profile`
* `bash -c "command"`
    1. do nothing
* "daily use" -> **interactive non-login shell**
    1. `/etc/bash.bashrc`
    2. `~/.bashrc`
* `ssh server "command"`
    1. `/etc/bash.bashrc`
    2. `~/.bashrc`

> https://wido.me/sunteya/understand-bashrc-and-profile
