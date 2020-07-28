version: "2"

services:

## journalnode
  journalnode-${i}:
    container_name: journalnode-${i}
    networks: ["${network_name}"]
    hostname: journalnode-${i}.${network_name}
    image: smizy/hadoop-base:2.7.7-alpine
    expose: [8480, 8485]
    environment:
      - SERVICE_8485_NAME=journalnode
      - SERVICE_8480_IGNORE=true
      - HADOOP_ZOOKEEPER_QUORUM=${ZOOKEEPER_QUORUM} 
      - HADOOP_HEAPSIZE=1000
      - HADOOP_NAMENODE_HA=${NAMENODE_HA}
      ${SWARM_FILTER_JOURNALNODE_${i}}
    command: journalnode
##/ journalnode

## namenode
  namenode-${i}:
    container_name: namenode-${i}
    networks: ["${network_name}"]
    hostname: namenode-${i}.${network_name}
    image: smizy/hadoop-base:2.7.7-alpine 
    expose: ["8020"]
    ports:  ["50070"]
    environment:
      - SERVICE_8020_NAME=namenode
      - SERVICE_50070_IGNORE=true
      - HADOOP_ZOOKEEPER_QUORUM=${ZOOKEEPER_QUORUM} 
      - HADOOP_HEAPSIZE=1000
      - HADOOP_NAMENODE_HA=${NAMENODE_HA}
      ${SWARM_FILTER_NAMENODE_${i}}
    entrypoint: entrypoint.sh
    command: namenode-${i}
##/ namenode

## datanode
  datanode-${i}:
    container_name: datanode-${i}
    networks: ["${network_name}"]
    hostname: datanode-${i}.${network_name}
    image: smizy/hadoop-base:2.7.7-alpine
    expose: ["50010", "50020", "50075"]
    environment:
      - SERVICE_50010_NAME=datanode
      - SERVICE_50020_IGNORE=true
      - SERVICE_50075_IGNORE=true
      - HADOOP_ZOOKEEPER_QUORUM=${ZOOKEEPER_QUORUM} 
      - HADOOP_HEAPSIZE=1000
      - HADOOP_NAMENODE_HA=${NAMENODE_HA}
      ${SWARM_FILTER_DATANODE_${i}}
    entrypoint: entrypoint.sh
    command: datanode
##/ datanode 