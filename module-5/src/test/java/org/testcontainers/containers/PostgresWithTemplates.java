package org.testcontainers.containers;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.testcontainers.utility.MountableFile;

public class PostgresWithTemplates extends PostgreSQLContainer {
    public static final String ACTUAL_DATABASE_NAME = "realtest";

    public PostgresWithTemplates() {
        super("postgres:16-alpine");
        this.withCopyFileToContainer(MountableFile.forClasspathResource("schema.sql"),
                "/docker-entrypoint-initdb.d/");
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        this.runInitScriptIfRequired();

        //data is in, save it
        snapshot();
    }

    public void snapshot() {
        try {
            ExecResult execResult = this.execInContainer("psql", "-U", "test", "-c", "ALTER DATABASE test WITH is_template = TRUE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        reset();

        this.withDatabaseName(ACTUAL_DATABASE_NAME);
    }

    public void reset() {
        try {
            ExecResult execResult = this.execInContainer("psql", "-U", "test", "-c", "DROP DATABASE " + ACTUAL_DATABASE_NAME + " with (FORCE)");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            ExecResult execResult1 = this.execInContainer("psql", "-U", "test", "-c", "CREATE DATABASE " + ACTUAL_DATABASE_NAME + " TEMPLATE test");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
