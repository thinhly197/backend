package com.ascend.campaign.configs;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableJpaRepositories("com.ascend.campaign.repositories")
@EnableTransactionManagement
@Slf4j
public class PersistenceConfiguration implements EnvironmentAware {

    @Autowired(required = false)
    private MetricRegistry metricRegistry;

    private Environment env;
    private RelaxedPropertyResolver dataSourcePropertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
        this.dataSourcePropertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        log.warn(" content={\"activity\":\"DataSource\", \"msg\":\"Configuring Datasource...\"}");
        StopWatch stopWatch = new StopWatch("Datasource");
        stopWatch.start("build");
        if (dataSourcePropertyResolver.getProperty("url") == null
                && dataSourcePropertyResolver.getProperty("databaseName") == null) {
            log.error(" content={\"activity\":\"DataSource\", "
                            + "\"msg\":\"\"Your database connection pool configuration is "
                            + "incorrect! The application\"\n"
                            + "  \" cannot start. Please check your Spring profile, "
                            + "current profiles are: {}",
                    Arrays.toString(env.getActiveProfiles()) + "\"}");

            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }

        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(dataSourcePropertyResolver.getProperty("dataSourceClassName"));
        config.addDataSourceProperty("url", dataSourcePropertyResolver.getProperty("url"));
        config.addDataSourceProperty("user", dataSourcePropertyResolver.getProperty("username"));
        config.addDataSourceProperty("password", dataSourcePropertyResolver.getProperty("password"));

        //MySQL optimizations, see https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        if ("com.mysql.jdbc.jdbc2.optional.MysqlDataSource".equals(
                dataSourcePropertyResolver.getProperty("dataSourceClassName"))) {
            config.addDataSourceProperty("cachePrepStmts",
                    dataSourcePropertyResolver.getProperty("cachePrepStmts", "true"));
            config.addDataSourceProperty("prepStmtCacheSize",
                    dataSourcePropertyResolver.getProperty("prepStmtCacheSize", "250"));
            config.addDataSourceProperty("prepStmtCacheSqlLimit",
                    dataSourcePropertyResolver.getProperty("prepStmtCacheSqlLimit", "2048"));
        }

        if (metricRegistry != null) {
            config.setMetricRegistry(metricRegistry);
        }

        HikariDataSource ds = new HikariDataSource(config);
        ds.setPoolName(dataSourcePropertyResolver.getProperty("poolName", "CampaignHikariCP"));
        ds.setMaximumPoolSize(Integer.valueOf(dataSourcePropertyResolver.getProperty("maximumPoolSize", "20")));

        stopWatch.stop();
        log.info(stopWatch.prettyPrint());

        return ds;
    }

}