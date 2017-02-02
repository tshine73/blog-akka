name := """blog-akka"""

version := "1.0"

scalaVersion := "2.12.1"

val akkaHttpVersion = "10.0.1"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test")


libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "1.2.1"

libraryDependencies += "com.github.cb372" %% "scalacache-guava" % "0.9.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http",
  "com.typesafe.akka" %% "akka-http-spray-json"
).map(_ % akkaHttpVersion)



libraryDependencies += "shine.st" %% "common" % "1.0.3"

resolvers += "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"