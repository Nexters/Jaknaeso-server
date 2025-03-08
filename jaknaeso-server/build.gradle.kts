import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("com.epages.restdocs-api-spec") version "0.19.4"
}

group = "org.nexters"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // restdocs-api-spec
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.4")

    // spring-rest-docs
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    // spring-security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    
    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-aop")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named("build") {
    finalizedBy("openapi3")
}

tasks.bootJar {
    archiveFileName.set("jaknaeso-server.jar")
    enabled = true
    launchScript()
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
