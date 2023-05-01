package com.PoolingAPI.dbConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
public class DbConfig {

    @Autowired
    private Environment env;

    @Bean(name = "dbMMPharmacy")
    @Primary
    public DataSource firstDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.MMPharmacy.driver-class-name")));
        dataSource.setUrl(env.getProperty("spring.MMPharmacy.url"));
        dataSource.setUsername(env.getProperty("spring.MMPharmacy.username"));
        dataSource.setPassword(env.getProperty("spring.MMPharmacy.password"));
        return dataSource;
    }

    @Bean(name = "jdbcMMPharmacy")
    public JdbcTemplate jdbcTemplateOne(@Qualifier("dbMMPharmacy") DataSource ds)
    {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "dbFDB")
    public DataSource secondDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("spring.FDB.driver-class-name")));
        dataSource.setUrl(env.getProperty("spring.FDB.url"));
        dataSource.setUsername(env.getProperty("spring.FDB.username"));
        dataSource.setPassword(env.getProperty("spring.FDB.password"));
        return dataSource;
    }

    @Bean(name = "jdbcFDB")
    public JdbcTemplate jdbcTemplateTwo(@Qualifier("dbFDB") DataSource ds)
    {
        return new JdbcTemplate(ds);
    }
}
