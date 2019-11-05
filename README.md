# CORS Filter [![Circle CI](https://circleci.com/gh/Rise-Vision/cors-filter.svg?style=svg)](https://circleci.com/gh/Rise-Vision/cors-filter)

## Introduction

The CORS Filter is a servlet filter used to handle CORS requests. It will inspect the incoming origin header and respond with the appropriate access-control headers.

## Built With
- Java (1.7)
- GAE (Google App Engine) 
- Maven
- [Wagon-Git](https://github.com/synergian/wagon-git)
- [Mockito](https://github.com/mockito/mockito)

## Development

### Local Development Environment Setup and Installation

* Maven 3 is required.

* Local build / test
``` bash
mvn clean install
mvn verify
```

### Dependencies
* Junit for testing 
* Mockito for mocking and testing
* Google App Engine SDK
* Wagon-Git for releasing the artifacts to [Rise Vision Maven Repository](https://github.com/Rise-Vision/mvn-repo)

### Usage
* Add CORS filter as dependency to your project 

```xml

<!-- Inside pom.xml of your project -->

<repositories>
  <repository>
    <id>mvn-repo-releases</id>
    <releases>
      <enabled>true</enabled>
    </releases>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
    <name>Maven Repository - Releases</name>
    <url>https://raw.github.com/Rise-Vision/mvn-repo/releases</url>
  </repository>
  <repository>
    <id>mvn-repo-snapshots</id>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
    <name>Maven Repository - Snapshots</name>
    <url>https://raw.github.com/Rise-Vision/mvn-repo/snapshots</url>
  </repository>
</repositories>

<!-- ... -->

<!-- In the <dependencies> section of your project's pom.xml -->
<dependency>
  <!-- From our private repo -->
  <groupId>com.risevision.cors</groupId>
  <artifactId>cors-filter</artifactId>
  <version>1.0.0</version>
</dependency>

<!-- ... -->
<!-- In your $HOME/.m2/settings.xml -->
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
  http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <localRepository/>
  <interactiveMode/>
  <usePluginRegistry/>
  <offline/>
  <pluginGroups/>
  <servers>
    <server>
      <id>mvn-repo-releases</id>
      <configuration>
        <httpHeaders>
          <property>
            <name>Authorization</name>
            <value>token {github auth token}</value>
          </property>
        </httpHeaders>
      </configuration>
    </server>

    <server>
      <id>mvn-repo-snapshots</id>
      <configuration>
        <httpHeaders>
          <property>
            <name>Authorization</name>
            <value>token {github auth token}</value>
          </property>
        </httpHeaders>
      </configuration>
    </server>
  </servers>
  <mirrors/>
  <proxies/>
  <profiles/>
  <activeProfiles/>
</settings>

```

* Configure the filter on the web.xml of your project. It accepts a list of api names which are the API class names. 

```xml
    
<filter>
    <filter-name>CorsFilter</filter-name>
    <filter-class>com.risevision.cors.filter.CorsFilter</filter-class>
    <init-param>
        <param-name>apis</param-name>
        <param-value>API_1_CLASS_NAME, API_2_CLASS_NAME</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>CorsFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

```

* You may need to update the dependency list in your project
```
mvn clean test -U
```
