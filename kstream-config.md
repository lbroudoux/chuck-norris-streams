
From the 3 topics created by Debezium, we want to produce following message on `rental-chuck-norris` output topic:

```
{
  "rental": {
    "id": 1,
    "user_id": 1,
    "movie_id": 1,
    "start_date": "2019-05-16",
    "rental_duration": 3
  },
  "movie": {
    "id": 1,
    "title": "The Delta Force",
    "year": 1986,
    "main_actor": "Chuck Norris"
  },
  "customer": {
    "id": 1,
    "first_name": "Laurent",
    "last_name": "Broudoux",
    "twitter_handle": "@lbroudoux"
  }
}
```

### Create target Topic

From the `chuck-norris-filter-kstreams` folder, declare topic into the `amq-streams` OpenShift project, where our Strimzi cluster leaves:

```
$ oc apply -f topic.yaml -n amq-streams
```

### Build a plain Java Kafka Stream Application

#### Build the Docker Image and push it to registry

From the `chuck-norris-filter-kstreams` folder (you need to replace `lbroudoux` with your own username):

```
$ mvn clean package
$ docker build -t quay.io/lbroudoux/chuck-norris-filter . 
$ docker push quay.io/lbroudoux/chuck-norris-filter:latest 
```

#### Deploy the Kafka Streams application

From the `chuck-movie-rental` OpenShift project (you need to adapt file before with your own image name):

```
$ oc apply -f deployment.yaml -n chuck-norris-rental
```

### Alternative: Build a Quarkus native Kafka Stream Application

### Build the Docker Image and push it to registry

From the `chuck-norris-filter-kstreams-quarkus` folder (you need to replace `lbroudoux` with your own username):

```
$ docker build --no-cache -f src/main/docker/Dockerfile.multistage -t quay.io/lbroudoux/chuck-norris-filter-quarkus .
$ docker push quay.io/lbroudoux/chuck-norris-filter-quarkus:latest
```

#### Deploy the Kafka Streams application

From the `chuck-movie-rental` OpenShift project (you need to adapt file before with your own image name):

```
$ oc apply -f deployment.yaml -n chuck-norris-rental
```

#### Differences from plain Java application

* Model Objects have to be annotated with `@io.quarkus.runtime.annotations.RegisterForReflection` to allow serialization using Jackson,
* When using Jackson JSON serialization, you have to add `@com.fasterxml.jackson.annotation.JsonProperty` on both constructor properties and class properties otherwise field name are used serialization and deserilization can not be done anymore,
* In the current Quarkus version (1.2.1) there's an issue regarding conversion to native app for Kafka Streams. I had to apply workaround provided here: https://github.com/quarkusio/quarkus/issues/7066 (see `JNIRegistrationFeature` class).