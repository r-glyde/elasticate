import sbt._

object Dependencies {

  val circeVersion = "0.13.0"

  lazy val commonDeps = Seq(
    "com.softwaremill.sttp.client" %% "core"           % "2.2.6",
    "io.circe"                     %% "circe-core"     % circeVersion,
    "io.circe"                     %% "circe-parser"   % circeVersion,
    "io.circe"                     %% "circe-generic"  % circeVersion,
    "org.typelevel"                %% "cats-core"      % "2.1.1",
    "ch.qos.logback"               % "logback-classic" % "1.2.3"
  )

  lazy val testDeps = Seq(
    "org.scalatest"            %% "scalatest"                % "3.1.0"  % Test,
    "org.elasticsearch.client" % "elasticsearch-rest-client" % "7.6.0"  % Test,
    "com.spotify"              % "docker-client"             % "8.16.0" % Test
  )

  lazy val coreDeps = commonDeps ++ testDeps

  lazy val exampleDeps = commonDeps ++ Seq(
    "org.typelevel"                %% "cats-effect"              % "2.1.4",
    "com.softwaremill.sttp.client" %% "http4s-backend"           % "2.2.6",
    "org.http4s"                   %% "http4s-async-http-client" % "0.21.7"
  )

}
