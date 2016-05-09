##Linux

### useful commands

##### look up users
	ls /home/

##### assgin command result to a variable
	a=$(ls | grep cassandra)
	echo $(java -version)
	
##### foreground & background job/task
	gitk &
or

    ctrl+z
    bg

`ctrl+z` will suspend current foreground job
	
	jobs || ps ua
	fg #job_id

list all background jobs and bring the specific background job back to foreground

#### link app

	echo $PATH				// find /usr/local/bin is the top level of path
	
	sudo ln -s "/opt/homebrew-cask/Caskroom/sublime-text/2.0.2/Sublime Text 2.app" /usr/local/bin/sublime

##### get current Git location

	which git

##### port
	sudo netstat -taupen
	sudo netstat -antp
	sudo netstat -tlnp | grep mysql

##### ps
	# BSD style
	ps axu
	
	# standard style
	ps -ef
	
##### run previous command
	
	# search
	ctrl + r; ctrl + r
	
	# stop
	ctrl + g
	
##### delete files with certain format in subdirectories
	
	find . -name 'usagestand.*' -type f -delete
	
##### delete files in current directory
	
	find *1.mp3 -delete
	
##### copy file to remote machine

	scp foobar.txt your_username@remotehost.edu:~/some/remote/directory
	
##### download logs from server
	# download a single file
	scp _my_account@_server_ip:_file_location _local_file_location
	# download all files under a folder
	scp -r _my_account@_server_ip:/opt/scripts ~/scripts/
	
##### print files location
	readlink -f server.log_2014-07-26T04-05-29
	
##### retrieve ssh finger print

	ssh-keygen -lf ~/.ssh/id_rsa.pub
	
##### pass parameters to shell script

	if [ $# -ne 2 ]
	then
	    echo "There should be two parameters"
	    exit 1
	fi
	
	Var_01=$1
	Var_02=$2

##### others

	ls -a file
	ls -d directory
	ls -a -l -d -C

; all, long, directory, Column

	cp -R source1 source2 destination
; recursive (for directory)

	# list files sorted by date
	ls -lrt

    
	rm -r ~/directory
	rm -i ~/important-direcotry
	mkdir -p parent/child
; recursive ; interactive ; parent

	more file
`space`,`f` --> next page `enter` --> next line

	tail -20 file
	tail -f serverlog
; follow

	ln sourceFile hardLinkFile
	ln -s sourceFile softLinkFile
	
##### disable alias
	# diable any alias like 'alias curl='curl --some --default --options''
	\curl -L https://get.rvm.io | bash -s stable

##### other
    file unknown-type-file 
    mv linux.md ~/notes/
	mv linux.md linux.md.bak (rename)
    sudo xkill
    ll | more
    # quiet mode
    wget -q -O /tmp/cassandra.tar.gz ${CASSANDRA_DOWNLOAD_URL}

----------

### permission

    d|rwx |---  |---	|2	   		|root|root |4096
     |user|group|others	|hard links	|user|group|file size(not accuracy,min data size storage is block (512 byte))

`d`:directory; `-`:binary file; `l`:link

`rwx`: read, write, executable

##### file | directory permission
File:

	r - cat, more, head, tail
	w - echo, vi
	x - command(ls,cd), script

Directory (permission for its sub-directories and sub-files):

	r - ls (read dir lists)
	w - touch, mkdir, rm
	x - cd (enter into dir)

##### change
	chmod u+wx linux.md
	chmod o-rx linux.md
	chmod g=rwx linux.md

user give `wx`

others remove `rx`

group set to `rwx`

> r --4 = 2^2
> 
> w --2 = 2^1
> 
> x --1 = 2^0

2+1=3, so 3 is not used. we could easily infer the mode from number

> rwx = 7
> 
> rwx-xr = 754

	chown neatpilot linux.md
	chown root linux.md
	chgrp neatpilot linux.md
	chgrp root linux.md

##### grant

	su -username

----------

### compress & package

##### gzip & gunzip
	
	gzip test.md	
	gunzip test.md.gz

`gunzip` is the hard link of `gzip`, thus `gzip test.md.gz` also works

`gzip` only zip file not directory => `tar` a directory to a single file

##### zip

	zip file.zip file
	zip -r dir.zip dir

	unzip test.zip

##### tar (.tar.gz)

	tar -zcv -f directory.tar.gz directory

`c`: create to a file
`v`: verbose
`f`: specify file name
`z`: with zip compress

	tar -zxv -f test.tar.gz
	tar -xv -f test.tar
`x` : extract
`f`: file name
`z`: unzip


----------

### termination

- **`SIGHUP (1)`** - Hangup detected on controlling terminal or death of controlling process. Use SIGHUP to reload configuration files and open/close log files.
- **`SIGKILL (9)`** - Kill signal. Use SIGKILL as a last resort to kill process. This will not save data or cleaning kill the process.
- **`SIGTERM (15)`** - Termination signal. This is the default and safest way to kill process.

<b></b>

    pidof chrome
    ps -aux | grep chrome
    
    (sudo) kill _#pid
    kill -15 _#pid || kill -SIGTERM _#pid
    kill -9 _#pid || kill -SIGKILL _#pid
    
    killall _processName
    killall -15 chrome
    killall -9 chrome

----------

### config

`/etc/paths` set path `$PATH` for finding commands

`/etc/shells` set shells 

`chsh -s /usr/local/bin/zsh` set zsh to default shell

`/etc/profile` --> System-wide .profile for sh

----------

### summary

. file : hidden file

Linux file naming rule

1. never use [space], [tab], [backspace], [@#$%-]
2. never use . (hidden file)
3. case sensitive


root-enabled command:
/sbin; /usr/sbin

all users:
/bin; /usr/bin

###### translation:

	ls: list
	cd: change directory
	pwd: print working directory
	mkdir: make directories
	cp: copy
	mv: move
	rm: remove
	cat: concatenate and display files
	ln: link
	chmod: change file mode bits
	su: switch user
	chown: change file ownship
	chgrp: change file group ownership
	umask: 
	gzip: GNU zip
	gunzip: GNU unzip

NOTE: in linux, directory is a special file, but it always should considered as a unique different thing, such as permission
