plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.jetbrains.kotlinx.kover")
    kotlin("plugin.jpa")
    kotlin("plugin.serialization") version "1.9.10"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation(platform("software.amazon.awssdk:bom:2.33.8"))
    implementation("software.amazon.awssdk:s3")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("com.google.truth:truth:1.4.4")

}

kover.reports {
    verify {
        rule {
            minBound(0)
        }
    }

    filters {
        excludes {
            packages("net.thechance.faith.api.dto*")
            packages("net.thechance.faith.entity*")
        }
    }
}