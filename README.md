# Demonstration of Clustering Vert.x Applications With Infinispan Cluster Manager

## Prerequisites
* Maven >= 3.3.9
* Java >= 8

## Building
* Check out code: git clone https://github.com/InfoSec812/summit-lightning-cluster.git
* Enter project directory: cd summit-lightning-cluster
* Build project: mvn clean package vertx:package

## Run Without Clustering:

`mvn vertx:run`

or

`java -jar target/cluster-<version>.jar`

## Run With Clustering

`mvn exec:exec -Dcluster.ip=<IP>`

or

`java -jar target/cluster-<version>.jar -cluster -cluster-host <IP> -Djava.net.preferIPv4Stack=true -Djgroups.tcp.address=<IP> -Djgroups.mping.mcast_addr=239.5.5.5`

### NOTE:

In order to observe cluster behavior, you will need to launch more than one instance on the same machine. If you want
to cluster across multiple machines, the bind address needs to match the IP of your cluster interface. Also, it is
possible to use Kubernetes cluster information to perform discovery, but that is beyond the scope of this demo.
