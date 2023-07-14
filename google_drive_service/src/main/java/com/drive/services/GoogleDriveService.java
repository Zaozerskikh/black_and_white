package com.drive.services;

import com.drive.dto.DriveFiles;
import com.drive.dto.File;
import com.drive.dto.PictureDto;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Log
@Service
public class GoogleDriveService {
    @SneakyThrows
    public List<File> getDriveFiles(String accessToken, String folderId) {
        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        webClient.get()
                .uri("https://www.googleapis.com/drive/v3/files?q=parents in '" + folderId + "'&mimeType='image/jpeg'")
                .retrieve()
                .bodyToMono(DriveFiles.class)
                .block()
                .getFiles()
                .stream()
                .map(file -> new PictureDto(
                        webClient.get()
                            .uri("https://www.googleapis.com/drive/v3/files/" + file.getId() + "?alt=media")
                            .accept(MediaType.IMAGE_JPEG)
                            .retrieve()
                            .bodyToMono(byte[].class)
                            .block(),
                        file.getId(),
                        file.getName()
                ))
                .forEach(image -> {
                    try {
                        ImageIO.write(
                                ImageIO.read(new ByteArrayInputStream(image.getImage())), "jpg",
                                new java.io.File("google_drive_service/downloaded_files/" + image.getFileId() + "________" + image.getName())
                        );
                    } catch (Exception e) {
                        log.warning("error in file writing: " + e.getMessage());
                    }
                });
        return null;
    }

}
