name := "Lift3 session-aware futures example"

version := "0.0.1"

organization := "com.ontheserverside"

scalaVersion := "2.11.6"

resolvers ++= Seq(
  Resolver.mavenLocal,
  "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "https://oss.sonatype.org/content/repositories/releases"
)

seq(webSettings :_*)

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "3.0-RC1"
  val specs2Version = "3.8.5"
  Seq(
    "net.liftweb"       %% "lift-webkit"            % liftVersion           % "compile",
    "net.liftmodules"   %% "lift-jquery-module_3.0" % "2.9"                 % "compile",
    "net.liftweb"       %% "lift-testkit"           % liftVersion           % "test",
    "org.eclipse.jetty" %  "jetty-webapp"           % "8.1.7.v20120910"     % "container",
    "org.eclipse.jetty" %  "jetty-plus"             % "8.1.7.v20120910"     % "container", // For Jetty Config
    "org.eclipse.jetty.orbit" % "javax.servlet"     % "3.0.0.v201112011016" % "compile" artifacts Artifact("javax.servlet", "jar", "jar"),
    "javax.servlet"     % "servlet-api"             % "2.5"                 % "provided->default",
    "ch.qos.logback"    %  "logback-classic"        % "1.0.6",
    "org.specs2"        %% "specs2-core"            % specs2Version         % "test",
    "org.specs2"        %% "specs2-matcher-extra"   % specs2Version         % "test"
  )
}

