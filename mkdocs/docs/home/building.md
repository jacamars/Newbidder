#Building the System

The source code for the RTB4FREE system is located [here](https://github.com/RTB4FREE){target=_blank}

##Makefile

To work with the source you will need Maven, Java 1.11, Make, and Yarn, and Mkdocs, and Minio Client. The goal of the makefile is to create the docker container necessary for deploying the bidder.

First step is to git pull the distribution.

##Application

The first step is to 'make application', or simply 'make' will create all the components necessary for the docker container.

```
	$make application
```

This will compile the JAVA application into an all-in-one JAR file, it will then create the campaign manager react application, and then create the docker container.

##Minio

The 'make minio' is executed after 'make application'. The purpose is to copy the initial data files
the bidder uses to convert 2 character country names into 3 character names, zip code information, and
CIDR blocks to block common bot addresses.

```
	$make minio
```
Note, the directory /tmp/s3 will contain the buckets and objects used by the bidder. If you want to change
the location of the hosted volume, or to replace with Amazon S3, modify the makefile.

##Local

The 'make local' compiles just the JAVA application. Eg:

```
	$make local
```

This just creates the classes, it does not build the JAR file.

##React

The 'make react' command compiles the REACT based campaign manager into a deployable www directory.

```
	$make react
```

This uses YARN to make the system.

##Docker

The 'make docker' command creates the docker container that holds the react-based campaign manager and the JAVA based bidder. It does not compile these artifacts first.

```
	$make docker
```

This will create the linux image used by the applications, but does not compile them.

##Backup-db

This command will back up your current POSTGRES database into a dump file.

```
	$make backup-db
```
By default it connects to localhost:54321, you will need to adjust this to point to the postgres container if localhost is not the destination.

It creates a file called 'database.backup' in the current working directory.

##Restore-db

This command will restore the database.backup file to the postgres database. Note, By default it connects to localhost:54321, you will need to adjust this to point to the postgres container if localhost is not the destination.

```
	$make restore-db
```

##Clean

This command deletes the class files.

```
	$make clean
```
