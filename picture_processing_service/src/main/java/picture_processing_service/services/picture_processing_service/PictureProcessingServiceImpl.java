package picture_processing_service.services.picture_processing_service;

import lombok.SneakyThrows;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Service that converts rgb images to black&white.
 */
@Service
public class PictureProcessingServiceImpl implements PictureProcessingService {
    @Override
    @SneakyThrows
    @Cacheable("pictures")
    public byte[] convertToBlackAndWhite(byte[] inputImage, String ext) {
        // conversion byte[] to Buffered images
        BufferedImage master = ImageIO.read(new ByteArrayInputStream(inputImage));
        BufferedImage greyScale = ImageIO.read(new ByteArrayInputStream(inputImage));

        // creating b&w picture
        new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null).filter(greyScale, greyScale);
        BufferedImage bw = new BufferedImage(master.getWidth(), master.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = bw.createGraphics();
        g2d.drawImage(master, 0, 0, null);
        g2d.dispose();

        // conversion BufferedImage to byte[]
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(bw, ext, output);
        return output.toByteArray();
    }
}
