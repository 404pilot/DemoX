
### port

#### test port is opened or not

    telnet 127.0.0.1 8080

#### all users

    cut -d: -f1 /etc/passwd
    
    cat /etc/passwd

#### disk usage for a folder

    du -h --max-depth=0 jenkins_home

#### os version

    uname -r
    cat /etc/*-release

#### if ;else ;then; fi

    if ! id -u vagrant; then echo "not exist" ;else echo 'exist' ;fi;
