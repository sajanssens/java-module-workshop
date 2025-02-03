package workshop;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers // Testcontainers will manage lifecycle of containers annotated with @Container

class KafkaCatTest {
    Network network = Network.newNetwork();

    @Container
    KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"))
            .withListener(() -> "kafka:19092")
            .withNetwork(network);

    @Container
    GenericContainer<?> kcat = new GenericContainer<>("confluentinc/cp-kcat:7.4.1")
            .withCreateContainerCmdModifier(cmd -> {
                cmd.withEntrypoint("sh");
            })
            .withCopyToContainer(Transferable.of("Message produced by kcat"), "/data/msgs.txt")
            .withNetwork(network)
            .withCommand("-c", "tail -f /dev/null");

    @Test
    void testKafkaCat() throws IOException, InterruptedException {
        kcat.execInContainer("kcat", "-b", "kafka:19092", "-t", "msgs", "-P", "-l", "/data/msgs.txt");
        String stdout = kcat
                .execInContainer("kcat", "-b", "kafka:19092", "-C", "-t", "msgs", "-c", "1")
                .getStdout();
        assertThat(stdout).contains("Message produced by kcat");
    }
}
