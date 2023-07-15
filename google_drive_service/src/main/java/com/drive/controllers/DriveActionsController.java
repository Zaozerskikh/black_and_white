package com.drive.controllers;

import com.drive.enums.SessionKey;
import com.drive.exceptions.DriveAuthException;
import com.drive.services.google_drive_service.GoogleDriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class DriveActionsController {
    @Autowired
    private GoogleDriveService googleDriveService;

    @RequestMapping("/processAll")
    public ResponseEntity<String> getFiles(
            HttpSession session,
            @RequestParam(name = "source_folder_id") String sourceFolderId,
            @RequestParam(name = "output_folder_name") String outputFolderName,
            @RequestParam(required = false, name = "bw") boolean blackAndWhite,
            @RequestParam(required = false, name = "vingette") boolean vingette,
            @RequestParam(required = false, name = "blur_background") boolean blurBackground) {

        Object rawToken = session.getAttribute(SessionKey.GOOGLE_OAUTH_TOKEN.toString());

        if (rawToken == null) {
            session.setAttribute(SessionKey.BW.toString(), blackAndWhite);
            session.setAttribute(SessionKey.VINGETTE.toString(), vingette);
            session.setAttribute(SessionKey.BLUR_BACKGROUND.toString(), blurBackground);
            session.setAttribute(SessionKey.OUTPUT_FOLDER_NAME.toString(), outputFolderName);
            session.setAttribute(SessionKey.SOURCE_FOLDER_ID.toString(), sourceFolderId);
            throw new DriveAuthException("token does not exist");
        }

        return ResponseEntity.ok(googleDriveService.processBatch(
                rawToken.toString(), sourceFolderId, outputFolderName, blackAndWhite, vingette, blurBackground
        ));
    }
}
