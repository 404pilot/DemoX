#**Daily Quick Reference**

##### play it
    git add -i || git add -p

## Git

##### Delete remote branch
    git branch -d 'cyclomatic_test'
    git push origin :cyclomatic_test

##### Show unmerged version one stories
    git branch -a --no-merged master | grep B-


## Maven
    mvn clean integration-test
    mvn deploy -DskipTests
    mvn surefire-report:report-only -DskipTests
    mvn clean process-resources liquibase:update
    mvn clean test '-Dtest=org.specific_unit_test'
    mvn clean -Dtest=com.*.routes.EndToEnd*IT verify -DfailIfNoTests=false
    mvn clean -Dtest=com.*.routes.* verify -DfailIfNoTests=false
    mvn clean -Dtest=AtomUtilTest,PaginatedAtomRouteBuilderIT verify -DfailIfNoTests=false
    
## Linux

#### tar

    tar xvzf file-1.0.tar.gz

    tar xvjf file-1.0.tar.bz2

    tar xvf file-1.0.tar
    
#### termination

    pidof chrome
    ps -aux | grep chrome
    
    (sudo) kill _#pid
    kill -15 _#pid || kill -SIGTERM _#pid
    kill -9 _#pid || kill -SIGKILL _#pid
    
    killall _processName
    killall -15 chrome
    killall -9 chrome
    
#### other
    mv aFile aFile.bak
    ps aux | grep something
    ctrl + r & ctrl + g 
    sudo xkill
    ll | more
    ls -dr /opt
    tail -f _dynamic_file
    rm -r .dir/


#### gradle

    export GRADLE_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=9999,server=y,suspend=n"
    gradle jettyRun
