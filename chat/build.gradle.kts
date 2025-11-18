plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.jetbrains.kotlinx.kover")
    kotlin("plugin.serialization") version "2.0.21"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("com.google.truth:truth:1.4.4")

    //websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    // AWS Configuration
    implementation(platform("software.amazon.awssdk:bom:2.33.8"))
    implementation("software.amazon.awssdk:s3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // phone number
    implementation("com.googlecode.libphonenumber:libphonenumber:9.0.14")
    
    implementation(project(":events"))
}

tasks.test {
    useJUnitPlatform()
    include("**/*Test.class")
    include("**/*Tests.class")
    include("**/*TestCase.class")
}


kover.reports {
    verify {
        rule {
            minBound(80)
        }
    }

    filters {
        excludes {
            packages("net.thechance.chat.api.dto*")
            packages("net.thechance.chat.entity*")
            packages("net.thechance.chat.service.model*")
            packages("net.thechance.chat.api.config*")
        }
    }
}