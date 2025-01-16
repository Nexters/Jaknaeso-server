import java.io.ByteArrayOutputStream

plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.epages.restdocs-api-spec") version "0.19.4"
}

group = "org.nexters"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // restdocs-api-spec
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.4")

    // spring-rest-docs
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named("build") {
    finalizedBy("openapi3")
}

val containerName = "api-doc"

tasks.register<Exec>("runSwagger") {
    commandLine("/usr/local/bin/docker", "ps", "--filter", "name=$containerName", "-qa")
    standardOutput = ByteArrayOutputStream()

    doLast {
        val containerId = standardOutput.toString().trim()
        if (containerId.isEmpty()) {
            exec {
                commandLine(
                    "/usr/local/bin/docker",
                    "run", "-d",
                    "-p", "8088:8080",
                    "--name", containerName,
                    "-v", "./build/api-spec:/usr/share/nginx/html/docs",
                    "-e", "URL=/docs/openapi3.yaml",
                    "swaggerapi/swagger-ui:latest"
                )
            }
        }
    }
}

openapi3 {
    this.setServer("http://localhost:8080")
    title = "Jaknaeso API"
    description = "Jaknaeso API description"
    version = "0.1.0"
    format = "yaml"
}