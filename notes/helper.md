
## Maven

``` shell
# displays phase, id, plugin, plugin's goal within a maven project
mvn fr.jcgay.maven.plugins:buildplan-maven-plugin:list

# show dependency
mvn dependency:tree

# show whole pom
mvn help:effective-pom

# available updates
mvn versions:display-plugin-updates

# plugin dependency
# `spotbugs-maven-plugin-3.1.1.jar` needs `maven-core-3.5.2.jar` -> it needs maven.3.5
mvn dependency:resolve-plugins
```
