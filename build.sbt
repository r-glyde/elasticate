name := "elasticate"
version := "0.1.0"
scalaVersion := "2.12.11"

lazy val root = Project("elasticate", file("."))

lazy val core = module("core")
  .settings(libraryDependencies ++= coreDeps)
//  .settings(composeContainerPauseBeforeTestSeconds := 30)

lazy val examples = module("examples")
  .settings(libraryDependencies ++= exampleDeps)
  .dependsOn(core)

val catsEffectVersion = "2.1.3"
val catsVersion       = "2.1.0"
val sttpVersion       = "2.1.0-RC1"
val http4sVersion     = "0.21.2"
val circeVersion      = "0.13.0"
val slf4jVersion      = "1.7.21"
val logbackVersion    = "1.2.3"
val scalaTestVersion  = "3.1.0"

lazy val commonDeps = Seq(
  "com.softwaremill.sttp.client" %% "core"           % sttpVersion,
  "io.circe"                     %% "circe-core"     % circeVersion,
  "io.circe"                     %% "circe-parser"   % circeVersion,
  "io.circe"                     %% "circe-generic"  % circeVersion,
  "ch.qos.logback"               % "logback-classic" % logbackVersion
)

lazy val testDeps = Seq(
  "org.scalatest"            %% "scalatest"                % scalaTestVersion % Test,
  "org.elasticsearch.client" % "elasticsearch-rest-client" % "7.6.0"          % Test,
  "com.spotify"              % "docker-client"             % "8.16.0"         % Test
)

lazy val coreDeps = commonDeps ++ testDeps ++ Seq(
  "org.typelevel" %% "cats-core" % catsVersion
)

lazy val exampleDeps = commonDeps ++ Seq(
  "org.typelevel"                %% "cats-effect"              % catsEffectVersion,
  "com.softwaremill.sttp.client" %% "http4s-backend"           % sttpVersion,
  "org.http4s"                   %% "http4s-async-http-client" % http4sVersion
)

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
