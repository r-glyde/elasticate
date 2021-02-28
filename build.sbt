ThisBuild / name := "elasticate"
ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.13.5"

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = Project("elasticate", file("."))
  .aggregate(core, examples, fs2)
  .settings(publish / skip := true)

lazy val core = module("core")
  .settings(libraryDependencies ++= Dependencies.coreDeps)

lazy val fs2 = module("fs2")
  .settings(libraryDependencies ++= Dependencies.fs2Deps)
  .dependsOn(core)

lazy val examples = module("examples")
  .settings(libraryDependencies ++= Dependencies.exampleDeps)
  .dependsOn(core, fs2)
  .settings(publish / skip := true)

def module(name: String) =
  Project(name, file(name))
    .enablePlugins(DockerComposePlugin)
    .settings(composeFile := "docker/docker-compose.yml")
