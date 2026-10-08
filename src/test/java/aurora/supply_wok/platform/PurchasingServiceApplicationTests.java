package aurora.supply_wok.platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PurchasingServiceApplicationTests {

    @Test
    void contextLoads_whenApplicationStarts_loadsContextSuccessfully() {
        // Arrange
        boolean contextLoaded = true;

        // Act
        // Application context is loaded by SpringBootTest

        // Assert
        assertThat(contextLoaded).isTrue();
    }

}

