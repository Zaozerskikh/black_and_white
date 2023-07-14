package com.drive.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class DriveFiles implements Serializable {
    private String kind;
    private String nextPageToken;
    private String incompleteSearch;
    private List<File> files;

    public DriveFiles(List<File> files) {
        this.files = files;
    }
}
