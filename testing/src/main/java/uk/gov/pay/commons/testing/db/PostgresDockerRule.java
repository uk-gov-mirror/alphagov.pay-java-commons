package uk.gov.pay.commons.testing.db;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.IOException;

public class PostgresDockerRule implements TestRule {
    private PostgresContainer container;

    public PostgresDockerRule() {
        startPostgresIfNecessary();
    }

    public String getConnectionUrl() {
        synchronized(container) {
            return container.getConnectionUrl();
        }
    }

    @Override
    public Statement apply(Statement statement, Description description) {
        return statement;
    }

    private void startPostgresIfNecessary() {
        synchronized(container) {
            try {
                if (container == null) {
                    container = new PostgresContainer();
                }
            } catch (DockerCertificateException | InterruptedException | DockerException | IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getUsername() {
        synchronized(container) {
            return container.getUsername();
        }
    }

    public String getPassword() {
        synchronized(container) {
            return container.getPassword();
        }
    }

    public void stop() {
        synchronized(container) {
            container.stop();
            container = null;
        }
    }

    public String getDriverClass() {
        return "org.postgresql.Driver";
    }
}
