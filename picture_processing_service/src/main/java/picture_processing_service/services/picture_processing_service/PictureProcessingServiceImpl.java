package picture_processing_service.services.picture_processing_service;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import picture_processing_service.jpa.entities.Picture;
import picture_processing_service.jpa.repo.PictureRepository;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Service that converts rgb images to black&white.
 */
@Log
@Service
public class PictureProcessingServiceImpl implements PictureProcessingService {
    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private WebClient senderWebClient;

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

    @Override
    @SneakyThrows
    @Cacheable("pictures")
    public byte[] addVingette(byte[] inputImage, String ext) {
        // Conversion byte[] to BufferedImage
        BufferedImage master = ImageIO.read(new ByteArrayInputStream(inputImage));

        // Create a copy of the original image
        BufferedImage modifiedImage = new BufferedImage(master.getWidth(), master.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = modifiedImage.createGraphics();
        g2d.drawImage(master, 0, 0, null);
        g2d.dispose();

        // Apply vignetting effect
        int centerX = modifiedImage.getWidth() / 2;
        int centerY = modifiedImage.getHeight() / 2;
        int maxDistance = Math.max(centerX, centerY);

        for (int y = 0; y < modifiedImage.getHeight(); y++) {
            for (int x = 0; x < modifiedImage.getWidth(); x++) {
                int dx = x - centerX;
                int dy = y - centerY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                double vignette = 1.3 - (distance / maxDistance);

                // Darken the pixel color based on vignette value
                int rgb = modifiedImage.getRGB(x, y);
                int red = (int) (vignette * ((rgb >> 16) & 0xFF));
                int green = (int) (vignette * ((rgb >> 8) & 0xFF));
                int blue = (int) (vignette * (rgb & 0xFF));
                int newRgb = (red << 16) | (green << 8) | blue;

                modifiedImage.setRGB(x, y, newRgb);
            }
        }

        // Conversion BufferedImage to byte[]
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(modifiedImage, ext, output);

        return output.toByteArray();
    }

    @Override
    @SneakyThrows
    @Cacheable("pictures")
    public byte[] blurBackground(MultipartFile inputImage) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("img", inputImage.getResource());

        return senderWebClient
                .post()
                .uri("http://localhost:5000/process")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        log.info("OK");
                        return clientResponse.bodyToMono(byte[].class);
                    }
                    log.info("ERROR: code = " + clientResponse.statusCode());
                    return Mono.just(new byte[0]);
                })
                .onErrorReturn(inputImage.getBytes())
                .block();
    }

    @Override
    @SneakyThrows
    public Picture savePicture(byte[] image) {
        return this.pictureRepository.save(new Picture(image, UUID.randomUUID().toString()));
    }


}
