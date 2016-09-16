package com.hanksha.mple.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType

import javax.sql.DataSource

/**
 * Created by vivien on 8/28/16.
 */
@Configuration
class DataConfig {

    @Bean
    @Profile('dev')
    public DataSource devDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScripts('classpath:schema.sql', 'classpath:data.sql')
                .build();
    }

}
