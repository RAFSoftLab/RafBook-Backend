package raf.rs.messagingservice.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import raf.rs.messagingservice.service.FileService;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadFile(MultipartFile file, Long textChannel) {
        return null;
    }
}
