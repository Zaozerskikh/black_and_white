package picture_processing_service.controllers;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import picture_processing_service.jpa.repo.PictureRepository;
import picture_processing_service.services.picture_processing_service.PictureProcessingService;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@Log
@RestController
@RequestMapping("/pictures")
public class PictureController {
    @Value("${server.base_url}")
    private String baseUrl;

    @Autowired
    private PictureProcessingService pictureProcessingService;

    @Autowired
    private PictureRepository pictureRepository;

    @SneakyThrows
    @SuppressWarnings("all")
    @Async("pictureProcessorExecutor")
    @PostMapping(value = "/convertToLink", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<String> processAndReturnLink(
            @RequestParam("inputImage") MultipartFile inputImage,
            @RequestParam(required = false, name = "bw") boolean blackAndWhite,
            @RequestParam(required = false, name = "vingette") boolean vingette,
            @RequestParam(required = false, name = "blur_background") boolean blurBackground) {
        byte[] processedImage = pictureProcessingService.processImage(inputImage, blackAndWhite, vingette, blurBackground);
        return CompletableFuture.completedFuture(baseUrl + "/pictures/get/" + this.pictureProcessingService.savePicture(processedImage).getLink());
    }

    @SneakyThrows
    @SuppressWarnings("all")
    @Async("pictureProcessorExecutor")
    @PostMapping(value = "/convertToImage",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.IMAGE_JPEG_VALUE)
    public CompletableFuture<byte[]> processAndReturnImage(
            @RequestParam("inputImage") MultipartFile inputImage,
            @RequestParam(required = false, name = "bw") boolean blackAndWhite,
            @RequestParam(required = false, name = "vingette") boolean vingette,
            @RequestParam(required = false, name = "blur_background") boolean blurBackground) {
        return CompletableFuture.completedFuture(
                pictureProcessingService.processImage(inputImage, blackAndWhite, vingette, blurBackground)
        );
    }

    @SneakyThrows
    @Async("pictureProcessorExecutor")
    @GetMapping(value = "/get/{link}", produces = MediaType.IMAGE_JPEG_VALUE)
    public CompletableFuture<byte[]> getPictureByLink(@PathVariable String link) {
        return CompletableFuture.completedFuture(
                this.pictureRepository
                        .findByLink(link)
                        .orElseThrow(() -> new NoSuchElementException("not found"))
                        .getImage()
        );
    }
}
