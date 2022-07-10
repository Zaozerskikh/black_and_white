package picture_processing_service.controllers;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import picture_processing_service.services.picture_processing_service.PictureProcessingService;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Log
@RestController
@RequestMapping("/pictures")
public class PictureController {
    @Autowired
    private PictureProcessingService pictureProcessingService;

    @SneakyThrows
    @Async("pictureProcessorExecutor")
    @PostMapping(value = "/convert",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public CompletableFuture<byte[]> convert(@RequestParam("inputImage") MultipartFile inputImage) {
        log.info("START PROCESSING");
        var result = CompletableFuture.completedFuture(
                pictureProcessingService.convertToBlackAndWhite(inputImage.getBytes(),
                Objects.requireNonNull(inputImage.getContentType())
                        .substring(inputImage.getContentType().indexOf("/") + 1))
        );
        log.info("PROCESSING COMPLETED");
        return result;
    }
}
