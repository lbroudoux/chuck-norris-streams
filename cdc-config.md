
From within the `chuck-movie-rental` OpenShift project.

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
oc exec -i $(oc get pods | grep debezium-connect | awk '{print $1}') -- curl -X POST \
    -H "Accept:application/json" \
    -H "Content-Type:application/json" \
    http://localhost:8083/connectors -d @- <<'EOF'
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
oc exec -i $(oc get pods | grep debezium-connect | awk '{print $1}') -- curl \
    -H "Accept:application/json" \
    http://localhost:8083/connectors
```

And check connector details:

```
oc exec -i $(oc get pods | grep debezium-connect | awk '{print $1}') -- curl \
    -H "Accept:application/json" \
    http://localhost:8083/connectors/rental-event-connector
```

```
oc exec -i $(oc get pods | grep debezium-connect | awk '{print $1}') -- curl \
    -H "Accept:application/json" \
    -XDELETE http://localhost:8083/connectors/rental-event-connector
```


### Trouble shooting

Listing existing topics on cluster:

```
oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-topics.sh \
    --zookeeper localhost:21810 \
    --list
```

Checking the differents topics content:

```
oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.inventory.customer

oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.inventory.movie

oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.inventory.rental

oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic connect-cluster-configs

oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic connect-cluster-offsets

oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic rental-chuck-norris

oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic events.rentals
```