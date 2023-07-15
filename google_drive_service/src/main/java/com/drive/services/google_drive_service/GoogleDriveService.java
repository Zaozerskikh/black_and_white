package com.drive.services.google_drive_service;

import lombok.SneakyThrows;

public interface GoogleDriveService {
    @SneakyThrows
    String processBatch(String accessToken, String sourceFolderId, String targetFolderName,
                        boolean blackAndWhite, boolean vingette, boolean blurBackground);
}
