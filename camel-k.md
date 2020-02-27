In order to install Camel-K on OpenShift, you may follow these [setup instructions](https://camel.apache.org/camel-k/latest/installation/openshift.html).

You may also just retrieve the `kamel` CLI tool from release page and then install the [Camel K Operator](https://operatorhub.io/operator/camel-k) from the Operator Hub. We've used the `1.0.0-rc1` for our demonstration.

### Configuration

The route is expresed within the `camel-k-routes/route.xml` file.

Some elements may need to be adapted depending on your cluster setup.

First, this route was written to call the `chuck-norris-facts-api` through the 3scale API Management. Thus you'll find on line 11 to 14: the API Key used for gateway authentication and the Gateway route URL. Adapt this to your own environment or remove the header and adapt the route is you're jsut calling the API directly.

```
<setHeader name="user-key">
    <constant>CHUCK_NORRIS_FACT_API_KEY</constant>
</setHeader>
<to uri="http://chuck-norris-facts-apicast-chuck-movie-rental.apps.cluster-paris-2bd4.paris-2bd4.example.opentlc.com/camel/restsvc/fact" />
```

Then, on line 23 to 26, you'll find the Telegram chat id and authentication code we've used for our own demonstration. Please adapt it depending on your need.

```
<setHeader name="CamelTelegramChatId">
    <constant>TELEGRAM_CHAT_ID</constant>
</setHeader>
<to uri="telegram:bots?authorizationToken=TELEGRAM_AUTHORIZATION_TOKEN" />
```

### Deployment

Once installed and logged on the `chuck-movie-rental` project, just execute this following command from the `camel-k-routes` folder:

```
$ kamel run -d mvn:org.apache.camel:camel-jackson route.xml
```

After some seconds, check the build is finished:

```
$ oc get pods -n chuck-movie-rental | grep camel-k-kit
camel-k-kit-bpajajr4fckmj3f83o70-1-build               0/1       Completed   0          1m
camel-k-kit-bpajajr4fckmj3f83o70-builder               0/1       Completed   0          2m
```

and we've got an healthy pod running:

```
$ oc get pods -n chuck-movie-rental | grep route
route-85945f6764-r4n7x                                 1/1       Running     0          45s
```

### Trouble shooting

Look at the route pod logs:

```
$ oc logs $(oc get pods --field-selector=status.phase==Running | grep route | awk '{print $1}')
Starting the Java application using /opt/run-java/run-java.sh ...
exec java -Xmx768m -XX:ParallelGCThreads=1 -XX:ConcGCThreads=1 -Djava.util.concurrent.ForkJoinPool.common.parallelism=1 -XX:CICompilerCount=2 -XX:+UseParallelGC -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=40 -XX:+ExitOnOutOfMemoryError -cp ./resources:/etc/camel/conf:/etc/camel/resources:/etc/camel/sources/i-source-000:dependencies/com.fasterxml.jackson.core.jackson-annotations-2.10.1.jar:dependencies/com.fasterxml.jackson.core.jackson-core-2.10.1.jar:dependencies/com.fasterxml.jackson.core.jackson-databind-2.10.1.jar:dependencies/com.fasterxml.jackson.jaxrs.jackson-jaxrs-base-2.10.1.jar:dependencies/com.fasterxml.jackson.jaxrs.jackson-jaxrs-json-provider-2.10.1.jar:dependencies/com.fasterxml.jackson.module.jackson-module-jaxb-annotations-2.10.1.jar:dependencies/com.fasterxml.woodstox.woodstox-core-5.0.3.jar:dependencies/com.github.ben-manes.caffeine.caffeine-2.8.0.jar:dependencies/com.github.luben.zstd-jni-1.4.0-1.jar:dependencies/com.sun.activation.javax.activation-1.2.0.jar:dependencies/com.sun.istack.istack-commons-runtime-3.0.8.jar:dependencies/com.sun.xml.bind.jaxb-core-2.3.0.1.jar:dependencies/com.sun.xml.bind.jaxb-impl-2.3.0.jar:dependencies/com.sun.xml.fastinfoset.FastInfoset-1.2.16.jar:dependencies/commons-codec.commons-codec-1.11.jar:dependencies/commons-io.commons-io-2.6.jar:dependencies/commons-logging.commons-logging-1.2.jar:dependencies/jakarta.activation.jakarta.activation-api-1.2.1.jar:dependencies/jakarta.ws.rs.jakarta.ws.rs-api-2.1.5.jar:dependencies/jakarta.xml.bind.jakarta.xml.bind-api-2.3.2.jar:dependencies/javax.annotation.javax.annotation-api-1.3.2.jar:dependencies/javax.servlet.javax.servlet-api-3.1.0.jar:dependencies/javax.xml.bind.jaxb-api-2.3.0.jar:dependencies/org.apache.camel.camel-api-3.0.0.jar:dependencies/org.apache.camel.camel-attachments-3.0.0.jar:dependencies/org.apache.camel.camel-base-3.0.0.jar:dependencies/org.apache.camel.camel-bean-3.0.0.jar:dependencies/org.apache.camel.camel-caffeine-lrucache-3.0.0.jar:dependencies/org.apache.camel.camel-cloud-3.0.0.jar:dependencies/org.apache.camel.camel-core-engine-3.0.0.jar:dependencies/org.apache.camel.camel-file-3.0.0.jar:dependencies/org.apache.camel.camel-http-3.0.0.jar:dependencies/org.apache.camel.camel-http-common-3.0.0.jar:dependencies/org.apache.camel.camel-jackson-3.0.0.jar:dependencies/org.apache.camel.camel-jaxp-3.0.0.jar:dependencies/org.apache.camel.camel-kafka-3.0.0.jar:dependencies/org.apache.camel.camel-main-3.0.0.jar:dependencies/org.apache.camel.camel-management-api-3.0.0.jar:dependencies/org.apache.camel.camel-support-3.0.0.jar:dependencies/org.apache.camel.camel-telegram-3.0.0.jar:dependencies/org.apache.camel.camel-util-3.0.0.jar:dependencies/org.apache.camel.camel-util-json-3.0.0.jar:dependencies/org.apache.camel.camel-webhook-3.0.0.jar:dependencies/org.apache.camel.k.camel-k-loader-xml-1.0.9.jar:dependencies/org.apache.camel.k.camel-k-runtime-core-1.0.9.jar:dependencies/org.apache.camel.k.camel-k-runtime-main-1.0.9.jar:dependencies/org.apache.camel.spi-annotations-3.0.0.jar:dependencies/org.apache.commons.commons-lang3-3.9.jar:dependencies/org.apache.cxf.cxf-core-3.3.4.jar:dependencies/org.apache.cxf.cxf-rt-frontend-jaxrs-3.3.4.jar:dependencies/org.apache.cxf.cxf-rt-rs-client-3.3.4.jar:dependencies/org.apache.cxf.cxf-rt-security-3.3.4.jar:dependencies/org.apache.cxf.cxf-rt-transports-http-3.3.4.jar:dependencies/org.apache.httpcomponents.httpclient-4.5.10.jar:dependencies/org.apache.httpcomponents.httpcore-4.4.12.jar:dependencies/org.apache.kafka.kafka-clients-2.3.1.jar:dependencies/org.apache.logging.log4j.log4j-api-2.13.0.jar:dependencies/org.apache.logging.log4j.log4j-core-2.13.0.jar:dependencies/org.apache.logging.log4j.log4j-slf4j-impl-2.13.0.jar:dependencies/org.apache.ws.xmlschema.xmlschema-core-2.2.4.jar:dependencies/org.codehaus.woodstox.stax2-api-3.1.4.jar:dependencies/org.glassfish.jaxb.jaxb-runtime-2.3.2.jar:dependencies/org.glassfish.jaxb.txw2-2.3.2.jar:dependencies/org.jvnet.staxex.stax-ex-1.8.1.jar:dependencies/org.lz4.lz4-java-1.6.0.jar:dependencies/org.slf4j.slf4j-api-1.7.29.jar:dependencies/org.xerial.snappy.snappy-java-1.1.7.3.jar org.apache.camel.k.main.Application
2020-02-25 15:27:23.718 INFO  [main] ApplicationRuntime - Add listener: org.apache.camel.k.listener.ContextConfigurer@9225652
2020-02-25 15:27:23.725 INFO  [main] ApplicationRuntime - Add listener: org.apache.camel.k.listener.RoutesConfigurer@50b472aa
2020-02-25 15:27:23.726 INFO  [main] ApplicationRuntime - Add listener: org.apache.camel.k.listener.RoutesDumper@3911c2a7
2020-02-25 15:27:23.819 INFO  [main] RuntimeSupport - Looking up loader for language: xml
2020-02-25 15:27:23.826 INFO  [main] RuntimeSupport - Found loader org.apache.camel.k.loader.xml.XmlSourceLoader@52e6fdee for language xml from service definition
2020-02-25 15:27:23.830 INFO  [main] RoutesConfigurer - Loading routes from: file:/etc/camel/sources/i-source-000/route.xml?language=xml
2020-02-25 15:27:23.830 INFO  [main] ApplicationRuntime - Listener org.apache.camel.k.listener.RoutesConfigurer@50b472aa executed in phase ConfigureRoutes
2020-02-25 15:27:23.917 INFO  [main] BaseMainSupport - Using properties from: file:/etc/camel/conf/application.properties
2020-02-25 15:27:27.310 INFO  [main] ApplicationRuntime - Listener org.apache.camel.k.listener.ContextConfigurer@9225652 executed in phase ConfigureContext
2020-02-25 15:27:27.311 INFO  [main] DefaultCamelContext - Apache Camel 3.0.0 (CamelContext: camel-k) is starting
2020-02-25 15:27:27.313 INFO  [main] DefaultManagementStrategy - JMX is disabled
2020-02-25 15:27:28.737 INFO  [main] HttpComponent - Created ClientConnectionManager org.apache.http.impl.conn.PoolingHttpClientConnectionManager@721eb7df
2020-02-25 15:27:29.228 INFO  [main] DefaultCamelContext - StreamCaching is not in use. If using streams then its recommended to enable stream caching. See more details at http://camel.apache.org/stream-caching.html
2020-02-25 15:27:29.435 WARN  [main] JacksonDataFormat - The option autoDiscoverObjectMapper is set to false, Camel won't search in the registry
2020-02-25 15:27:29.827 INFO  [main] KafkaConsumer - Starting Kafka consumer on topic: rental-chuck-norris with breakOnFirstError: false
2020-02-25 15:27:29.909 INFO  [main] ConsumerConfig - ConsumerConfig values: 
	allow.auto.create.topics = true
	auto.commit.interval.ms = 5000
	auto.offset.reset = latest
	bootstrap.servers = [my-cluster-kafka-bootstrap.amq-streams.svc.cluster.local:9092]
	check.crcs = true
	client.dns.lookup = default
	client.id = 
	client.rack = 
	connections.max.idle.ms = 540000
	default.api.timeout.ms = 60000
	enable.auto.commit = true
	exclude.internal.topics = true
	fetch.max.bytes = 52428800
	fetch.max.wait.ms = 500
	fetch.min.bytes = 1
	group.id = camelk
	group.instance.id = null
	heartbeat.interval.ms = 3000
	interceptor.classes = []
	internal.leave.group.on.close = true
	isolation.level = read_uncommitted
	key.deserializer = class org.apache.kafka.common.serialization.StringDeserializer
	max.partition.fetch.bytes = 1048576
	max.poll.interval.ms = 300000
	max.poll.records = 500
	metadata.max.age.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	partition.assignment.strategy = [org.apache.kafka.clients.consumer.RangeAssignor]
	receive.buffer.bytes = 65536
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 40000
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	send.buffer.bytes = 131072
	session.timeout.ms = 10000
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2, TLSv1.1, TLSv1]
	ssl.endpoint.identification.algorithm = https
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLS
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS
	value.deserializer = class org.apache.kafka.common.serialization.StringDeserializer

2020-02-25 15:27:35.033 WARN  [main] ConsumerConfig - The configuration 'specific.avro.reader' was supplied but isn't a known config.
2020-02-25 15:27:35.112 INFO  [main] AppInfoParser - Kafka version: 2.3.1
2020-02-25 15:27:35.112 INFO  [main] AppInfoParser - Kafka commitId: 18a913733fb71c01
2020-02-25 15:27:35.113 INFO  [main] AppInfoParser - Kafka startTimeMs: 1582644455033
2020-02-25 15:27:35.115 INFO  [Camel (camel-k) thread #2 - KafkaConsumer[rental-chuck-norris]] KafkaConsumer - Subscribing rental-chuck-norris-Thread 0 to topic rental-chuck-norris
2020-02-25 15:27:35.115 INFO  [main] DefaultCamelContext - Route: kafkaListener started and consuming from: kafka://rental-chuck-norris?brokers=my-cluster-kafka-bootstrap.amq-streams.svc.cluster.local%3A9092&groupId=camelk
2020-02-25 15:27:35.117 INFO  [Camel (camel-k) thread #2 - KafkaConsumer[rental-chuck-norris]] KafkaConsumer - [Consumer clientId=consumer-1, groupId=camelk] Subscribed to topic(s): rental-chuck-norris
2020-02-25 15:27:35.121 INFO  [main] DefaultCamelContext - Total 1 routes, of which 1 are started
2020-02-25 15:27:35.123 INFO  [main] DefaultCamelContext - Apache Camel 3.0.0 (CamelContext: camel-k) started in 7.810 seconds
2020-02-25 15:27:35.124 INFO  [main] ApplicationRuntime - Listener org.apache.camel.k.listener.RoutesDumper@3911c2a7 executed in phase Started
2020-02-25 15:27:35.913 INFO  [Camel (camel-k) thread #2 - KafkaConsumer[rental-chuck-norris]] Metadata - [Consumer clientId=consumer-1, groupId=camelk] Cluster ID: OZP89e4ISIGjdXbNsBTD8Q
2020-02-25 15:27:35.915 INFO  [Camel (camel-k) thread #2 - KafkaConsumer[rental-chuck-norris]] AbstractCoordinator - [Consumer clientId=consumer-1, groupId=camelk] Discovered group coordinator my-cluster-kafka-1.my-cluster-kafka-brokers.amq-streams.svc.cluster.local:9092 (id: 2147483646 rack: null)
2020-02-25 15:27:35.920 INFO  [Camel (camel-k) thread #2 - KafkaConsumer[rental-chuck-norris]] ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=camelk] Revoking previously assigned partitions []
2020-02-25 15:27:35.920 INFO  [Camel (camel-k) thread #2 - KafkaConsumer[rental-chuck-norris]] AbstractCoordinator - [Consumer clientId=consumer-1, groupId=camelk] (Re-)joining group
2020-02-25 15:27:36.008 INFO  [Camel (camel-k) thread #2 - KafkaConsumer[rental-chuck-norris]] AbstractCoordinator - [Consumer clientId=consumer-1, groupId=camelk] (Re-)joining group
2020-02-25 15:27:39.017 INFO  [Camel (camel-k) thread #2 - KafkaConsumer[rental-chuck-norris]] AbstractCoordinator - [Consumer clientId=consumer-1, groupId=camelk] Successfully joined group with generation 35
2020-02-25 15:27:39.021 INFO  [Camel (camel-k) thread #2 - KafkaConsumer[rental-chuck-norris]] ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=camelk] Setting newly assigned partitions: rental-chuck-norris-0
2020-02-25 15:27:39.031 INFO  [Camel (camel-k) thread #2 - KafkaConsumer[rental-chuck-norris]] ConsumerCoordinator - [Consumer clientId=consumer-1, groupId=camelk] Setting offset for partition rental-chuck-norris-0 to the committed offset FetchPosition{offset=19, offsetEpoch=Optional[0], currentLeader=LeaderAndEpoch{leader=my-cluster-kafka-0.my-cluster-kafka-brokers.amq-streams.svc.cluster.local:9092 (id: 0 rack: ), epoch=0}}
```