package com.mpalourdio.projects.mpalourd_ai

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class MpalourdAiApplication {

//    @Bean
//    fun runner(builder: ChatClient.Builder): CommandLineRunner {
//        return CommandLineRunner { _: Array<String?>? ->
//            val chatClient = builder.build()
//            val response = chatClient.prompt("Just one word").call().content()
//            println(response)
//        }
//    }
}

fun main(args: Array<String>) {
    runApplication<MpalourdAiApplication>(*args)
}


