
From within the `chuck-movie-rental` OpenShift project.

On OpenShift 3.11, we assume OpenShift templates from AMQ Streams or Strimzi packages have been made available to the project so that we can use the `oc new-app strimzi-connect-s2i` command.

> On OpenShift 4.x where AMQ Streams or Strimzi comes from Operator Hub packages, templates are no longer availables directly and we need to add them explicitely with following commands:

```
export STRIMZI_VERSION=0.15.0
git clone -b $STRIMZI_VERSION https://github.com/strimzi/strimzi-kafka-operator
cd strimzi-kafka-operator
oc project chuck-movie-rental
oc create -f examples/templates/cluster-operator/connect-s2i-template.yaml
cd ..
```

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

> For OpenShift 4.3 setup, we've tried upgrading the debezium version:

```
# Build a Debezium image
export DEBEZIUM_VERSION=1.0.1.Final
mkdir -p plugins && cd plugins && \
for PLUGIN in {mongodb,mysql,postgres}; do \
    curl https://repo1.maven.org/maven2/io/debezium/debezium-connector-$PLUGIN/$DEBEZIUM_VERSION/debezium-connector-$PLUGIN-$DEBEZIUM_VERSION-plugin.tar.gz | tar xz; \
done && \
oc start-build debezium-connect --from-dir=. --follow && \
cd .. && rm -rf plugins
```

> In order to start build on OpenShift 4, you may need to configure authentication to the regsitry.redhat.io registry. Have a look at https://access.redhat.com/RegistryAuthentication. You may want to create a service account token here: https://access.redhat.com/terms-based-registry/#/accounts. With this token, you'll need to create a pull secret and associate it with your build with the commands below:

```
oc create secret docker-registry my-service-account-pull-secret --docker-server=https://registry.redhat.io --docker-username='...' --docker-password='...' --docker-email='...' -n chuck-movie-rental
oc -n chuck-movie-rental secrets link default my-service-account-pull-secret --for=pull
oc -n chuck-movie-rental secrets link builder my-service-account-pull-secret
oc import-image debezium-connect-source:1.3.0 -n chuck-movie-rental
oc start-build debezium-connect --from-dir=. --follow -n chuck-movie-rental
```

### Declare the Debezium Connector within pod

#### MySQL version

```
oc exec -i $(oc get pods --field-selector=status.phase==Running | grep debezium-connect | awk '{print $1}') -- curl -X POST \
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
oc exec -i $(oc get pods --field-selector=status.phase==Running | grep debezium-connect | awk '{print $1}') -- curl \
    -H "Accept:application/json" \
    http://localhost:8083/connectors
```

And check connector details:

```
oc exec -i $(oc get pods --field-selector=status.phase==Running | grep debezium-connect | awk '{print $1}') -- curl \
    -H "Accept:application/json" \
    http://localhost:8083/connectors/rental-event-connector
```

```
oc exec -i $(oc get pods--field-selector=status.phase==Running | grep debezium-connect | awk '{print $1}') -- curl \
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