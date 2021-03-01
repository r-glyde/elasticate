import sbt._

object Dependencies {

  object Sttp {
    private val version = "2.2.9"

    val core  = "com.softwaremill.sttp.client" %% "core"           % version
    val https = "com.softwaremill.sttp.client" %% "http4s-backend" % version
  }

  object Circe {
    private val version = "0.13.0"

    val core    = "io.circe" %% "circe-core"    % version
    val parser  = "io.circe" %% "circe-parser"  % version
    val generic = "io.circe" %% "circe-generic" % version

    val all = Seq(core, parser, generic)
  }

  lazy val commonDeps = (Sttp.core +: Circe.all) ++ Seq(
    "org.typelevel"  %% "cats-core"      % "2.4.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  lazy val testDeps = Seq(
    "org.scalatest"            %% "scalatest"                           % "3.2.5"  % Test,
    "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "7.11.1" % Test,
    "com.spotify"              % "docker-client"                        % "8.16.0" % Test
  )

  lazy val coreDeps = commonDeps ++ testDeps

  lazy val fs2Deps = Seq(
    "co.fs2" %% "fs2-core" % "2.5.3"
  )

  lazy val exampleDeps = (Sttp.https +: commonDeps) ++ Seq(
    "org.typelevel" %% "cats-effect"              % "2.3.3",
    "org.http4s"    %% "http4s-async-http-client" % "0.21.19"
  )

}
