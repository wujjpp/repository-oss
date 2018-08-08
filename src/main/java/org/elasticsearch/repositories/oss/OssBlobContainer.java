package org.elasticsearch.repositories.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.blobstore.BlobMetaData;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.BlobStoreException;
import org.elasticsearch.common.blobstore.support.AbstractBlobContainer;
import org.elasticsearch.common.logging.Loggers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.util.Map;

public class OssBlobContainer extends AbstractBlobContainer {
    protected final Logger logger = Loggers.getLogger(OssBlobContainer.class);
    protected final OssBlobStore blobStore;
    protected final String keyPath;

    public OssBlobContainer(BlobPath path, OssBlobStore blobStore) {
        super(path);
        this.keyPath = path.buildAsString();
        this.blobStore = blobStore;
    }

    @Override 
    public boolean blobExists(String blobName) {
        logger.trace("blobExists({})", blobName);
        try {
            return blobStore.blobExists(buildKey(blobName));
        } catch (OSSException | ClientException | IOException e) {
            logger.warn("can not access [{}] in bucket {{}}: {}", blobName, blobStore.getBucket(),
                e.getMessage());
            throw new BlobStoreException("Failed to check if blob [" + blobName + "] exists", e);
        }
    }

    @Override 
    public InputStream readBlob(String blobName) throws IOException {
        logger.trace("readBlob({})", blobName);
        if (!blobExists(blobName)) {
            throw new NoSuchFileException("[" + blobName + "] blob not found");
        }
        return blobStore.readBlob(buildKey(blobName));
    }


    @Override 
    public void writeBlob(String blobName, InputStream inputStream, long blobSize)
        throws IOException {
        if (blobExists(blobName)) {
            throw new FileAlreadyExistsException(
                "blob [" + blobName + "] already exists, cannot overwrite");
        }
        logger.trace("writeBlob({}, stream, {})", blobName, blobSize);
        blobStore.writeBlob(buildKey(blobName), inputStream, blobSize);
    }


    @Override 
    public void deleteBlob(String blobName) throws IOException {
        logger.trace("deleteBlob({})", blobName);
        if (!blobExists(blobName)) {
            throw new NoSuchFileException("Blob [" + blobName + "] does not exist");
        }
        try {
            blobStore.deleteBlob(buildKey(blobName));
        } catch (OSSException | ClientException e) {
            logger.warn("can not access [{}] in bucket {{}}: {}", blobName, blobStore.getBucket(),
                e.getMessage());
            throw new IOException(e);
        }

    }

    @Override 
    public Map<String, BlobMetaData> listBlobs() throws IOException {
        return listBlobsByPrefix(null);
    }


    @Override 
    public Map<String, BlobMetaData> listBlobsByPrefix(String blobNamePrefix)
        throws IOException {
        logger.trace("listBlobsByPrefix({})", blobNamePrefix);
        try {
            return blobStore.listBlobsByPrefix(keyPath, blobNamePrefix);
        } catch (IOException e) {
            logger.warn("can not access [{}] in bucket {{}}: {}", blobNamePrefix,
                blobStore.getBucket(), e.getMessage());
            throw new IOException(e);
        }
    }

    @Override 
    public void move(String sourceBlobName, String targetBlobName) throws IOException {
        logger.trace("move({}, {})", sourceBlobName, targetBlobName);
        if (!blobExists(sourceBlobName)) {
            throw new IOException("Blob [" + sourceBlobName + "] does not exist");
        } else if (blobExists(targetBlobName)) {
            throw new IOException("Blob [" + targetBlobName + "] has already exist");
        }
        try {
            blobStore.move(buildKey(sourceBlobName), buildKey(targetBlobName));
        } catch (OSSException | ClientException | NoSuchFileException e) {
            logger.warn("can not move blob [{}] to [{}] in bucket {{}}: {}", sourceBlobName,
                targetBlobName, blobStore.getBucket(), e.getMessage());
            throw new IOException(e);
        }
    }

    protected String buildKey(String blobName) {
        return keyPath + (blobName == null ? StringUtils.EMPTY : blobName);
    }
}
