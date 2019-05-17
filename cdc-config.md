
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

#### PostgreSQL attempt

```
oc exec -i $(oc get pods | grep debezium-connect | awk '{print $1}') -- curl -X POST \
    -H "Accept:application/json" \
    -H "Content-Type:application/json" \
    http://localhost:8083/connectors -d @- <<'EOF'

{
    "name": "rental-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "tasks.max": "1",
        "database.hostname": "postgresql",
        "database.port": "5432",
        "database.user": "postgres",
        "database.password": "password",
        "database.dbname": "postgres",
        "database.server.name": "rental",
        "database.history.kafka.bootstrap.servers": "my-cluster-kafka-bootstrap.amq-streams.svc.cluster.local:9092",
        "database.history.kafka.topic": "schema-changes.rental",
        "transforms": "unwrap",
        "transforms.unwrap.type":"io.debezium.transforms.UnwrapFromEnvelope",
        "transforms.unwrap.drop.tombstones":"false",
        "snapshot.mode": "initial"
    }
}
EOF
```

#### MySQL version

```
oc exec -i $(oc get pods | grep debezium-connect | awk '{print $1}') -- curl -X POST \
    -H "Accept:application/json" \
    -H "Content-Type:application/json" \
    http://localhost:8083/connectors -d @- <<'EOF'

{
    "name": "rental-connector",
    "config": {
        "connector.class": "io.debezium.connector.mysql.MySqlConnector",
        "tasks.max": "1",
        "database.hostname": "mysql",
        "database.port": "3306",
        "database.user": "debezium",
        "database.password": "dbz",
        "database.server.id": "184054",
        "database.server.name": "mysqldb",
        "database.history.kafka.bootstrap.servers": "my-cluster-kafka-bootstrap.amq-streams.svc.cluster.local:9092",
        "database.history.kafka.topic": "schema-changes.rental",
        "transforms": "unwrap",
        "transforms.unwrap.type":"io.debezium.transforms.UnwrapFromEnvelope",
        "transforms.unwrap.drop.tombstones":"false",
        "snapshot.mode": "schema_only_recovery"
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
    http://localhost:8083/connectors/rental-connector
```

```
oc exec -i $(oc get pods | grep debezium-connect | awk '{print $1}') -- curl \
    -H "Accept:application/json" \
    -XDELETE http://localhost:8083/connectors/rental-connector
```





oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.inventory.customer

oc exec -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --topic dbserver1.inventory.customer

oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --topic dbserver1.inventory.rental

oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --from-beginning \
    --property print.key=true \
    --topic connect-cluster-configs


oc exec -n amq-streams -it my-cluster-kafka-0 -- /opt/kafka/bin/kafka-topics.sh \
    --zookeeper my-cluster-zookeeper-client:2181 \
    --list


"payload":{"before":null,"after":{"id":1,"first_name":"alain","last_name":"pham","twitter_handle":"@koint"},"source":{"version":"0.9.5.Final","connector":"mysql","name":"dbserver1","server_id":223344,"ts_sec":1558021643,"gtid":null,"file":"mysql-bin.000003","pos":5010,"row":0,"snapshot":false,"thread":31,"db":"inventory","table":"customer","query":null},"op":"c","ts_ms":1558021643176}}

payload":{"before":null,"after":{"id":2,"first_name":"laurent","last_name":"broudoux","twitter_handle":"@lbroudoux"},"source":{"version":"0.9.5.Final","connector":"mysql","name":"dbserver1","server_id":223344,"ts_sec":1558021643,"gtid":null,"file":"mysql-bin.000003","pos":5357,"row":0,"snapshot":false,"thread":31,"db":"inventory","table":"customer","query":null},"op":"c","ts_ms":1558021643176}}



"payload":{"before":null,"after":{"id":1,"rental_duration":7,"start_date":1558021764000,"customer_id":1,"movie_id":2},"source":{"version":"0.9.5.Final","connector":"mysql","name":"dbserver1","server_id":223344,"ts_sec":1558021763,"gtid":null,"file":"mysql-bin.000003","pos":5708,"row":0,"snapshot":false,"thread":21,"db":"inventory","table":"rental","query":null},"op":"c","ts_ms":1558021763598}}


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
        "database.history.kafka.topic": "schema-changes.inventory",
        "snapshot.mode": "schema_only_recovery"
    }
}
EOF

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
        "database.history.kafka.topic": "schema-changes.inventory",
        "snapshot.mode": "when_needed"
    }
}
EOF


"transforms": "unwrap",
"transforms.unwrap.type":"io.debezium.transforms.UnwrapFromEnvelope",
"transforms.unwrap.drop.tombstones":"false",