package org.elasticsearch.repositories.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.model.*;

import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.settings.Settings;

import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

public class OssStorageService extends AbstractComponent {

	static Logger logger = Logger.getLogger(OssStorageService.class);

	private OSSClient client;

	public OssStorageService(Settings settings, RepositoryMetaData metadata) {
		super(settings);
		this.client = createClient(metadata);
	}

	private OSSClient createClient(RepositoryMetaData repositoryMetaData) {
		OSSClient client;
		
		String accessKeyId = OssStorageSettings.getSetting(OssStorageSettings.ACCESS_KEY_ID, repositoryMetaData);
		String secretAccessKey = OssStorageSettings.getSetting(OssStorageSettings.SECRET_ACCESS_KEY, repositoryMetaData);
		String endpoint = OssStorageSettings.getSetting(OssStorageSettings.ENDPOINT, repositoryMetaData);
		
		String securityToken = OssStorageSettings.getSetting(OssStorageSettings.SECURITY_TOKEN, repositoryMetaData, true);
		// String securityToken = OssStorageSettings.SECURITY_TOKEN.get(repositoryMetaData.settings());
		
		if (Strings.hasLength(securityToken)) {
			client = new OSSClient(endpoint, accessKeyId, secretAccessKey, securityToken);
		} else {
			client = new OSSClient(endpoint, accessKeyId, secretAccessKey);
		}
		return client;
	}

	public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) throws OSSException, ClientException {
		return SocketAccess.doPrivilegedException(() -> {
			logger.info("try to delete object...");

			DeleteObjectsResult result = this.client.deleteObjects(deleteObjectsRequest);

			logger.info("result :" + result);

			return result;
		});
	}

	public boolean doesObjectExist(String bucketName, String key)
			throws ClientException, ServiceException, URISyntaxException {
		return SocketAccess.doPrivilegedException(() -> {

			try {
				logger.info("try to check object exist...");

				boolean result = this.client.doesObjectExist(bucketName, key);

				logger.info(bucketName + " -> " + key + " exists? " + result);

				return result;
			} catch (OSSException err) {
				logger.info(err);
				return false;
			}
		});
	}

	public boolean doesBucketExist(String bucketName) throws OSSException, ClientException {
		return SocketAccess.doPrivilegedException(() -> {

			logger.info("try to check " + bucketName + " exist...");

			boolean flag = this.client.doesBucketExist(bucketName);

			logger.info(bucketName + " exist? " + flag);

			return flag;
		});

	}

	public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws OSSException, ClientException {
		return SocketAccess.doPrivilegedException(() -> {

			logger.info("try to list objects...");

			ObjectListing result = this.client.listObjects(listObjectsRequest);

			logger.info(result);

			return result;
		});
	}

	public OSSObject getObject(String bucketName, String key) throws OSSException, ClientException {
		return SocketAccess.doPrivilegedException(() -> {
			logger.info("try to get: " + bucketName + ":" + key);
			OSSObject result = this.client.getObject(bucketName, key);
			logger.info(result);
			return result;
		});
	}

	public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata, boolean failIfAlreadyExists)
			throws OSSException, ClientException {
		return SocketAccess.doPrivilegedException(() -> {
			logger.info("try to create: " + bucketName + ":" + key);
			// TODO: add checking
			PutObjectResult result = this.client.putObject(bucketName, key, input, metadata);
			logger.info(" ---> " + result);
			return result;
		});
	}

	public void deleteObject(String bucketName, String key) throws OSSException, ClientException {
		SocketAccess.doPrivilegedVoidException(() -> {
			this.client.deleteObject(bucketName, key);
		});
	}

	public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
			String destinationKey) throws OSSException, ClientException {
		return SocketAccess.doPrivilegedException(() -> {
			logger.info("try to copy: " + sourceBucketName + ":" + sourceKey + " to : " + destinationBucketName + " : "
					+ destinationKey);

			CopyObjectResult result = this.client.copyObject(sourceBucketName, sourceKey, destinationBucketName,
					destinationKey);
			logger.info(result);

			return result;
		});
	}

	public void shutdown() {
		this.client.shutdown();
	}
}