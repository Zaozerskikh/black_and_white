package picture_processing_service.services.picture_processing_service;

import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;
import picture_processing_service.jpa.entities.Picture;

public interface PictureProcessingService {
    byte[] convertToBlackAndWhite(byte[] inputImage, String ext);

    @SneakyThrows
    byte[] addVingette(byte[] inputImage, String ext);

    @SneakyThrows
    byte[] blurBackground(MultipartFile inputImage);

    @SneakyThrows
    Picture savePicture(byte[] image);
}
