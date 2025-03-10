plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
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

extra["springCloudVersion"] = "2023.0.3"

val kotlinReflectVersion = "2.0.21"
val junitPlatformLauncherVersion = "1.11.3"
val kotlinTestJunit5 = "2.0.21"
val springCloudDepVersion = "2021.0.5"
val cloudConfigVersion = "4.1.3"

dependencies {
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
	implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinReflectVersion")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformLauncherVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinTestJunit5")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
