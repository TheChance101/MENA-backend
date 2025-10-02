package net.thechance.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@ComponentScan(basePackages = ["net.thechance"])
@EnableJpaRepositories(basePackages = ["net.thechance"])
@EntityScan(basePackages = ["net.thechance"])
@EnableAsync
class MenaApplication

fun main(args: Array<String>) {
    runApplication<MenaApplication>(*args)
}