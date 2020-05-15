name := "elasticate"
version := "0.1.0"
scalaVersion := "2.12.11"

lazy val root = Project("elasticate", file("."))

lazy val core = module("core")
  .settings(libraryDependencies ++= Dependencies.coreDeps)

lazy val examples = module("examples")
  .settings(libraryDependencies ++= Dependencies.exampleDeps)
  .dependsOn(core)

def module(name: String) =
  Project(name, file(name))
    .enablePlugins(DockerComposePlugin)
    .settings(
      scalacOptions ++= Seq(
        "-Ypartial-unification",
        "-feature",
        "-language:higherKinds",
      ))
    .settings(composeFile := "docker/docker-compose.yml")
