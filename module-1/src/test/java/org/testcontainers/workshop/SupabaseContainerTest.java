package org.testcontainers.workshop;

import org.junit.jupiter.api.Test;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class SupabaseContainerTest {

    @Test
    void test() throws SQLException {
        try (SupabaseContainer supabase = new SupabaseContainer(DockerImageName.parse("supabase/postgres:15.1.1.55"))) {
            supabase.start();
            Connection connection = DriverManager.getConnection(supabase.getJdbcUrl(), supabase.getUsername(), supabase.getPassword());
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1");
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            resultSet.next();
            assertThat(resultSet.getInt(1)).isEqualTo(1);
            assertThat(supabase.getUsername()).isEqualTo("postgres");
        }
    }

    @Test
    void test2() throws SQLException {
        try (SupabaseContainer supabase = new SupabaseContainer(DockerImageName.parse("supabase/postgres:15.1.1.55"))
                .withPassword("testpassword")) {
            supabase.start();

            Connection connection = DriverManager.getConnection(supabase.getJdbcUrl(), supabase.getUsername(), supabase.getPassword());
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT 1");
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            resultSet.next();

            assertThat(resultSet.getInt(1)).isEqualTo(1);
            assertThat(supabase.getJdbcUrl()).endsWith("/postgres");
            assertThat(supabase.getUsername()).isEqualTo("postgres");
            assertThat(supabase.getPassword()).isEqualTo("testpassword");
        }
    }
}
