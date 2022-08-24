resolvers += Resolver.typesafeRepo("releases")
// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.15")
addSbtPlugin("com.typesafe.play" % "sbt-play-ebean" % "6.2.0-RC4")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.9")