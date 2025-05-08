package fact.useless.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UselessApiApplication

fun main(args: Array<String>) {
	runApplication<UselessApiApplication>(*args)
}
