plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.liquibase.gradle") version "2.2.1"
}

liquibase {
    activities {
        register("main") {
            arguments = listOf(
                "changeLogFile=src/main/resources/db/changelog/db.changelog-master.yaml",
                "url=${project.findProperty("spring.datasource.url") as String}",
                "username=${project.findProperty("spring.datasource.username") as String}",
                "password=${project.findProperty("spring.datasource.password") as String}",
            )
        }
    }
    runList = "main"
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
    // liquibase
    runtimeOnly("org.liquibase:liquibase-core")
}
