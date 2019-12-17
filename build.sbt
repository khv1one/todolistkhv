import sbt.Keys.scalacOptions

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """todolistkhv""",
    organization := "com.kinoplanrecruite",
    version := "0.01-SNAPSHOT",
    scalaVersion := "2.13.1",

    libraryDependencies ++= Seq(
      guice,
      "com.typesafe.play" %% "play-slick" % "5.0.0",
      //"com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
      "mysql" % "mysql-connector-java" % "8.0.13",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
    ),

    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )



// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.kinoplanrecruite.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.kinoplanrecruite.binders._"
