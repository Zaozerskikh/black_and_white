package picture_processing_service.services.picture_processing_service;

public interface PictureProcessingService {
    byte[] convertToBlackAndWhite(byte[] inputImage, String ext);
}
