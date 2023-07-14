package com.drive.services;

import com.drive.dto.DriveFiles;
import com.drive.dto.File;
import com.drive.dto.PictureDto;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

@Log
@Service
public class GoogleDriveService {
    @Value("${google.base_file_access_url}")
    private String driveFilesBaseUrl;

    @Value("google_drive_service/downloaded_files/")
    private String downloadedImagesTempFolder;

    @Value("google_drive_service/processed_files/")
    private String processedImagesTempFolder;

    @SneakyThrows
    public List<File> processBatch(String accessToken, String sourceFolderId, String targetFolderName,
                                   boolean blackAndWhite, boolean vingette, boolean blurBackground) {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        WebClient googleDriveWebClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .exchangeStrategies(strategies)
                .build();

        WebClient pictureProcessingServerWebClient = WebClient
                .builder()
                .exchangeStrategies(strategies)
                .build();

        googleDriveWebClient.get()
                .uri(driveFilesBaseUrl + "?q=parents in '" + sourceFolderId + "'&mimeType='image/jpeg'")
                .retrieve()
                .bodyToMono(DriveFiles.class)
                .block()
                .getFiles()
                .stream()
                .map(file -> new PictureDto(
                        googleDriveWebClient.get()
                            .uri(driveFilesBaseUrl + "/" + file.getId() + "?alt=media")
                            .accept(MediaType.IMAGE_JPEG)
                            .retrieve()
                            .bodyToMono(byte[].class)
                            .block(),
                        file.getId(),
                        file.getName()
                ))
                .forEach(image -> {
                    try {
                        String filePath = downloadedImagesTempFolder + image.getFileId() + "________" + image.getName();
                        ImageIO.write(
                                ImageIO.read(new ByteArrayInputStream(image.getImage())), "jpg",
                                new java.io.File(filePath)
                        );

                        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                        body.add("inputImage", new PathResource(new java.io.File(filePath).toPath()));

                        URI uri = URI.create("http://localhost:8081/pictures/convertToImage" +
                                "?bw=" + blackAndWhite +
                                "&vingette=" + vingette +
                                "&blur_background=" + blurBackground);

                        java.io.File processedImage = new java.io.File(processedImagesTempFolder + image.getFileId() + "________" + image.getName());

                        try (OutputStream outputStream = new FileOutputStream(processedImage)) {
                            outputStream.write(
                                    pictureProcessingServerWebClient.post()
                                            .uri(uri)
                                            .contentType(MediaType.MULTIPART_FORM_DATA)
                                            .body(BodyInserters.fromMultipartData(body))
                                            .retrieve()
                                            .bodyToMono(byte[].class)
                                            .block()
                            );
                        } catch (Exception e) {
                            log.warning("fetching processed image error: " + e.getMessage());
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
        return null;
    }

}
