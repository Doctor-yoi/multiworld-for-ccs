import net.fabricmc.loom.task.RemapJarTask

plugins {
    id ("fabric-loom") version "1.3-SNAPSHOT"
    id ("maven-publish")
	id ("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

base {
    archivesBaseName = "Multiworld-Fabric"
    version = "1.18.2"
    group = "me.isaiah.mods"
}


dependencies {
    annotationProcessor("com.github.bsideup.jabel:jabel-javac-plugin:0.4.1")

    // 1.18.2
    minecraft("com.mojang:minecraft:1.18.2") 
    mappings("net.fabricmc:yarn:1.18.2+build.2:v2")
    modImplementation("net.fabricmc:fabric-loader:0.13.3")

    annotationProcessor("com.github.bsideup.jabel:jabel-javac-plugin:0.4.1")
	
	include("xyz.nucleoid:fantasy:0.4.7+1.18.2")
	modImplementation("xyz.nucleoid:fantasy:0.4.7+1.18.2")
	modImplementation("curse.maven:cyber-permissions-407695:4640544")
	modImplementation("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")
	modImplementation("net.fabricmc.fabric-api:fabric-api:0.76.0+1.18.2")
	// modImplementation("net.fabricmc.fabric-api:fabric-api-deprecated:0.76.0+1.18.2")
}


sourceSets {
    main {
        java {
            srcDir("${rootProject.projectDir}/Multiworld-Common/src/main/java/com")
            //srcDir("${rootProject.projectDir}/Multiworld-Fabric-1.17/src/main/java")

            // Needs fixing for 1.18:
            //exclude("**/MixinWorld.java")
            
            srcDir("src/main/java")
        }
        resources {
            srcDir("${rootProject.projectDir}/Multiworld-Common/src/main/resources")
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