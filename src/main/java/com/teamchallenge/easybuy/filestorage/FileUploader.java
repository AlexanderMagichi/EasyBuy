
package com.teamchallenge.easybuy.filestorage;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {
    /**
     * @param file     Файл для загрузки
     * @param folder   Папка в облаке (в Cloudinary это может быть префиксом public_id)
     * @param fileName Имя файла (будет использоваться как public_id)
     */
    void upload(MultipartFile file, String folder, String fileName);
}