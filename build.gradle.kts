import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    `java-library`
    `maven-publish`
}

val owner = "renatomrcosta"
group = "com.xunfos"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

// Set Java compatibility
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    with(kotlinOptions) {
        jvmTarget = "11"
        languageVersion = "1.9"
    }
}

// Sets manifest and sources jar
tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}
java {
    withSourcesJar()
}

// Github Maven Package repository configuration
publishing {
    repositories {
        maven {
            name = project.name
            url = uri("https://maven.pkg.github.com/$owner/${project.name}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>(project.name) {
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
            println("Publishing version: $version")
        }
    }
}
