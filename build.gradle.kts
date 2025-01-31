import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.2" apply false
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allprojects {
    group = "org.nexters"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.diffplug.spotless")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        encoding("UTF-8")
        java {
            toggleOffOn()
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
            googleJavaFormat()
        }
    }

    tasks.compileJava {
        dependsOn("spotlessApply")
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
        all {
            exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        }
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        testImplementation("org.springframework.boot:spring-boot-starter-test")

        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        // log4j2
        implementation("org.springframework.boot:spring-boot-starter-log4j2")
        implementation("com.lmax:disruptor:4.0.0")

        // p6spy (local db query log)
        implementation("p6spy:p6spy:3.9.1")
        implementation("com.github.vertical-blank:sql-formatter:2.0.5")

        // log4jdbc
        implementation("org.bgee.log4jdbc-log4j2:log4jdbc-log4j2-jdbc4.1:1.16")

        // Jackson
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.2")
    }
}


project(":jaknaeso-server") {
    dependencies {
        implementation(project(":jaknaeso-core"))
    }
}

project(":jaknaeso-core") {

    val jar: Jar by tasks
    val bootJar: BootJar by tasks

    bootJar.enabled = false
    jar.enabled = true
}

tasks.register<Copy>("installGitHooks") {
    description = "Git hooks를 설치"
    group = "git"

    from("$rootDir/scripts/git-hooks/")
    into("$rootDir/.git/hooks/")

    filePermissions {
        user {
            execute = true
            read = true
            write = true
        }
        group.execute = true
        other.execute = true
    }
}
