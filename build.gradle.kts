plugins {
    kotlin("jvm") version "2.3.0-Beta2"
    kotlin("plugin.spring") version "2.3.0-Beta2"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.6"
}

group = "com.mpalourdio.projects"
version = "0.0.1-SNAPSHOT"
description = "Sample application with Spring AI"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

extra["springAiVersion"] = "1.1.0"
extra["commonsIOVersion"] = "2.21.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.ai:spring-ai-advisors-vector-store")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")
    implementation("org.apache.commons:commons-lang3")
    implementation("commons-io:commons-io:${property("commonsIOVersion")}")
    implementation("org.springframework.ai:spring-ai-starter-model-chat-memory-repository-jdbc")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.bootBuildImage {
    imageName.set("ghcr.io/mpalourdio/mpalourd-ai:latest")
    environment.put("LC_ALL", "en_US.UTF-8")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
tasks.register<Exec>("npmInstall") {
    workingDir = file("front")
    commandLine("npm", "install")
}

tasks.register<Exec>("ngBuild") {
    workingDir = file("front")
    commandLine("npm", "run", "build:prod")
    dependsOn("npmInstall")
}

tasks.register<Copy>("copyNgBuild") {
    from("front/dist/package")
    into("build/resources/main/public")
    dependsOn("ngBuild")
}

tasks.processResources { dependsOn("copyNgBuild") }
