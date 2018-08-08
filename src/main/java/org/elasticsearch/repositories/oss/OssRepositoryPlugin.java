package org.elasticsearch.repositories.oss;

import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.RepositoryPlugin;
import org.elasticsearch.repositories.Repository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OssRepositoryPlugin extends Plugin implements RepositoryPlugin {

	@Override
	public Map<String, Repository.Factory> getRepositories(Environment env, NamedXContentRegistry namedXContentRegistry) {
		return Collections.singletonMap(OssRepository.TYPE, 
				(metadata) -> new OssRepository(metadata, env, namedXContentRegistry, new OssStorageService(env.settings(), metadata)));
	}

	@Override
	public List<Setting<?>> getSettings() {
		return Arrays.asList(
				OssStorageSettings.ACCESS_KEY_ID, 
				OssStorageSettings.SECRET_ACCESS_KEY,
				OssStorageSettings.ENDPOINT, 
				OssStorageSettings.BUCKET, 
				OssStorageSettings.SECURITY_TOKEN,
				OssStorageSettings.BASE_PATH, 
				OssStorageSettings.COMPRESS, 
				OssStorageSettings.CHUNK_SIZE
		);

	}
}