package com.dkbcodefactory.urlshortenerapi.configuration.postgres

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource

@Configuration
@Profile("local")
class PostgresContainerConfig {

    private val IMAGE_VERSION = "postgres:17-alpine"
    private val DOCKER_IMAGE = DockerImageName.parse(IMAGE_VERSION)

    private var postgreSQLContainer: PostgreSQLContainer<*>? = null

    @Bean
    fun postgresContainer(): PostgreSQLContainer<*> {
        if (postgreSQLContainer == null) {
            postgreSQLContainer = PostgreSQLContainer(DOCKER_IMAGE)
                .withDatabaseName("url-shoter-db")
                .withUsername("milsondev")
                .withPassword("5e796cf3fc50")
                .apply { start() }
        }
        return postgreSQLContainer!!
    }

    @Bean
    fun dataSource(postgresContainer: PostgreSQLContainer<*>): DataSource {
        return DriverManagerDataSource().apply {
            setDriverClassName("org.postgresql.Driver")
            setUrl(postgresContainer.jdbcUrl)
            setUsername(postgresContainer.username)
            setPassword(postgresContainer.password)
        }
    }
}