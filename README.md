# 1repo-maven-proxy
Simple Proxy For Maven Repositories

## About
Simple proxy for the maven remote repositories. Artifact downloaded only once and placed to the local repo. Ideal to work with
releases, not snapshots.

## Build

`mvn package` will result in `target/1repo-maven.jar`

## Run

`java -jar 1repo-maven.jar`

## Configure

Place `application.properties` near the 1repo-maven.jar file with the following settings:

```
1repo.mavenDir=<place to put downloaded jars>
1repo.remoteRepo=<place where to take maven artifacts, by default it is http://repo1.maven.org/maven2/>
server.port=<port to listen, default is 8080>
```
