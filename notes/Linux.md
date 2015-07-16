
### port

#### test port is opened or not

    telnet 127.0.0.1 8080

    exec 6<>/dev/tcp/127.0.0.1/8080 || echo "No one is listening"

    netstat -tulpn | grep :80

#### all users

    cut -d: -f1 /etc/passwd

    cat /etc/passwd

#### disk usage for a folder

    du -h --max-depth=0 jenkins_home

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
