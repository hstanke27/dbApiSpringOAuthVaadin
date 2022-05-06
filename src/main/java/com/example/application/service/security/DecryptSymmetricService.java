package com.example.application.service.security;

import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.DecryptResponse;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DecryptSymmetricService {

    /**
     * Decrypt data that was encrypted using a symmetric key.
     * @param projectId gcp project Id
     * @param locationId gcp location Id - f. e. us-east1
     * @param keyRingId gcp key ring Id
     * @param keyId gcp key id (name)
     * @param ciphertext text to decrypt
     * @return
     */
    public String decryptSymmetric(String projectId, String locationId, String keyRingId, String keyId, byte[] ciphertext) {

        try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
            // Build the key version name from the project, location, key ring, and
            // key.
            CryptoKeyName keyName = CryptoKeyName.of(projectId, locationId, keyRingId, keyId);

            // Decrypt the response.
            DecryptResponse response = client.decrypt(keyName, ByteString.copyFrom(ciphertext));
            return response.getPlaintext().toStringUtf8();
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

}
