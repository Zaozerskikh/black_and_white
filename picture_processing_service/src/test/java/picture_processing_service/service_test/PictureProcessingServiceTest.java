package picture_processing_service.service_test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import picture_processing_service.PictureProcessingServiceApplication;
import picture_processing_service.services.picture_processing_service.PictureProcessingService;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PictureProcessingServiceApplication.class)
public class PictureProcessingServiceTest {
    @Autowired
    PictureProcessingService pictureProcessingService;

    @Test
    @SneakyThrows
    void convertToBlackAndWhiteTest() {
        BufferedImage inputImage = ImageIO.read(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("test_image.jpg")));
        assertNotEquals(ColorSpace.TYPE_GRAY, inputImage.getColorModel().getColorSpace().getType());

        BufferedImage outputImage = ImageIO.read(new ByteArrayInputStream(
                pictureProcessingService.convertToBlackAndWhite(toByteArray(inputImage), "jpg")));

        assertEquals(ColorSpace.TYPE_GRAY, outputImage.getColorModel().getColorSpace().getType());
    }

    @SneakyThrows
    private byte[] toByteArray(BufferedImage bi) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpg", outputStream);
        return outputStream.toByteArray();
    }
}
