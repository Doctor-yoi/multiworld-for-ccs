import net.fabricmc.loom.task.RemapJarTask

plugins {
    id ("fabric-loom") version "1.3-SNAPSHOT"
    id ("maven-publish")
	id ("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

base {
    archivesBaseName = "Multiworld-Fabric"
    version = "bundle"
    group = "me.isaiah.mods"
}

dependencies {

	// 1.20
    minecraft("com.mojang:minecraft:1.20") 
    mappings("net.fabricmc:yarn:1.20+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.21")
	
	// bundle jars
	include(project(":Multiworld-Fabric-1.18.2"))
}


sourceSets {
    main {
        java {
            // Needs fixing for 1.18:
            exclude("me/isaiah/**/*.java")
            exclude("**/Multiworld.mixins.json")
            exclude("org/minecarts/**/*.java")
			
			srcDirs("src/main/java") 
        }
        resources {
			 exclude("**/Multiworld.mixins.json")
        }
    }
}

/*configure([tasks.compileJava]) {
    sourceCompatibility = 16 // for the IDE support
    options.release = 8

    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(16)
    }
}*/

//tasks.getByName("compileJava") {
    //sourceCompatibility = 16
    //options.release = 8
//}


tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.INHERIT }

tasks.getByName<ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    filesMatching("fabric.mod.json") {
        expand(
            mutableMapOf(
                "version" to "1.1"
            )
        )
    }
}

val remapJar = tasks.getByName<RemapJarTask>("remapJar")

tasks.named("build") { finalizedBy("copyReport2") }

tasks.register<Copy>("copyReport2") {
    from(remapJar)
    into("${project.rootDir}/output")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()
            
            pom {
                name.set(project.name.toLowerCase())
                description.set("A concise description of my library")
                url.set("http://www.example.com/")
            }

            artifact(remapJar)
        }
    }

    repositories {
        val mavenUsername: String? by project
        val mavenPassword: String? by project
        mavenPassword?.let {
            maven(url = "https://repo.codemc.io/repository/maven-releases/") {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}