#Reactive-university-project

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

Then navigate to (http://localhost:9000/) to see the results.
_You might need to replace `localhost` with IP of your docker-machine/boot2docker._


### Example of akka usage can be found:

####Java:

```
app/akka/demo/HelloAkkaJava.java
test/akka/demo/HelloAkkaTest.java
```

####Scala:
```
app/akka/demo/HelloAkkaScala.scala
test/akka/demo/HelloAkkaSpec.scala
```


 