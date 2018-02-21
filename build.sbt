
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "AkkaStreamPlayGround",
    libraryDependencies ++= Seq(
      "org.scalatest"     %% "scalatest"   % "3.0.1" % Test,
      "com.typesafe.akka" %% "akka-stream" % "2.5.9"
    )
  )
