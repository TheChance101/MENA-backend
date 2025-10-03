plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.jetbrains.kotlinx.kover")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.mockk:mockk:1.14.5")
    testImplementation("com.google.truth:truth:1.4.5")
    runtimeOnly("org.postgresql:postgresql")
    implementation(platform("software.amazon.awssdk:bom:2.33.8"))
    implementation("software.amazon.awssdk:s3")
    implementation("com.googlecode.libphonenumber:libphonenumber:9.0.14")
    implementation("com.github.ben-manes.caffeine:caffeine:2.9.1")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
}

kover.reports {
    verify {
        rule {
            minBound(0)
        }
    }

    filters {
        excludes {
            packages("net.thechance.identity.api.dto*")
            packages("net.thechance.identity.entity*")
            packages("net.thechance.identity.exception*")
        }
    }
}