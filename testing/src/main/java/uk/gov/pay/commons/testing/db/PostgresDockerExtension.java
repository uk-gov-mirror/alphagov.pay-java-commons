package uk.gov.pay.commons.testing.db;

import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import org.junit.jupiter.api.extension.Extension;

import java.io.IOException;

public class PostgresDockerExtension implements Extension {

    private static PostgresContainer container;

    public PostgresDockerExtension() {
        startPostgresIfNecessary();
    }

    public String getConnectionUrl() {
        return container.getConnectionUrl();
    }

    private void startPostgresIfNecessary() {
        try {
            if (container == null) {
                container = new PostgresContainer();
            }
        } catch (DockerCertificateException | InterruptedException | DockerException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return container.getUsername();
    }

    public String getPassword() {
        return container.getPassword();
    }

    public void stop() {
        container.stop();
        container = null;
    }

    public String getDriverClass() {
        return "org.postgresql.Driver";
    }
}
