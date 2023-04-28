package de.flyndre.fleventsbackend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentPreview {
    private String uuid;
    private String filename;
    private String url;
}
