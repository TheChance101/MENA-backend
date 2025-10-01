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
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("com.itextpdf:itext7-core:9.3.0")
    implementation("com.itextpdf:html2pdf:6.2.1")
    implementation("com.itextpdf:kernel:9.3.0")
    implementation("com.itextpdf:layout:9.3.0")
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