name := """reactive-stocks"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

// scalaz-bintray resolver needed for specs2 library
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)
libraryDependencies ++= Seq(
  ws, // Play's web services module
  specs2 % Test,
  "org.specs2" %% "specs2-matcher-extra" % "3.6" % Test,
  "org.easytesting" % "fest-assert" % "1.4" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % Test,
  "org.webjars" % "bootstrap" % "2.3.2",
  "org.webjars" % "flot" % "0.8.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % Test
)

libraryDependencies += "net.fwbrasil" % "activate-core_2.11" % "1.7"
libraryDependencies += "net.fwbrasil" % "activate-play_2.11" % "1.7"
libraryDependencies += "org.mariadb.jdbc" % "mariadb-java-client" % "1.4.4"

routesGenerator := InjectedRoutesGenerator

fork in run := false