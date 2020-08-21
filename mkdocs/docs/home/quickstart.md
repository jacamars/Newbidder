#Building the System
## Rudder-Transform

First step is to make a rudder-transforms docker image. This is not part of the CDP code base but CDP uses these transforms. This is the standard transforms build by Rudderstack. You will need to build the docker container for this.

Here are the instructions:

A. Either user the C1X cdp-bl code base, or git the standard rudderstack release.

B. Change directory to cdp-bl/rudder-transformer

C. Run docker build from there

$docker build -t c1x/r-transformer .
Now on to building the CDP components...

## JAVA Cluster and Service Components

This is the second step of the process. Here we make the Java components of the back-plane of CDP. Make the application using JAVA:

$make application
React Component

## Make the UI

This is the third step of the process. Makes the react components, if you have made changes to the control program:

$make ui
Docker Images

## Dockerize 

This is the packaging of everything together. Makes the docker image. Do this if you made changes in the application or UI.

$make dockerize
Docker containers are now built, and you can bring the system up.

DOCKER BASED Operations

These are the Docker instructions for working with CDP-Cluster, CDP-T Service, Rudderstack Transform

Docker Compose

Use Docker Compose to run all the components on your system

$docker-compose up -d
To run everything but CDP-Cluster and CDP-Tserver:

$docker-compose -f support.yml up -d
Working with Source

# SENDING EVENTS

There is a sample source, with 3 destinations already configured in the database. You can address this source in these sample programs with the value of 1Zg4SLMGY8rGkj7x1anRIWpMMOa.

##Send a batch of events

Assuming you have at least one CDP Cluster running and is mapped to localhost:8080 you can generate an event with:

$cd scripts
./generate-event 1Zg4SLMGY8rGkj7x1anRIWpMMOa localhost:8080
Download configuration

$cd scripts
./send-download localhost:8080
Upload a configuration

$cd scripts
./send-upload filename localhost:8080   
Load Representative Sample of All Event Types

$cd scripts
$./load-msgs 1Zg4SLMGY8rGkj7x1anRIWpMMOa localhost:8080
HANDY DOCKER ACTIVITIES

Running Containers

$docker ps
Attach to a Container

Do a docker ps and then use the container-id or name:

$docker exec -it <id-or-name> /bin/bash

##Attach to the log

Do a docker ps and then use the container-id or name:

$docker logs -f <id-or-name>

##Delete an image

Do a docker ls first

$docker image ls
Find the container id

$docker image rm <image-id> --force
Correct Checksum Error

If docker-compose complains about a checksum after you delete a container do this:

$docker-compose rm
Connect Intellij Debugger to CDP-Cluster or CDP-T Service:

# Configuration

Each of the docker components has its own set of environment variables. Here is a breakdown on those:

##Cluster
The cluster has environment variables to set up the different sub-systems used by it. The file Docker.env
contains the values for the macro substitions below"

```
    environment:
      OKTADOMAIN: "${OKTADOMAIN}"
      TOKENTIMEOUT: "300"
      MESSAGETIMEOUT: "90"
      HTTPS: "{$HTTPS}"
      ORGANIZATION: "${ORGANIZATION}"
      #INDEXPAGE: "/cdp-control"
      INDEXPAGE: "/control-plane-ui"
      PORT: "8080"
      WSPORT: "8887"
      BACKUPCOUNT: "3"
      READBACKUP: "true"
      THREADS: "128"
      KINESIS="${KINESIS}"
      S3="${S3}"
      JDBC: "${JDBC}"
      JDBCDRIVER: "org.postgresql.Driver"
      CONVERSION: "userId=properties[consumer.upmId]"
      MESSAGEFILTER: "userId != null AND properties[consumer.loginStatus] = 'logged in'"
```

Overview of the configuration parameters:

- OKTADOMAIN: This is the issuer domain for verification of session tokens.
- TOKENTIMEOUT: This is the timeout for api/login sessions, in minutes. Default is 30 mins. Here it is set for 5 hours.
- MESSAGETIMEOUT: The timeout, in days for messages stored in the cache. Default is forever, here it is set
to 90 days.

- HTTPS: Set to true for HTTPS (self signed cert) or false for HTTP. Default is false.
- WSPORT: The web services port, set to "8887" by default.
- KINESIS: For Kinesis input of messages to the system. Form kinesis://key=value&key=value...

    - writekey= Writekey to use when ingesting the messages
    - aws_access_key= AWS access key
    - aws_secret_key= AWS secret key
    - stream= The stream name.
    - shard_id= The shard id to use.
    - partition= The partition to use.
    - region= The region to use.
    - record_limit= The number of records to batch on ingest.
    - sleep_time= Number of seconds to wait between reads.
    - records= Number of records to process at a time
    - sleep= Sleep interval between ingests
    - create= Set to true to create the stream if it doesn't exist.
    
- S3: For S3 output/input. Form is s3://...

    - aws_access_key= AWS access key
    - aws_secret_key= AWS secret key
    - region= The region to use.
    - bucket= The bucket to use.
    - key= The key to use. Use $datetime to have it use yyyy-mm-dd-hh-mm-ss time format for the key.
    - period= Number of seconds to wait per output to S3. Set to 0 for no buffered output

- JDBC: This is the POSTGRES access string used by CDP to store messages.
- CONVERSION - This will promote a subobject to a first level attribute. In this example, userId is null
it will be replaced by the value in propertis[consumer.loginStatus].
- MESSAGEFILTER - Predicate that screens out unwanted values. In this case we will only process
messages where userId is not null and the user is logged in.

T-Service

The cluster has environment variables to set up the different sub-systems used by it. The file Docker.env
contains the values for the macro substitions below"

```
   environment:
      CLUSTERS: "cluster1:5701"
      TRANSFORMERS: "http://r-transformer1:9090"
      S3: "${S3}"
      KINESIS: "${S3}"
```

The following are the meanings
- CLUSTERS: Comma separated list of clusters to connect to.
- TRANSFORMERS: Location of the dockerized RudderStack destinationtransform processes.
- S3: The S3 definition for saving messages and profiles to S3.
- KINESIS: The Kinesis specification.


# THEORY OF OPERATION

Like Rudderstack, CDP is a Segment.io replacement. Unlike Rudderstack or Segment, CDP holds all the 
messages it receives in a clusterable, in memory data grid. Segment has no query capability and 
Rudderstack queries against Postgres.

This means messages received by CDP are queryable at memory speeds. The size of the database 
is determined by the number of clusters, and queries are distributed, much like Elastic Search.

The system is fault tolerant, and uses Raft consensus to determine leader-followers. 
Partitioning of data across shards is completely automatic. The nodes are designed to be 
triple-redundant (configurable).

Basically, the INPUTS to the system are from APIs (like Segment or Rudderstack) and flow 
into the Cluster node. Each Cluster node is a Hazelcast cluster. The more nodes, the larger 
the potential database in memory. The in memory database is queryable using SQL predicates, 
the queries are distributed across the nodes. The in memory database is automatically saved 
to Postgres. Metadata is also stored in postgres (like configuration data).

The INPUTS from the APIs are Messages (of type track, screen, identify, etc.) These 
MESSAGEs are stored in a queryable IMDG (in memory data grid).

Each MESSAGE flowing into the Cluster becomes a Job. JOBs are placed on a blocking 
distributed Queue. These queues are known as JOBS, DEADLETTER, COMPLETED and ERRORED. 
A job on the JOB Queue is waiting to be processed. The T-Service takes a job from the 
JOBS Queue and places it in the RUNNING Cache. While on the RUNNING Cache, the Job can 
be queried. Note, JOBS, DEADLETTER, COMPLETED and ERRORED are Queues and cannot be queried - 
but they can be iterated over.

The T-Service takes a JOB from the JOBS Queue (via poll) - Which identifies a message 
and outputs to N outputs. The T-Service is a Hazelcast CLIENT - it is not part of the cluster. 
The outputs correspond to Rudderstack Transforms, and are called TASKS). The T-Service handles 
multiple jobs concurrently using workers. Multiple T-Service components can be run to handle higher loads.

The T-Service takes the Job, which identifies the MESSAGE and the output transforms to be used. 
Using these transforms from the configuration, the corresponding Rudderstack Transformation client 
is called with an HTTP POST providing the MESSAGE and the configuration for the transformation 
(Like FB, Kinesis, S3, etc).

If the T-Service completes its work, then the Job is placed on the COMPLETED Queue and deleted 
from the RUNNING Cache. A failure to connect to the Rudderstack transform will be designated 
as an error. The JOB will be placed in the DEADLETTER Queue for the Cluster to deal with as 
it sees fit.

Note, the RUNNING Cache entries have a timeout. Thus, if the T-Service crashes or exits, 
any JOBs it was assigned will be deleted from the RUNNING Cache, but will be automatically 
placed on the DEADLETTER Queue again.

The Cluster will take the JOBs on the DEADLETTER Queue and if the retry count is below a 
configurablethreshold, then it will be placed back onto the JOB Queue. Note, those tasks 
(transforms) that completed in a previously errored JOB will not be rerun.

JOBS that end up on the DEADLETTER QUEUE multiple times will be placed on the ERRORED 
Queue once the number of jobs has exceeded their retry count. These jobs stay on the 
ERRORED queue, where they remain indefinitely are unless manually restarted or deleted.

##Message Flow

##Receive Input

The Cluster-Server receives Segment API messages as HTTP POST messages. These messages have unique 
IDs, each message loads into the MESSAGES cache, with the id as the key. This is a serializable 
message and is stored into an ICache.

Messages stay in the cache until they are deleted by a predefined eviction strategy. This could 
be evicted after N elements are in the cache and FIFO eviction occurs or, if configured, it 
could be based on the time in the cache.

In any event, a message written to the cache is always backed up to Postgres database.

##Messages into Jobs

Sending an event to the Cluster will result in the creation of a "Job". A job contains a 
write key, a message id, and keys for destinations. The job is placed on the JOBS IQueue b
y the Cluster-Server.

T-Service polls the JOBS IQueue, and pops a job to be run off the queue. The Job is 
updated to running, and it is written back to the RUNNING cache. The T-Service creates 
a job runner, and executes that with its executor service. When the Job completes, its 
status is updated and it is then written to the COMPLETED IQueue or the DEADLETTER IQueue, 
depending on status.

A Job has 1 or more destinations. These destinations are Tasks. The configCache is queried 
to obtain the destination configurations for each task. The job also contains the id of the 
message and that is retrieved from the MESSAGES cache. With the destination config parameters 
in hand and the message, an HTTP POST is sent to the Rudderstack Transform service. When the 
POST returns, the status of the task is updated.

##How Billing Works

Billing is simply a serializable JAVA object with some basic counters. It basically looks 
like this:

```
public class Billing implements Serializable { 
    Instant timestamp;  // The time in UTC format. 
    Long messagesIn;    // messages received by the cluster 
    Long messagesOut;   // messages sent by the services 
    Long bytesIn;       // number of bytes received by the cluster 
    Long bytesOut;      // number of bytes transmitted by the service 
    Long queries;       // number of queries received 
    Long queryTime;     // query time = number-of-ms-for-query * nClusters. 
    Long nClusters;     // number of clusters running. 
    Long nServices;     // number of services running. 
}
```

This is stored in a Cache called "BILLING". This cache will store the last 10,080 entries. 
Then they are aged out. Each record is indexed by timestamp - the epoch. Once a minute the 
Raft leader will create a new Billing record for the clusters and services to update.

The Billing records are queryable in memory for up to 7 days.

##How Journaling to S3 Works

##How Journaling to Postgres Works

##QUERIES WITH SQL PREDICATES

Standard Hazelcast predicates can be built to handle queries. The most important records to query are 
of course the messages. Here is a fragment of a track.

```
{
"sentAt": "2020-05-06T12:44:04.029Z" 
"channel": "web",
"type": "track",
"context": {
    "app": {
        "build": "1.0.0",
        "name": "RudderLabs JavaScript SDK",
        "namespace": "com.rudderlabs.javascript",
        "version": "1.1.1"
    },
    "traits": {
        "email": "fan.boi@apple.com"
    }
}
"properties": {
    "my_special_type": 1000,
    "test_123": "Hello world"
},
...
```

Besides properties and traits, the fields are accessed as objects using '.' notation. To access 
traits or properties, use [] format.

Examples from above:

context.app.build    --> Equals "1.0.0"
To access properties attribute value for 'my_special_type' use:

```
properties[my_special_type]  --> Equals 1000
```

This is because traits and properties have fields that are not defined in the message schema at 
compile time. These are all user defined fields.

###Query Simple

The predicate to query all track messages would be

```
"type = track"
```

Note, the predicate knows the types, so track does not have to be in quotes.

###Query with AND and OR

Here is a predicate for track and alias:

```
"type = track OR type = alias"
```

Query With Date Fields.

Find all track and web events for anonymousId 123456 and between may 5 and may 10:

```
"(type = track OR type = alias) AND \
    anonymousId = 123456 AND \
    (timestamp >= 2020-05-05T00:00:00.000Z AND timestamp <= 2020-05-10T23:59:99.999Z)"
```    
Note, all the records will be returned that match. This could be a large number of records, so 
you have to be prepared to deal with it, or, you can use other predicate types to implement paging 
and filtering.

###Query traits and properties

Querying the properties and traits) fields is different than all other fields you will encounter. This is because traits and properties can have user defined values.

With all other fields you can use the name of the field. With properties and traits, their field names are not set, so, a different query pattern is used. For example, in the above fragment to find a message with the properties field "my_special_type" equal to 1000 we would use this:

```
"properties[my_special_type] contains 1000"
```

###Query Context Traits

The attribute "traits" may appear at message.traits, or it may appear at message.context.traits. 
From a user perspective they are treated the same way. However, their implementations are dramatically 
different. The message.traits uses a distributed IMap evaluator attached to the MESSAGECACHE and 
PROFILES caches. The 'context' is not at the IMap level, so, it is implemented as a special case by 
C1X in the Hazelcast source code in file: com.hazelcast.query.impl.QueryableEntry.java at 
approximately line 127, in the method: extractAttributeValue(String). This is only for support of 
the messages.context.traits case. It is not a general purpose mapping function.