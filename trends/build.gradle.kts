plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.jetbrains.kotlinx.kover")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
}

kover.reports {
    verify {
        rule {
            minBound(80)
        }
    }

    filters {
        excludes {
            packages("net.thechance.trends.api.dto*")
            packages("net.thechance.trends.entity*")
        }
    }
}