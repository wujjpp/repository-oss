package org.elasticsearch.repositories.oss;

import org.elasticsearch.cluster.metadata.RepositoryMetaData;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.blobstore.BlobPath;
import org.elasticsearch.common.blobstore.BlobStore;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.repositories.blobstore.BlobStoreRepository;

import java.io.File;

public class OssRepository extends BlobStoreRepository {
	public static final String TYPE = "oss";
	private final OssBlobStore blobStore;
	private final BlobPath basePath;
	private final boolean compress;
	private final ByteSizeValue chunkSize;
	private final OssStorageService storageService;
	private final Environment environment;
	
	private String bucket;
	
	public OssRepository(RepositoryMetaData metadata, Environment env, NamedXContentRegistry namedXContentRegistry,
			OssStorageService ossStorageService) {
		super(metadata, env.settings(), namedXContentRegistry);
		
		this.storageService = ossStorageService;
		this.environment = env;
		
		bucket = OssStorageSettings.getSetting(OssStorageSettings.BUCKET, metadata);
		String basePath = OssStorageSettings.getSetting(OssStorageSettings.BASE_PATH, metadata, true);
		
		if (Strings.hasLength(basePath)) {
			BlobPath path = new BlobPath();
			for (String elem : basePath.split(File.separator)) {
				path = path.add(elem);
			}
			this.basePath = path;
		} else {
			this.basePath = BlobPath.cleanPath();
		}
		
		this.compress = OssStorageSettings.getSetting(OssStorageSettings.COMPRESS, metadata);
		
		this.chunkSize = OssStorageSettings.getSetting(OssStorageSettings.CHUNK_SIZE, metadata);
		
		logger.trace("using bucket [{}], base_path [{}], chunk_size [{}], compress [{}]", bucket, basePath, chunkSize,
				compress);
		
		blobStore = new OssBlobStore(env.settings(), bucket, ossStorageService);
	}
	
	@Override
    protected OssBlobStore createBlobStore() {
		final OssBlobStore blobStore = new OssBlobStore(environment.settings(), bucket, storageService);
	    return blobStore;
    }

	@Override
	protected BlobStore blobStore() {
		return this.blobStore;
	}

	@Override
	protected BlobPath basePath() {
		return this.basePath;
	}

	@Override
	protected boolean isCompress() {
		return compress;
	}

	@Override
	protected ByteSizeValue chunkSize() {
		return chunkSize;
	}
}
