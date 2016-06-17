name := """reactive-stocks-seed-node"""

version := "latest"

lazy val seed_app = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    mainClass in Compile := Some("SeedNodeApp"),
    version in Docker := "latest",
    NativePackagerKeys.dockerExposedPorts := Seq(9001, 9444, 2551, 2552, 2553),
    NativePackagerKeys.dockerExposedVolumes := Seq("/opt/docker/logs")
  )

scalaVersion := "2.11.7"

// scalaz-bintray resolver needed for specs2 library
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % "2.4.6"
)

fork in run := true

resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)