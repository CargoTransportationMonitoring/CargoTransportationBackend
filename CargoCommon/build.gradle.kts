import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4" apply false
    id("io.spring.dependency-management") version "1.1.6"
    id("java-library")
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories{
    mavenLocal()
    mavenCentral()
}
val liquibaseVersion = "4.29.2"
val mapstructVersion = "1.5.5.Final"

// dependency versions
val springCloudVersion = "2023.0.4"
val keyCloakAdminVersion = "25.0.6"

dependencies {
    // dependencies for other projects
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    api("org.liquibase:liquibase-core:$liquibaseVersion")
    api("org.mapstruct:mapstruct:$mapstructVersion")
    api("org.keycloak:keycloak-admin-client:$keyCloakAdminVersion")

    // just for library
    implementation("io.github.openfeign:feign-core")
    implementation("org.springframework:spring-webmvc")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        mavenBom("org.keycloak.bom:keycloak-adapter-bom:${keyCloakAdminVersion}")
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = false
}

tasks.withType<Jar> {
    enabled = true
}