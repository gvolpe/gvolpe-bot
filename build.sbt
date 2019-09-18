import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._
import com.scalapenos.sbt.prompt._
import Dependencies._

ThisBuild / scalaVersion := "2.13.0"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "io.github"
ThisBuild / organizationName := "gvolpe"

lazy val root = (project in file("."))
  .settings(
    name := "gvolpe-bot",
    scalafmtOnCompile := true,
    libraryDependencies ++= Seq(
      compilerPlugin(Libraries.bm4),
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.canoe,
      Libraries.fs2Core,
      Libraries.logback % "runtime"
    )
  )

promptTheme := PromptTheme(
  List(
    text("[sbt] ", fg(105)),
    text(_ => "gvolpe-bot", fg(15)).padRight(" λ ")
  )
)

addCompilerPlugin("org.scalameta" % "semanticdb-scalac_2.13.0" % "4.2.0")
