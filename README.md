# Reactive-university-project

## Running the project in a simple way

```bash
sbt run
```

Then navigate to (http://localhost:9000/) to see the results.

## Running the project on Docker

First, you need to compile the sources and create a docker image. In shell, run:

```bash
sbt compile
sbt docker:publishLocal
```

Then verify that `reactive-stocks:latest` image was created by typing:

```bash
docker image
```

Finally, you can run your new image by:

```
docker run -p 9000:9000 reactive-stocks:latest
```

or just run the whole project with

```
docker-compose up
```

Then navigate to (http://localhost:9000/) to see the results.
_You might need to replace `localhost` with IP of your docker-machine/boot2docker._


### Example of akka usage can be found:

#### Java:

```
app/akka/demo/HelloAkkaJava.java
test/akka/demo/HelloAkkaTest.java
```

#### Scala:
```
app/akka/demo/HelloAkkaScala.scala
test/akka/demo/HelloAkkaSpec.scala
```

## Database connection
### Database
Chosen database is MySQL (version used for development: 5.6.30)
To configure stock-root user:

```sql
GRANT ALL PRIVILEGES ON *.* TO 'stock'@'localhost' WITH GRANT OPTION;
SET PASSWORD FOR 'stock'@'localhost' = PASSWORD("stock");
```

Before starting application, remember about creating database `stock_exchange` and table `quotation`

```sql
CREATE DATABASE IF NOT EXISTS `stock_exchange`;
USE `stock_exchange`;

CREATE TABLE IF NOT EXISTS `quotation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) NOT NULL,
  `opening` float NOT NULL,
  `max` float NOT NULL,
  `min` float NOT NULL,
  `closing` float NOT NULL,
  `change_percentage` float NOT NULL,
  `volume` int(11) NOT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`id`)
);
```

Complete SQL file with inserts: stock_exchange.sql.

### DB Initialisation

This is done by docker-compose when creating the DB docker image. It runs `stock_exchange.sql` contents on the DB.
If you need to recreate your DB, just run:

```
docker rm reactiveuniversityproject_db_1 && docker rmi reactiveuniversityproject_db
```

This will remove the appropriate container and its associated image. The next time you run `docker-compose up`,
a new image will be created.

### ORM
As for ORM, [http://slick.lightbend.com/docs/] in version 1.1.0 has been chosen.

Information about connection is located in file conf/application.conf.

### Demonstration
localhost:9000/quotations - presents list of 'ALIOR' quotations, allows to add new and delete existing ones


## TODOs

* Provide an Akka cluster (seed/workers)