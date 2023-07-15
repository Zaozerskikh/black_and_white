package com.drive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class DriveFileContentDTO {
    private String type;
    private File file;
}
