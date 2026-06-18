package com.teamchallenge.easybuy.infrastructure.persistence;

import com.teamchallenge.easybuy.security.crypto.AesGcmEncryptor;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * A JPA {@link AttributeConverter} that automatically encrypts and decrypts
 * String attributes using the {@link AesGcmEncryptor}.
 * <p>
 * This converter ensures that sensitive string data, such as personal details,
 * is stored in the database in an encrypted format and is transparently
 * decrypted when read by the application.
 * <p>
 * To use this converter on a specific entity field, annotate it with
 * {@code @Convert(converter = CryptoStringConverter.class)}.
 *
 * @see AesGcmEncryptor
 */
@Converter(autoApply = false)
public class CryptoStringConverter implements AttributeConverter<String, String> {

    /**
     * Converts a plaintext String attribute to an encrypted format for storage in the database column.
     * <p>
     * The method uses the {@link AesGcmEncryptor#encrypt(String)} method. If the attribute is null or blank,
     * it is returned as is.
     *
     * @param attribute The plaintext String to be encrypted.
     * @return The encrypted string, or the original value if it was null or blank.
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isBlank()) return attribute;
        return AesGcmEncryptor.encrypt(attribute);
    }

    /**
     * Converts an encrypted String from a database column back to a plaintext String for the entity attribute.
     * <p>
     * The method checks if the data has the expected encryption format ("v1$...") before attempting decryption.
     * This allows for graceful handling of unencrypted data (e.g., during a migration period).
     *
     * @param dbData The encrypted String retrieved from the database column.
     * @return The decrypted plaintext string, or the original value if it was not in the expected encrypted format, or null/blank.
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return dbData;
        // If already encrypted (our v1$... format), we decrypt it.
        if (dbData.startsWith("v1$")) {
            return AesGcmEncryptor.decrypt(dbData);
        }
        // Otherwise, we assume it's old unencrypted data (for migration purposes)
        return dbData;
    }
}