plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.openapi.generator") version "7.9.0"
//    id("org.jetbrains.kotlin.kapt") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

// versions
val postgresVersion = "42.7.2"
val r2dbcVersion = "1.0.5.RELEASE"
val liquibaseVersion = "4.29.2"
val junit5Version = "2.0.20"
val junitLauncherVersion = "1.11.0"
val openApiStarterVersion = "2.6.0"
val jacksonDatabindNullable = "0.2.6"
val mapstructVersion = "1.5.5.Final"
val testContainersVersion = "1.20.1"

// openApi
val openApiSpecDir = "$rootDir/src/main/resources/openapi"
val openApiGeneratedApiDir = layout.buildDirectory.dir("generated").get().toString()
val openApiAdditionalProperties = mapOf("useJakartaEe" to "true")
val configOptionsUse = mapOf("useTags" to "true", "useSpringBoot3" to "true", "interfaceOnly" to "true", "dataLibrary" to "java8", "skipOperationExample" to "true")
val apiPackagePrefix = "com.example.api"
val modelPackagePrefix = "com.example.model"
val openApiSrcDir = "generated/src/main/java"

// taskNames
val openApiUsersApiTask = "openApiUsersApi"
val openApiCargoApiTask = "openApiCargoApi"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
//    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
//    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiStarterVersion")
    implementation("org.openapitools:jackson-databind-nullable:$jacksonDatabindNullable")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
//    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")
    runtimeOnly("org.postgresql:postgresql:$postgresVersion")
//    runtimeOnly("org.postgresql:r2dbc-postgresql:$r2dbcVersion")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$junit5Version")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitLauncherVersion")

}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

fun registerOpenApiTask(taskName: String, descriptionParam: String, specFile: String,
                        apiPackageParam: String, modelPackageParam: String) {
    tasks.register(taskName, org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
        description = descriptionParam
        group = "OpenAPI"
        generatorName.set("spring")
        inputSpec.set("$openApiSpecDir/$specFile")
        outputDir.set(openApiGeneratedApiDir)
        apiPackage.set(apiPackageParam)
        modelPackage.set(modelPackageParam)
        library.set("spring-boot")
        additionalProperties.set(openApiAdditionalProperties)
        configOptions.set(configOptionsUse)
        globalProperties.set(mapOf("generateSupportingFiles" to "false", "skipOperationExample" to "true"))
    }
}

registerOpenApiTask(openApiUsersApiTask, "Генерация API для сущности пользователей (users)",
    "users-api.yaml", "$apiPackagePrefix.users",
    "$modelPackagePrefix.users")
registerOpenApiTask(openApiCargoApiTask, "Генерация API для сущности грузов (cargo)",
    "cargo-api.yaml", "$apiPackagePrefix.cargo", "$modelPackagePrefix.cargo")

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir(openApiSrcDir).get().toString())
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn(tasks.named(openApiUsersApiTask), tasks.named(openApiCargoApiTask))
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}
