package com.drive.controllers;

import com.drive.dto.File;
import com.drive.enums.SessionKey;
import com.drive.exceptions.DriveAuthException;
import com.drive.services.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class DriveActionsController {
    @Autowired
    private GoogleDriveService googleDriveService;

    @RequestMapping("/files")
    public ResponseEntity<List<File>> getFiles(HttpSession session) {
        Object rawToken = session.getAttribute(SessionKey.GOOGLE_OAUTH_TOKEN.toString());

        if (rawToken == null) {
            throw new DriveAuthException("token does not exist");
        }

        return ResponseEntity.ok(googleDriveService.getDriveFiles(
                rawToken.toString(), "1hcUMwXphRUsOkkP7SKESGPbGJd5_kR6r"
        ));
    }
}
