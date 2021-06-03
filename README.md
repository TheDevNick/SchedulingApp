# C195

## Title
Planning App

## Purpose of application
A multi-lingual GUI for managing appointments with customers.

## Author and contact
Nicholas Hollins <nholl22@wgu.edu>

## IDE Version
IntelliJ IDEA 2020.3 (Edu)
Build #IE-203.5981.183, built on May 15, 2020
Runtime version: 11.0.9+11-b1145.21 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.

## JDK version
corretto-11.0.9.1

## JavaFX version
javafx-sdk-11.0.2

# How to run app

Create a `database.xml` file in the root of the directory of the repo with the following contents (being sure to fill in the nodes):

```xml
<?xml version = "1.0"?>
<database>
    <server></server>
    <port></port>
    <name></name>
    <user></user>
    <password></password>
</database>
```

You will also need a user in the database with the username "test".

## Additional report

For the additional report, I chose to get a rundown of all customers per firstLevelDivision.
