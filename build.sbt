name := "elasticate"
version := "0.1.0"

lazy val root = Project("elasticate", file("."))
  .aggregate(core, examples, fs2)

lazy val core = module("core")
  .settings(libraryDependencies ++= Dependencies.coreDeps)

lazy val fs2 = module("fs2")
  .settings(libraryDependencies ++= Dependencies.fs2Deps)
  .dependsOn(core)

lazy val examples = module("examples")
  .settings(libraryDependencies ++= Dependencies.exampleDeps)
  .dependsOn(core, fs2)

def module(name: String) =
  Project(name, file(name))
    .enablePlugins(DockerComposePlugin)
    .settings(
      scalaVersion := "2.13.3",
      scalacOptions ++= Seq(
        "-deprecation"
      ),
      composeFile := "docker/docker-compose.yml"
    )
