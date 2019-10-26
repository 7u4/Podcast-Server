package lan.dk.podcastserver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lan.dk.podcastserver.service.JsonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

/**
 * Created by kevin on 15/06/2016 for Podcast Server
 */
@Disabled("Should have FFmpeg installed to be executed")
@SpringBootTest(classes = Application.class, webEnvironment=DEFINED_PORT)
public class ApplicationTest {

    @Autowired JsonService jsonService;
    @Value("${server.port}") Integer port;

    @Test
    public void should_respond_true_to_health_check() throws IOException {
        /* Given */
        String health = "http://localhost:"+ port +"/system/health";
        /* When */
        ApplicationHealthStatus status = jsonService.parseUrl(health).map(d -> d.read("$", ApplicationHealthStatus.class)).getOrElseThrow(RuntimeException::new);
        /* Then */
        assertThat(status).isNotNull();
        assertThat(status.getStatus()).isEqualTo("UP");
        assertThat(status.getDb().getStatus()).isEqualTo("UP");
        assertThat(status.getDiskSpace().getStatus()).isEqualTo("UP");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ApplicationHealthStatus extends HealthStatus {
        private DiskSpaceStatus diskSpace;
        private DbStatus db;

        public DiskSpaceStatus getDiskSpace() {
            return this.diskSpace;
        }

        public DbStatus getDb() {
            return this.db;
        }

        public void setDiskSpace(DiskSpaceStatus diskSpace) {
            this.diskSpace = diskSpace;
        }

        public void setDb(DbStatus db) {
            this.db = db;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DiskSpaceStatus extends HealthStatus {}
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DbStatus extends HealthStatus {}
    }

    public static class HealthStatus {
        private String status;

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}
