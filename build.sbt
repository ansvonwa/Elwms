ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val benchmark = project
  .in(file("benchmark"))
  .dependsOn(root)
  .settings(
  )

lazy val root = (project in file("."))
  .settings(
    name := "Elwms",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.12" % "test",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test",
    libraryDependencies += "org.scalatest" %% "scalatest-funspec" % "3.2.12" % "test",
  )
