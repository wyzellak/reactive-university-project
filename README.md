# Reactive-university-project

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


@Piotr Kluch to check if docker deployment is correct please wrap up project into docker container and run (inside docker container):

```
sbt compile
sbt test
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

### ORM
As for ORM, [http://slick.lightbend.com/docs/] in version 1.1.0 has been chosen.

Information about connection is located in file conf/application.conf.

### Demonstration
localhost:9000/quotations - presents list of 'ALIOR' quotations, allows to add new and delete existing ones