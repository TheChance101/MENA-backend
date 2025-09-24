plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.jetbrains.kotlinx.kover")
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

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
        }
    }
}