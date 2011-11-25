
name := "chat-akka"

scalaVersion := "2.9.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq( 
	"org.specs2" %% "specs2" % "1.6.1" % "test", 
	"junit" % "junit" % "4.7" % "test",
	"org.slf4j" % "slf4j-api" % "1.6.1",	
	"ch.qos.logback" % "logback-classic" % "0.9.28",
	"se.scalablesolutions.akka" % "akka-actor" % "1.2",
	"se.scalablesolutions.akka" % "akka-remote" % "1.2",
	"se.scalablesolutions.akka" % "akka-testkit" % "1.2"
	)