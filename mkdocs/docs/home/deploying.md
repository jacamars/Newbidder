#Deployments

##Docker-Compose

##Kubernetes

This simple quickstart deployment will start the RTB4FREE stack on a single k8s node.  It will start the following containers:

- One RTB4FREE bidder.
- One postgresql database server. Contains campaign configuration information used by the bidder.
- One zookeeper instance. Used by kafka.
- One kafka instance. Data from the bidder is published to Kafka, and is read by logstash. 
- One Elasticsearch node. Contains bidder logs on campaign activity such as requests, bids, wins, etc.
- One Kibana node.  User interface application for Elasticsearch.
- One Logstash instance. Reads data published to Kafka from the bidder, transforms data, then indexes into Elasticsearch.


This configuration was developed on a multipass virtual machine and a k3s kubernetes single node cluster. 
The setup instructions for this configuration is described [here](k3s/README.md)

Start the quickstart deployment with the following kubectl commands.

1. Start Postgres.
    ```
    kubectl apply -f postgres-pod.yml
    ```
1. Start Kafka and Zookeeper.
    ```
    kubectl apply -f kafka-pod.yml
    ```
1. Get ECK from Elastic
    ```
    kubectl apply -f https://download.elastic.co/downloads/eck/1.1.2/all-in-one.yaml
    ```
1. Start Elasticsearch and Kibana
    ```
    kubectl apply -f quickstart/eck_quickstart.yml
    kubectl apply -f quickstart/eck_kibana_quickstart.yml
    ```
1.  Get user "elastic", pwd for kibana login
    ```
    kubectl get secret quickstart-es-elastic-user -o=jsonpath='{.data.elastic}' | base64 --decode; echo
    ```
1. Port forward kibana port
    ```
    kubectl port-forward service/quickstart-kb-http 5601 &
    ```
1. Log in to Kibana and confirm Elasticsearch is up and active.  
    1. Open a browser to https://localhost:5601   
       - **Note: https!**
       - You may need to allow your browser to access https from localhost
    2. Log in with user "elastic" and password from previous step.
1. Start Logstash.
    ```
    kubectl apply -f quickstart/logstash_quickstart.yml
    ```
    - At this point, Logstash should have connected to Elasticsearch. Logstash will load index templates used by the bidder.  Logstash should now also be connected to Kafka and listening for bidder topics.
  
1. Start the bidder and open the ports used.
    ```
    kubectl apply -f quickstart/rtb_quickstart.yml
    kubectl port-forward service/rtb-bidder-service 8155 &
    kubectl port-forward service/rtb-bidder-service 7379 &
    kubectl port-forward service/rtb-bidder-service 8080 &
    ```
1. Connectivity from the bidder to Kafka to Logstash to Elasticsearch should now be established.  .
   1. Go to the Kibana, Dev Tools. List indices with command:
    ```
        GET _cat/indices?s=i
    ```
   1. You should now see indices for rtblogs-<date> and status-<date>.
1. You can define campaigns in the bidder's Campaign Manger. 
    1. Open a browser to http://localhost:8155.
    2. Select Campaign Manager and log in to defined campaigns.
2. Load the Elasticsearch Data Transform jobs that calculate campaign summaries and budgets.
   1. Go to the Kibana, Dev Tools
   2. To load data transform jobs for campaign summaries, 
      1. copy these [API commands](../elk/bidder_budget_queries/transform_jobs_campaign.json) to the development console
      2. Execute. 
   3. To load data transform jobs for budget summaries, 
      1. copy these [API commands](../elk/bidder_budget_queries/transform_jobs_budget.json) to the development console
      2. Execute. 
   4. Go to the Kibana, Stack Management, Elasticsearch - Transforms.  You should see the 6 transform jobs.
   5. Start all the Transform jobs.  
3. Load the Kibana objects.
   1. Go to the Kibana, Stack Management, Kibana - Saved Objects.  
   2. Choose Import, and load the [saved objects file for bidder](elk/kibana/rtb_saved_objects.ndjson). This loads the index patterns, visualizations and dashboards

The RTB4FREE stack should be up and running, and able to process bid requests.  Requests, bids, pixels and wins are logged into Elasticsearch.  Results will be viewable on the Campaign Manager Dashboard and in the Kibana Dashboards.

### Start Demo Campaigns

1.  Load the demo postgres database.
    1.  Copy to [demo postgres database](postgres_db/rtb_postgres.dump) into your postgres pod.
        ```
        kubectl cp ./postgres_db/rtb_postgres.dump postgres:rtb_postgres.dump
        ```
    2. Import the demo configuration.
        ```
        kubectl exec -it postgres /bin/bash
        psql -U postgres postgres < rtb_postgres.dump
        exit
        ```
2.  View/Edit the campaign in bidder's Campaign Manger. 
    1. Open a browser to http://localhost:8155
    2. Log in with credentials
        - Org: rtb4free
        - User: ben.faul@rtb4free.com
        - Password: zulu
        - Server: localhost:7379
    3. You can view the demo Campaign, Creative and Target definitions.
    4. Selecting Campaigns should show the active campaign in running mode.
        ![Campaign Manager - Campaigns](../images/campmgr_1.png)   
3. Generate sample requests to show bidder operation.
   1. Start simulator with command:
    ```
    kubectl apply -f simulator.yml     
    ```
   2. After the bidder starts processing bids, you should see results in the Campaign Manger and Elasticsearch.
      1. Select the Dashboard. You should see active campaign with stats.
         ![Campaign Manager - Dashboard](../images/campmgr_2.png)   
      2. In Kibana, the RTB Dashboard should show activity.
         ![Kibana - RTB Dashboard](../images/kibana_dash1.png)   
      3. On Elasticsearch, the following indices with campaign data will be generated.
         - budget_daily
         - budget_hourly
         - budget_total
         - pixels-yyyy.mm.dd
         - requests-yyyy.mm.dd
         - rtblogs-yyyy.mm.dd
         - status-yyyy.mm.dd
         - wins-<yyyy.mm.dd
         - xform-bids
         - xform-pixels
         - xform-wins    



### Cluster deployment

The quickstart deployment will start the RTB4FREE stack on a single k8s node.  It will start the following containers:

- Three RTB4FREE bidders.
- One postgresql database server. 
- One zookeeper instance. 
- One kafka instance. 
- Three node Elasticsearch cluster. 
- Two Kibana nodes.  
- Three Logstash instances. 


This configuration was developed on 3 multipass virtual machines as a 3 node k3s kubernetes cluster. 
The setup instructions for this configuration is described [here](k3s/README.md)

Start the cluster deployment with the following kubectl commands.

1. Start Postgres.
    ```
    kubectl apply -f postgres-pod.yml
    ```
2. Start Kafka and Zookeeper.
    ```
    kubectl apply -f kafka-pod.yml
    ```
3. Get ECK from Elastic
    ```
    kubectl apply -f https://download.elastic.co/downloads/eck/1.1.2/all-in-one.yaml
    ```
4. Create local Persistent Volumes for Elasticsearch data on nodes 1, 2 and 3
    ```
    multipass shell node1
    sudo mkdir /esdata_eck
    exit
    ```
5. Start Elasticsearch and Kibana
    ```
    kubectl apply -f cluster/eck_cluster.yml
    kubectl apply -f cluster/eck_kibana_cluster.yml
    ```
6.  Get user "elastic", pwd for kibana login
    ```
    kubectl get secret es-cluster-es-elastic-user -o=jsonpath='{.data.elastic}' | base64 --decode; echo
    ```
7. Port forward kibana port
    ```
    kubectl port-forward service/kb-cluster-kb-http 5601 &
    ```
8. Log in to Kibana and confirm Elasticsearch is up and active.  
    1. Open a browser to https://localhost:5601   
       - **Note: https!**
       - You may need to allow your browser to access https from localhost
    2. Log in with user "elastic" and password from previous step.
9. Start Logstash.
    ```
    kubectl apply -f cluster/logstash_cluster.yml
    ```
    - At this point, Logstash should have connected to Elasticsearch. Logstash will load index templates used by the bidder.  Logstash should now also be connected to Kafka and listening for bidder topics.
  
10. Start the bidder and open the ports used.
    ```
    kubectl apply -f cluster/rtb_cluster.yml
    kubectl port-forward service/rtb-bidder-service 8155 &
    kubectl port-forward service/rtb-bidder-service 7379 &
    kubectl port-forward service/rtb-bidder-service 8080 &
    ```
11. Connectivity from the bidder to Kafka to Logstash to Elasticsearch should now be established.  .
   1. Go to the Kibana, Dev Tools. List indices with command:
    ```
        GET _cat/indices?s=i
    ```
   2. You should now see indices for rtblogs-<date> and status-<date>.
12. You can define campaigns in the bidder's Campaign Manger. 
    1. Open a browser to http://localhost:8155.
    2. Select Campaign Manager and log in to defined campaigns.
13. Load the Elasticsearch Data Transform jobs that calculate campaign summaries and budgets.
   3. Go to the Kibana, Dev Tools
   4. To load data transform jobs for campaign summaries, 
      1. copy these [API commands](../elk/bidder_budget_queries/transform_jobs_campaign.json) to the development console
      2. Execute. 
   5. To load data transform jobs for budget summaries, 
      1. copy these [API commands](../elk/bidder_budget_queries/transform_jobs_budget.json) to the development console
      2. Execute. 
   6. Go to the Kibana, Stack Management, Elasticsearch - Transforms.  You should see the 6 transform jobs.
   7. Start all the Transform jobs.  
14. Load the Kibana objects.
   8. Go to the Kibana, Stack Management, Kibana - Saved Objects.  
   9. Choose Import, and load the [saved objects file for bidder](elk/kibana/rtb_saved_objects.ndjson). This loads the index patterns, visualizations and dashboards

The RTB4FREE stack should be up and running, and able to process bid requests.  Requests, bids, pixels and wins are logged into Elasticsearch.  Results will be viewable on the Campaign Manager Dashboard and in the Kibana Dashboards.
