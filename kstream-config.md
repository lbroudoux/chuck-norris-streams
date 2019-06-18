
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

### Build the Docker Image and push it to registry

From the `chuck-norris-filter-kstreams` folder (you need to replace `lbroudoux` with your own username):

```
$ mvn clean package
$ docker build -t quay.io/lbroudoux/chuck-norris-filter . 
$ docker push quay.io/lbroudoux/chuck-norris-filter:latest 
```

### Deploy the Kafka Streams application

From the `chuck-movie-rental` OpenShift project (you need to adapt file before with your own image name):

```
$ oc apply -f deployment -n chuck-norris-rental
```