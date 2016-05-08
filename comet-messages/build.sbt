name := "Lift3 actor communication example"

version := "0.0.1"

organization := "net.liftweb"

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
  Seq(
    "net.liftweb"       %% "lift-webkit"            % liftVersion           % "compile",
    "net.liftmodules"   %% "messagebus_3.0"         % "1.0"                 % "compile",
    "net.liftmodules"   %% "lift-jquery-module_3.0" % "2.9"                 % "compile",
    "org.eclipse.jetty" % "jetty-webapp"            % "8.1.7.v20120910"     % "container",
    "org.eclipse.jetty" % "jetty-plus"              % "8.1.7.v20120910"     % "container", // For Jetty Config
    "org.eclipse.jetty.orbit" % "javax.servlet"     % "3.0.0.v201112011016" % "container" artifacts Artifact("javax.servlet", "jar", "jar"),
    "ch.qos.logback"    % "logback-classic"         % "1.0.6"
  )
}

