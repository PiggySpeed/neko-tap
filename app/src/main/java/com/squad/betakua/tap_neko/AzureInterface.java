package com.squad.betakua.tap_neko;

import android.os.Build;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import static java.lang.System.getenv;

public class AzureInterface {
    private static final String CONNECTION_STRING_TEMPLATE = "DefaultEndpointsProtocol=https;" +
            "AccountName=%s;" +
            "AccountKey=%s";
    private final CloudBlobClient blobClient;

    public AzureInterface() throws URISyntaxException, InvalidKeyException {
        final String accountName = BuildConfig.AzureStorageAccountName;
        final String accountKey = BuildConfig.AzureStorageAccountKey;
        final String connectionString =
                String.format(CONNECTION_STRING_TEMPLATE, accountName, accountKey);
        final CloudStorageAccount storageAccount =
                CloudStorageAccount.parse(connectionString);
        this.blobClient = storageAccount.createCloudBlobClient();
    }

    /**
     * Upload an audio file to Azure
     * Warning: will overwrite file if file with the same audioTitle already exists
     *
     * @param audioTitle Title of audio clip to store
     * @param in         InputStream to read from
     * @param length     Length in bytes of file (or -1 if unknown)
     * @return True if upload succesful, false otherwise
     */
    public boolean uploadAudio(final String audioTitle, final InputStream in, final long length) {
        try {
            final CloudBlobContainer container =
                    this.blobClient.getContainerReference("instructionaudio");
            final CloudBlockBlob blockBlob = container.getBlockBlobReference(audioTitle);
            blockBlob.upload(in, length);
            return true;
        } catch (URISyntaxException e) {
            return false;
        } catch (StorageException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Download audio file from Azure
     *
     * @param audioTitle Title of audio clip in Azure Storage
     * @param out        OutputStream to write to
     * @return True if upload succesful, false otherwise
     */
    public boolean downloadAudio(final String audioTitle, final OutputStream out) {
        try {
            final CloudBlobContainer container =
                    this.blobClient.getContainerReference("instructionaudio");
            final CloudBlockBlob blockBlob = container.getBlockBlobReference(audioTitle);
            blockBlob.download(out);
            return true;
        } catch (URISyntaxException e) {
            return false;
        } catch (StorageException e) {
            return false;
        }
    }
}
