import sbt._

object Dependencies {

  object Version {
    val cats = "2.0.0"
    val catsEffect = "2.0.0"
    val fs2 = "2.0.0"
    val canoe = "0.1.0"
    val logback = "1.2.3"
    val bm4 = "0.3.1"
  }

  object Libraries {
    lazy val cats = "org.typelevel" %% "cats-core" % Version.cats
    lazy val catsEffect = "org.typelevel" %% "cats-effect" % Version.catsEffect
    lazy val canoe = "org.augustjune" %% "canoe" % Version.canoe
    lazy val fs2Core = "co.fs2" %% "fs2-core" % Version.fs2
    lazy val logback = "ch.qos.logback" % "logback-classic" % Version.logback
    lazy val bm4 = "com.olegpy" %% "better-monadic-for" % Version.bm4

    lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8"
  }

}
