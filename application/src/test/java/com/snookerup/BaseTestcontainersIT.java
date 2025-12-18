package com.snookerup;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.Map;

/**
 * Base class used for any integration tests, using Testcontainers to spin up any necessary containers.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
public abstract class BaseTestcontainersIT {

    static PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>("postgres:17.4")
            .withDatabaseName("snookerup")
            .withUsername("snookerup")
            .withPassword("snookerup");

    static GenericContainer KEYCLOAK = new GenericContainer(DockerImageName.parse(
            "quay.io/keycloak/keycloak:18.0.0-legacy"))
            .withExposedPorts(8080)
            .withClasspathResourceMapping("/keycloak", "/tmp", BindMode.READ_ONLY)
            .withEnv("JAVA_OPTS",
                    "-Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile " +
                            "-Dkeycloak.migration.file=/tmp/snookerup-realm.json")
            .withEnv("DB_VENDOR", "H2")
            .withEnv("KEYCLOAK_USER", "keycloak")
            .withEnv("KEYCLOAK_PASSWORD", "keycloak")
            .waitingFor(Wait.forHttp("/auth").forStatusCode(200));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.cognito.issuerUri", () -> "http://localhost:" +
                KEYCLOAK.getMappedPort(8080) + "/auth/realms/snookerup");
        registry.add("spring.datasource.username", DATABASE::getUsername);
        registry.add("spring.datasource.password", DATABASE::getPassword);
        registry.add("spring.datasource.url",  DATABASE::getJdbcUrl);
    }

    static {
        DATABASE.start();
        KEYCLOAK.start();
    }

    protected DefaultOidcUser createOidcUser(String email, String username) {
        return new DefaultOidcUser(
                null,
                new OidcIdToken(
                        "some-id",
                        Instant.now(),
                        Instant.MAX,
                        Map.of(
                                "email", email,
                                "sub", "snookerup",
                                "name", username
                        )
                )
        );
    }
}
