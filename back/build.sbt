name := """sisdep"""
organization := "GeoSAT"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean, LauncherJarPlugin, DockerPlugin, JavaAppPackaging)

scalaVersion := "2.13.6"

javacOptions += "-Xlint:unchecked"
javacOptions += "-Xlint:deprecation"

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += ehcache
libraryDependencies += "io.vavr" % "vavr-jackson" % "0.10.3"
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.0"
libraryDependencies += "com.auth0" % "java-jwt" % "3.18.2"
libraryDependencies += "org.apache.poi" % "poi" % "5.0.0"
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "5.0.0"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.12.0"
libraryDependencies += "de.svenkubiak" % "jBCrypt" % "0.4.3"
libraryDependencies += "me.gosimple" % "nbvcxz" % "1.5.0"
libraryDependencies += "com.typesafe.play" %% "play-mailer" % "8.0.1"
libraryDependencies += "com.typesafe.play" %% "play-mailer-guice" % "8.0.1"
// Las dependecias de twelvemonkeys reemplazan algunas partes del core de image.io (de java)
// esto corrige algunos problemas en la carga de imágenes provenientes de modelos Samsung (No standar JPEG format)
libraryDependencies += "com.twelvemonkeys.imageio" % "imageio-core" % "3.7.0"
libraryDependencies += "com.twelvemonkeys.imageio" % "imageio-jpeg" % "3.7.0"
libraryDependencies +=  "org.postgresql" % "postgresql" % "42.3.4"
// https://mvnrepository.com/artifact/io.ebean/ebean-postgis
libraryDependencies += "net.postgis" % "postgis-jdbc" % "2.5.1"
libraryDependencies += "io.ebean" % "ebean-postgis" % "12.8.1"
libraryDependencies ++= Seq(javaWs)

import com.typesafe.sbt.packager.docker._
// necesario para permitir manejo de fuentes
Docker / mappings += file(
  s"${baseDirectory.value}/fontconfig.properties"
) -> "fontconfig.properties"
dockerExecCommand := Seq("podman")
dockerBaseImage := "docker.io/library/adoptopenjdk:11.0.11_9-jre-openj9-0.26.0-focal"
// La imagen de alpine no viene con bash por defecto, por eso se setea el usuario a root y se instala.
dockerCommands += Cmd("USER", "root")
dockerCommands += ExecCmd("COPY", "fontconfig.properties", "/usr/lib/jvm/java-11-openjdk/jre/lib/fontconfig.properties")
// dockerCommands += ExecCmd("RUN", "/bin/sh", "-c", "apk add --no-cache bash")
// dockerCommands += ExecCmd("RUN", "/bin/sh", "-c", "apk add --no-cache ttf-dejavu")
dockerCommands += ExecCmd("RUN", "/bin/sh", "-c", "mkdir -p /archivos")

// development setting to bind https port to 9443
PlayKeys.devSettings += "play.server.https.port" -> "9443"