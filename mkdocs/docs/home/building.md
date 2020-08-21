#Building the System

The source code for the RTB4FREE system is located [here](https://github.com/RTB4FREE){target=_blank}

##Makefile

To work with the source you will need Maven, Java 1.11, Make, and Yarn, and Mkdocs. The goal of the makefile
is to create the docker container necessary for deploying the bidder.

First step is to git pull the distribution.

##Application

The 'make application', or simply 'make' will create all the components necessary for the docker container.

```
	$make application
```

This will compile the JAVA application into an all-in-one JAR file, it will then create the campaign manager react application, and then create the docker container.

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
