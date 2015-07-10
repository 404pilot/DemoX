
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

    uname -r
    cat /etc/*-release

#### if ;else ;then; fi

    if ! id -u vagrant; then echo "not exist" ;else echo 'exist' ;fi;

#### run command under another directory without switching

    ( cd another_dir && ./run_shell.sh )
