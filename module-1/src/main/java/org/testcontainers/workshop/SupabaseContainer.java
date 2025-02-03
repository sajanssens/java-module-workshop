package org.testcontainers.workshop;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class SupabaseContainer extends GenericContainer<SupabaseContainer> {
    private String password = "p@$$w0rd";

    public SupabaseContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DockerImageName.parse("supabase/postgres"));
        withExposedPorts(5432);
        withEnv("POSTGRES_PASSWORD", getPassword());
        waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*\\s", 2));
    }

    public String getJdbcUrl() {
        return "jdbc:postgresql://" + getHost() + ":" + getMappedPort(5432) + "/postgres";
    }

    public String getUsername() {
        return "postgres";
    }

    public String getPassword() {
        return this.password;
    }

    public SupabaseContainer withPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    protected void configure() {
        withEnv("POSTGRES_PASSWORD", password);
        super.configure();
    }
}
