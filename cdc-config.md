
From within the `chuck-norris-rental` OpenShift project.

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
        "database.user": "user",
        "database.password": "password",
        "database.server.id": "184054",
        "database.server.name": "rental",
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