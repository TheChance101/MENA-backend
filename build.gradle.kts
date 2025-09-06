plugins {
    id("org.springframework.boot") version "3.3.2" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
}

allprojects {
    group = "net.thechance"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}