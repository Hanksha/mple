package com.hanksha.mple.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType

import javax.annotation.PostConstruct
import javax.sql.DataSource
import java.nio.file.Files
import java.nio.file.Paths

@Configuration
class DataConfig {

    @PostConstruct
    void init() {
        if(!Files.exists(Paths.get('storage')))
            Files.createDirectory(Paths.get('storage'))

        if(!Files.exists(Paths.get('storage/database')))
            Files.createDirectory(Paths.get('storage/database'))
    }

    @Bean
    @Profile('dev')
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setName('mpledb')
                .setType(EmbeddedDatabaseType.H2)
                .addScripts('classpath:schema.sql', 'classpath:data.sql')
                .build();
    }

    @Bean
    @Profile('prod')
    public DataSource prodDataSource() {

        DriverManagerDataSource ds = new DriverManagerDataSource()
        ds.setUrl('jdbc:h2:file:./storage/database/mpledb')
        ds
    }

}
