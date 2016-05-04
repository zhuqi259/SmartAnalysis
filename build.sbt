organization := "cn.edu.jlu"

version := "1.0"

scalaVersion := "2.10.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "UTF-8")

javacOptions ++= Seq("-source", "1.7", "-encoding", "UTF-8", "-target", "1.7")

libraryDependencies ++= {
  val akkaV = "2.3.14"
  val sprayV = "1.3.3"
  Seq(
    "org.apache.mina" % "mina-core" % "2.0.10",
    "joda-time" % "joda-time" % "2.9.1",
    "com.github.nscala-time" %% "nscala-time" % "2.6.0",
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    // "io.spray" %% "spray-caching" % sprayV,
    "io.spray" %% "spray-servlet" % sprayV,
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "com.typesafe.slick" %% "slick" % "3.1.0",
    "mysql" % "mysql-connector-java" % "5.1.37",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.specs2" %% "specs2-core" % "2.3.11" % "test",
    // "org.json4s" %% "json4s-native" % "3.3.0"
    "org.json4s" %% "json4s-jackson" % "3.3.0", // json中文
    "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"
  )
}

Revolver.settings

lazy val SmartAnalysis = (project in file(".")).enablePlugins(JettyPlugin).enablePlugins(TomcatPlugin).enablePlugins(WarPlugin)

webappPostProcess := {
  webappDir =>
    def listFiles(level: Int)(f: File): Unit = {
      val indent = ((1 until level) map { _ => "  " }).mkString
      if (f.isDirectory) {
        streams.value.log.info(indent + f.getName + "/")
        f.listFiles foreach {
          listFiles(level + 1)
        }
      } else streams.value.log.info(indent + f.getName)
    }
    listFiles(1)(webappDir)
}

