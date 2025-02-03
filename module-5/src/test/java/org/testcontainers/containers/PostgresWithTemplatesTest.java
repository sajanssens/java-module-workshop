package org.testcontainers.containers;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PostgresWithTemplatesTest {
    static PostgresWithTemplates pg = new PostgresWithTemplates();

    static {
        pg.start();
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    public static void setup(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.username", pg::getUsername);
        reg.add("spring.datasource.password", pg::getPassword);
        reg.add("spring.datasource.url", pg::getJdbcUrl);
    }

    @BeforeEach
    public void reset() {
        pg.reset();
        // Resetting the database terminates the connections, so we need to soft-evict those in the connection pool as well.
        // Otherwise, tests would fail with postgres error 'FATAL: terminating connection due to administrator command'.
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.getHikariPoolMXBean().softEvictConnections();
        }
    }
    @Test
    void contextLoads() {
        String s = jdbcTemplate.queryForObject("SELECT current_database();", String.class);

        assertThat(s).isNotEqualTo("test");

        var count = jdbcTemplate.queryForObject("SELECT count(*) from products;", Integer.class);
        assertThat(count).isPositive();
        jdbcTemplate.execute("TRUNCATE TABLE products;");

        var afterTruncate = jdbcTemplate.queryForObject("SELECT count(*) from products;", Integer.class);
        assertThat(afterTruncate).isZero();
    }

    @Test
    void contextLoads2() {
        String s = jdbcTemplate.queryForObject("SELECT current_database();", String.class);

        assertThat(s).isNotEqualTo("test");

        var count = jdbcTemplate.queryForObject("SELECT count(*) from products;", Integer.class);
        assertThat(count).isPositive();
        jdbcTemplate.execute("TRUNCATE TABLE products;");

        var afterTruncate = jdbcTemplate.queryForObject("SELECT count(*) from products;", Integer.class);
        assertThat(afterTruncate).isZero();
    }
}
