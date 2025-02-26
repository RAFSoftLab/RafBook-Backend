package raf.rs.messagingservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String uploadFile(MultipartFile file, Long textChannel);

}
