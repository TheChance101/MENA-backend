plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.jetbrains.kotlinx.kover")
}

dependencies {
    implementation(project(":events"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation(kotlin("test"))
    implementation("org.springframework.boot:spring-boot-starter-web")
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
            packages("net.thechance.wallet.api.dto*")
            packages("net.thechance.wallet.entity*")
        }
    }
}