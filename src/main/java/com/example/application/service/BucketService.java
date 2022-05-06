package com.example.application.service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class BucketService {

    /**
     * Get the credentials for OAuth2 clients from a GCP bucket
     *
     * @param projectId The ID of your GCP project
     * @param bucketName The ID of your GCS bucket
     * @return
     */
    public static byte[] getCredentialsFromGcpBucket(String projectId, String bucketName) {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        Page<Blob> blobs = storage.list(bucketName);
        for (Blob blob : blobs.iterateAll()) {
            return blob.getContent(Blob.BlobSourceOption.generationMatch());
        }
        return null;
    }

}
