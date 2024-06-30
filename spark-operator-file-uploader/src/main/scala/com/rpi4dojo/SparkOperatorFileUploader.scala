package com.rpi4dojo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableSwagger2
class SparkOperatorFileUploader

object SparkOperatorFileUploader extends App {
  SpringApplication.run(classOf[SparkOperatorFileUploader], args: _*)
}
@Configuration
@EnableSwagger2
@EnableWebMvc
class SwaggerConfig {
  @Bean
  def api(): Docket = {
    new Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(RequestHandlerSelectors.basePackage("com.rpi4dojo.controller"))
      .paths(PathSelectors.any())
      .build()
  }
}
