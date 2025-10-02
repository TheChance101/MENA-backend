package net.thechance.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ComponentScan(basePackages = ["net.thechance"])
@EnableJpaRepositories(basePackages = ["net.thechance"])
@EntityScan(basePackages = ["net.thechance"])
@PropertySource("classpath:identity-rate-limit.properties")
@EnableScheduling
class MenaApplication

fun main(args: Array<String>) {
    runApplication<MenaApplication>(*args)
}