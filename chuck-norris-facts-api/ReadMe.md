# Spring Boot with camel and other useful things chuck-norris-facts-api 

## To build this project use

```
mvn install
```

## To run this project with Maven use

```
mvn spring-boot:run
```


## For testing

```
curl http://localhost:8090/camel/api-docs
curl http://localhost:8090/camel/ping
```


## Acces Swagger UI with definition

```
http://localhost:8090/webjars/swagger-ui/3.22.0/index.html?url=/camel/api-docs
```

## Call the ping rest operation
```
curl http://localhost:8090/camel/restsvc/ping
```