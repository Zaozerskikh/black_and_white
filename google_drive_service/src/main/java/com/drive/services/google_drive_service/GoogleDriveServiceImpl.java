package com.drive.services.google_drive_service;

import com.drive.dto.DriveFileDto;
import com.drive.dto.DriveFiles;
import com.drive.dto.PictureDto;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

@Log
@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {
    @Value("${google.base_file_access_url}")
    private String driveFilesAccessBaseUrl;

    @Value("${google.base_file_upload_url}")
    private String driveFilesUploadBaseUrl;

    @Value("google_drive_service/downloaded_files/")
    private String downloadedImagesTempFolder;

    @Value("google_drive_service/processed_files/")
    private String processedImagesTempFolder;

    @SneakyThrows
    @Override
    public String processBatch(String accessToken, String sourceFolderId, String targetFolderName,
                                           boolean blackAndWhite, boolean vingette, boolean blurBackground) {
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
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

        String resultFolderId = createFolder(googleDriveWebClient, "output", sourceFolderId).getId();


        googleDriveWebClient.get()
                .uri(driveFilesAccessBaseUrl + "?q=parents in '" + sourceFolderId + "' and mimeType='image/jpeg'")
                .retrieve()
                .bodyToMono(DriveFiles.class)
                .block()
                .getFiles()
                .stream()
                .map(file -> new PictureDto(
                        googleDriveWebClient.get()
                            .uri(driveFilesAccessBaseUrl + "/" + file.getId() + "?alt=media")
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

                        java.io.File processedImage = new java.io.File(processedImagesTempFolder + image.getFileId() + "________" + image.getName());

                        try (OutputStream outputStream = new FileOutputStream(processedImage)) {
                            outputStream.write(
                                    this.processImage(googleDriveWebClient, filePath, blackAndWhite, vingette, blurBackground)
                            );
                            log.info("file uploaded: " + this.uploadFile(accessToken, resultFolderId, processedImage, image.getName()).getId());
                        } catch (Exception e) {
                            log.warning("fetching processed image error: " + e.getMessage());
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
        return "https://drive.google.com/drive/u/0/folders/" + resultFolderId;
    }

    public DriveFileDto createFolder(WebClient googleDriveWebClient, String folderName, String parentFolderId) {
        DriveFileDto fileMetadata = new DriveFileDto();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(List.of(parentFolderId));
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("metadata", fileMetadata);

        return googleDriveWebClient
                .post()
                .uri(driveFilesUploadBaseUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(DriveFileDto.class)
                .block();
    }

    @SneakyThrows
    public DriveFileDto uploadFile(String token, String parentFolderId, File file, String fileName) {
        DriveFileDto metadata = new DriveFileDto();
        metadata.setName(fileName);
        metadata.setParents(List.of(parentFolderId));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("metadata", metadata);
        body.add("file", new FileSystemResource(file));

        return  WebClient
                .create()
                .post()
                .uri(driveFilesUploadBaseUrl + "?uploadType=multipart")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(DriveFileDto.class)
                .block();
    }

    private byte[] processImage(WebClient pictureProcessingServerWebClient, String filePath,
                                boolean blackAndWhite, boolean vingette, boolean blurBackground) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("inputImage", new PathResource(new java.io.File(filePath).toPath()));

        URI uri = URI.create("http://localhost:8081/pictures/convertToImage" +
                "?bw=" + blackAndWhite +
                "&vingette=" + vingette +
                "&blur_background=" + blurBackground);

        return pictureProcessingServerWebClient.post()
                .uri(uri)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
}
