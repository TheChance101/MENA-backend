plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.liquibase.gradle") version "2.2.1"
}

liquibase {
    activities {
        register("mainConfig") {
            arguments = listOf(
                "changeLogFile=${project.findProperty("spring.liquibase.change-log") as String}",
                "url=${project.findProperty("spring.datasource.url") as String}",
                "username=${project.findProperty("spring.datasource.username") as String}",
                "password=${project.findProperty("spring.datasource.password") as String}",
            )
        }
    }
    runList = "mainConfig"
}

dependencies {
    implementation(project(":identity"))
    implementation(project(":chat"))
    implementation(project(":dukan"))
    implementation(project(":faith"))
    implementation(project(":wallet"))
    implementation(project(":trends"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation(platform("software.amazon.awssdk:bom:2.33.8"))
    implementation("software.amazon.awssdk:s3")

    implementation ("org.springframework.boot:spring-boot-starter-security")
    
    runtimeOnly("org.liquibase:liquibase-core")
}
