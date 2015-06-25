name := "icfp2013s"

version := "1.0"

scalaVersion := "2.10.2"

scalacOptions ++= Seq("-feature", "-deprecation")

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.github.axel22" %% "scalameter" % "0.4-M2" % "test"

libraryDependencies += "org.spire-math" %% "spire" % "0.6.0"

libraryDependencies += "junit" % "junit" % "4.11" % "test"

libraryDependencies += "com.google.guava" % "guava" % "14.0.1" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.10" % "test"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

EclipseKeys.withSource := true

unmanagedSourceDirectories in Compile <++= baseDirectory { base =>
  Seq(
    base / "problems"
  )
}
