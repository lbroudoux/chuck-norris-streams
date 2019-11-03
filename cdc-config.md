
From within the `amq-streams` OpenShift project.

### Create Debezium Connect pod

```
# Deploy a single instance of Kafka Connect with no plug-in installed on HA persistent Kafka
oc new-app strimzi-connect-s2i --name=debezium-connect -p CLUSTER_NAME=debezium -p KAFKA_CONNECT_BOOTSTRAP_SERVERS=my-cluster-kafka-bootstrap.amq-streams.svc.cluster.local:9092
```

### Launch a new build injecting plugins in Debezium Connect
```
# Build a Debezium image
export DEBEZIUM_VERSION=0.9.5.Final
mkdir -p plugins && cd plugins && \
for PLUGIN in {mongodb,mysql,postgres}; do \
    curl http://central.maven.org/maven2/io/debezium/debezium-connector-$PLUGIN/$DEBEZIUM_VERSION/debezium-connector-$PLUGIN-$DEBEZIUM_VERSION-plugin.tar.gz | tar xz; \
done && \
oc start-build debezium-connect --from-dir=. --follow && \
cd .. && rm -rf plugins
```

### Declare the Debezium Connector within pod

#### MySQL version

```
oc exec -i my-cluster-kafka-0 -c kafka -- curl -s -X POST \
    -H "Accept:application/json" \
    -H "Content-Type:application/json" \
    http://debezium-connect-api:8083/connectors -d @- <<'EOF'
{
    "name": "rental-event-connector",
    "config": {
        "connector.class": "io.debezium.connector.mysql.MySqlConnector",
        "tasks.max": "1",
        "database.hostname": "mysqldebezium.chuck-movie-rental.svc.cluster.local",
        "database.port": "3306",
        "database.user": "debezium",
        "database.password": "dbz",
        "database.server.id": "184054",
        "database.server.name": "dbserver1",
        "database.whitelist": "inventory",
        "database.history.kafka.bootstrap.servers": "my-cluster-kafka-bootstrap.amq-streams.svc.cluster.local:9092",
        "database.history.kafka.topic": "schema-changes.inventory"
    }
}
EOF
```

### Check connector existence and details

```
oc exec -i my-cluster-kafka-0 -c kafka -- curl -s \
    -H "Accept:application/json" \
    http://debezium-connect-api:8083/connectors
```

And check connector details:

```
oc exec -i my-cluster-kafka-0 -c kafka -- curl -s \
    -H "Accept:application/json" \
    http://debezium-connect-api:8083/connectors/rental-event-connector
```

```
oc exec -i my-cluster-kafka-0 -c kafka -- curl -s \
    -H "Accept:application/json" \
    -XDELETE http://debezium-connect-api:8083/connectors/rental-event-connector
```


### Trouble shooting

Listing existing topics on cluster:

```
oc exec -it my-cluster-zookeeper-0 -c zookeeper -- /opt/kafka/bin/kafka-topics.sh \
    --zookeeper localhost:21810 \
    --list
```

Checking the differents topics content:

```
oc exec -it my-cluster-kafka-0 -c kafka -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.inventory.customer

oc exec -it my-cluster-kafka-0 -c kafka -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.inventory.movie

oc exec -it my-cluster-kafka-0 -c kafka -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.inventory.rental

oc exec -it my-cluster-kafka-0 -c kafka -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic connect-cluster-configs

oc exec -it my-cluster-kafka-0 -c kafka -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic connect-cluster-offsets

oc exec -it my-cluster-kafka-0 -c kafka -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic rental-chuck-norris

oc exec -it my-cluster-kafka-0 -c kafka -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic events.rentals
```