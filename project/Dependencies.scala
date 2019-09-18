import sbt._

object Dependencies {

  object Versions {
    val canoe      = "0.1.0"
    val cats       = "2.0.0"
    val catsEffect = "2.0.0"
    val fs2        = "2.0.0"
    val http4s     = "0.21.0-M4"
    val logback    = "1.2.3"
    val bm4        = "0.3.1"
  }

  object Libraries {
    def http4s(artifact: String): ModuleID = "org.http4s" %% s"http4s-$artifact" % Versions.http4s

    lazy val cats         = "org.typelevel" %% "cats-core" % Versions.cats
    lazy val catsEffect   = "org.typelevel" %% "cats-effect" % Versions.catsEffect
    lazy val canoe        = "org.augustjune" %% "canoe" % Versions.canoe
    lazy val fs2Core      = "co.fs2" %% "fs2-core" % Versions.fs2
    lazy val http4sServer = http4s("blaze-server")
    lazy val http4sDsl    = http4s("dsl")
    lazy val logback      = "ch.qos.logback" % "logback-classic" % Versions.logback
    lazy val bm4          = "com.olegpy" %% "better-monadic-for" % Versions.bm4

    lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
  }

}
