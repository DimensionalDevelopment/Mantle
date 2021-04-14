import java.net.URI

plugins {
    id("fabric-loom") version "0.5.43"
    id("maven-publish")
}

base.archivesBaseName = "mantle"
group = "slimeknights"
version = "1.6.11-SNAPSHOT"

repositories {
    maven {
        name = "Shedaniel's Maven"
        url = uri("https://maven.shedaniel.me/")
    }

    maven {
        name = "Curseforge Maven"
        url = uri("https://www.cursemaven.com")
    }

    maven {
        name = "BuildCraft"
        url = uri("https://mod-buildcraft.com/maven")
    }

    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }

    maven {
        url = uri("gcs://devan-maven")
    }
}

val modImplementationAndInclude by configurations.register("modImplementationAndInclude")

dependencies {
    minecraft("net.minecraft", "minecraft", "1.16.5")
    mappings("net.fabricmc", "yarn", "1.16.5+build.6", classifier = "v2")

    modImplementation("net.fabricmc", "fabric-loader", "0.11.3")
    modImplementation("net.fabricmc.fabric-api", "fabric-api", "0.32.5+1.16")

    modApi("me.shedaniel.cloth", "cloth-config-fabric", "4.11.19")
    modApi("alexiil.mc.lib", "libblockattributes-core", "0.8.9-pre.1")
    modApi("alexiil.mc.lib", "libblockattributes-fluids", "0.8.9-pre.1")

    modRuntime("com.terraformersmc:modmenu:1.16.9")

//    modRuntime("me.shedaniel", "RoughlyEnoughItems", "5.8.10")
    modRuntime("curse.maven", "worldedit-225608", "3135186")
    modRuntime("curse.maven", "appleskin-248787", "2987255")
    modRuntime("curse.maven", "hwyla-253449", "3033613")




    add(sourceSets.main.get().getTaskName("mod", JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME), modImplementationAndInclude)
    add(net.fabricmc.loom.util.Constants.Configurations.INCLUDE, modImplementationAndInclude)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

loom {
    accessWidener = file("src/main/resources/mantle.aw")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    if (JavaVersion.current().isJava9Compatible) {
        options.release.set(8)
    } else {
        sourceCompatibility = "8"
        targetCompatibility = "8"
    }
}

tasks.withType<AbstractArchiveTask> {
    from(file("LICENSE"))
    from(file("LICENSE.LESSER"))
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.remapJar {
    doLast {
        input.get().asFile.delete()
    }
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.remapJar) {
                classifier = null
            }
        }
    }

    repositories {
        // Add repositories to publish to here.
        if (project.hasProperty("maven_url")) {
            maven {
                val mavenUrl = project.property("maven_url") as String
                url = uri(mavenUrl)
                if (mavenUrl.startsWith("http") && project.hasProperty("maven_username") && project.hasProperty("maven_password")) {
                    credentials {
                        username = project.property("maven_username") as String
                        password = project.property("maven_password") as String
                    }
                }
            }
        }
    }
}