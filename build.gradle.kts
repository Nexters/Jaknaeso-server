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

val oasDir = file("build/api-spec")
val swaggerUiDir = file("swagger-ui")

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("copyOasToSwaggerUi") {
    dependsOn("openapi3")
    from("$oasDir/openapi3.yaml")
    into(swaggerUiDir)
}

tasks.named("build") {
    finalizedBy("copyOasToSwaggerUi")
}

openapi3 {
    this.setServer("http://localhost:8080")
    title = "Jaknaeso API"
    description = "Jaknaeso API description"
    version = "0.1.0"
    format = "yaml"
}