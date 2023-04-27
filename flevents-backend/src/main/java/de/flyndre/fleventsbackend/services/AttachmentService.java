package de.flyndre.fleventsbackend.services;

import de.flyndre.fleventsbackend.Models.Attachment;
import de.flyndre.fleventsbackend.repositories.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Contains methods to handle attachments
 * @since 2.2.0
 * @author Lukas Burkhardt
 */

@Service
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;

    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    /**
     * Creates and stores an attachment out of a MultipartFile
     * @param file the file to be stored
     * @return the saved attachment
     * @throws IOException if MultipartFile not accessible
     */
    public Attachment createAttachment(MultipartFile file) throws IOException {
        Attachment attachment = new Attachment();
        attachment.setUuid(null);
        attachment.setFilename(file.getOriginalFilename());
        attachment.setData(file.getBytes());
        return attachmentRepository.save(attachment);
    }

    /**
     * returns an attachment if there is one with the given id.
     * @param attachmentId
     * @return
     */
    public Attachment getAttachment(String attachmentId) {
        return attachmentRepository.findById(attachmentId).get();
    }
}
