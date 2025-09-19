plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.jetbrains.kotlinx.kover")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}

kover.reports {
    verify {
        rule {
            minBound(80)
        }
    }

    filters {
        excludes {
            packages("net.thechance.faith.api.dto*")
            packages("net.thechance.faith.entity*")
        }
    }
}