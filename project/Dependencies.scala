import sbt._

object Dependencies {
    lazy val scalactic: ModuleID = "org.scalactic" %% "scalactic" % "3.0.1" % "test"
    lazy val scalatest: ModuleID = "org.scalatest" %% "scalatest" % "3.0.1" % "test"

    lazy val junit: ModuleID = "junit" % "junit" % "4.12" % "test"

    lazy val testDeps = Seq(scalactic, scalatest, junit)

    lazy val scalaXml: ModuleID = "org.scala-lang.modules" % "scala-xml_2.12" % "1.0.6"

    lazy val swt: ModuleID = {
        val os = (sys.props("os.name"), sys.props("os.arch")) match {
            case ("Linux", "amd64" | "x86_64") => "gtk.linux.x86_64"
            case ("Linux", _) => "gtk.linux.x86"
            case ("Mac OS X", "amd64" | "x86_64") => "cocoa.macosx.x86_64"
            case ("Mac OS X", _) => "cocoa.macosx.x86"
            case (os, "amd64") if os.startsWith("Windows") => "win32.win32.x86_64"
            case (os, _) if os.startsWith("Windows") => "win32.win32.x86"
            case (os, arch) => sys.error("Cannot obtain lib for OS '" + os + "' and architecture '" + arch + "'")
        }
        val artifact = "org.eclipse.swt." + os
        "org.eclipse.swt" % artifact % "4.5"
    }

    lazy val jface: ModuleID = "swt" % "jface" % "3.0.1"

    lazy val visDeps = Seq(scalaXml, swt, jface)
    lazy val visResolver: MavenRepository = "swt-repo" at "http://maven-eclipse.github.io/maven"
    lazy val visJavaOptions: Seq[String] = {
        if (sys.props("os.name") == "Mac OS X") Seq("-XstartOnFirstThread", "-d64") else Seq()
    }
}
