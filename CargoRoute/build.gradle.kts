plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.openapi.generator") version "7.9.0"
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
    mavenLocal()
    mavenCentral()
}

// dependency versions
val postgresVersion = "42.7.2"
val r2dbcVersion = "1.0.5.RELEASE"
val liquibaseVersion = "4.29.2"
val junit5Version = "2.0.20"
val junitLauncherVersion = "1.11.0"
val openApiStarterVersion = "2.6.0"
val jacksonDatabindNullable = "0.2.6"
val mapstructVersion = "1.5.5.Final"
val testContainersVersion = "1.20.1"
val keyCloakAdminVersion = "25.0.6"

// openApi
val openApiSpecDir = "$rootDir/CargoRoute/src/main/resources/openapi"
val openApiGeneratedApiDir = layout.buildDirectory.dir("generated").get().toString()
val openApiAdditionalProperties = mapOf("useJakartaEe" to "true")
val configOptionsUse = mapOf(
    "useTags" to "true",
    "useSpringBoot3" to "true",
    "interfaceOnly" to "true",
    "dataLibrary" to "java8",
    "skipOperationExample" to "true"
)
val apiPackagePrefix = "com.example.api"
val modelPackagePrefix = "com.example.model"
val openApiSrcDir = "generated/src/main/java"

// taskNames
val openApiRouteApiTask = "openApiRouteApi"

extra["springCloudVersion"] = "2023.0.4"

dependencies {
    implementation(project(":CargoCommon"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiStarterVersion")
    implementation("org.openapitools:jackson-databind-nullable:$jacksonDatabindNullable")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    runtimeOnly("org.postgresql:postgresql:$postgresVersion")

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

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("org.keycloak.bom:keycloak-adapter-bom:${keyCloakAdminVersion}")
    }
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

fun registerOpenApiTask(
    taskName: String, descriptionParam: String, specFile: String,
    apiPackageParam: String, modelPackageParam: String
) {
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

registerOpenApiTask(
    openApiRouteApiTask, "Генерация API для сущности маршрутов (route)",
    "route-api.yaml", "$apiPackagePrefix.route", "$modelPackagePrefix.route"
)

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir(openApiSrcDir).get().toString())
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn(tasks.named(openApiRouteApiTask))
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}
