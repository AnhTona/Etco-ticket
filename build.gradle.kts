plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.esco"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

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
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("com.turkraft.springfilter:jpa:3.2.5")
	compileOnly("org.projectlombok:lombok")
	// DevTools TẠM TẮT: nó tạo 2 classloaders → ngốn gấp đôi RAM → gây OOM khi upload PDF lớn
	 developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	runtimeOnly("com.mysql:mysql-connector-j")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("com.squareup.okhttp3:okhttp:4.12.0")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
	implementation("org.apache.commons:commons-text:1.12.0")
	implementation("org.apache.pdfbox:pdfbox:3.0.4")
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
jvmArgs = listOf("-Xms512m", "-Xmx4g")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
