import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta11"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
    id("java-library")
    id("signing")
    id("com.vanniktech.maven.publish") version "0.31.0"
}

group = "io.github.lumine1909"
version = "1.0.2"
description = "A utility project for handling mod and packet message in plugin-side"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

mavenPublishing {
    coordinates(
        groupId = group as String,
        artifactId = "messageutil",
        version = version as String
    )
    pom {
        name.set("messageutil")
        description.set("API for server side mcpr format recording.")
        url.set("https://github.com/Lumine1909/MessageUtil")
        licenses {
            license {
                name.set("MIT License")
            }
        }

        developers {
            developer {
                id.set("Lumine1909")
                name.set("Lumine1909")
                email.set("133463833+Lumine1909@users.noreply.github.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/Lumine1909/MessageUtil.git")
            developerConnection.set("scm:git:ssh://github.com/Lumine1909/MessageUtil.git")
            url.set("https://github.com/Lumine1909/MessageUtil")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
