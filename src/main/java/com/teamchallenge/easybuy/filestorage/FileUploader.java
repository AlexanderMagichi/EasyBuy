
package com.teamchallenge.easybuy.filestorage;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    /**
     * @param file
     * @param folder
     * @param fileName
     */
    void upload(MultipartFile file, String folder, String fileName);
}