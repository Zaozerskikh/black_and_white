package com.drive.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class File implements Serializable {
    private String kind;
    private String id;
    private String name;
    private String mimeType;
    private List<String> parents;

}
