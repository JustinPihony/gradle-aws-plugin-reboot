/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.classmethod.aws.reboot.gradle.secretsmanager;

import org.gradle.api.Project;

import com.amazonaws.services.secretsmanager.AWSSecretsManagerClient;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;

import jp.classmethod.aws.reboot.gradle.AwsPluginExtension;
import jp.classmethod.aws.reboot.gradle.common.BaseRegionAwarePluginExtension;

public class AmazonSecretsManagerPluginExtension extends BaseRegionAwarePluginExtension<AWSSecretsManagerClient> {
	
	public static final String NAME = "secretsmanager";
	
	
	public AmazonSecretsManagerPluginExtension(Project project) {
		super(project, AWSSecretsManagerClient.class);
	}
	
	@Override
	protected AWSSecretsManagerClient initClient() {
		AWSSecretsManagerClientBuilder builder = AWSSecretsManagerClient.builder();
		
		AwsPluginExtension aws = getProject().getExtensions().getByType(AwsPluginExtension.class);
		String profile = aws.getProfileName() == null ? System.getenv("AWS_PROFILE") : aws.getProfileName();
		
		getProject().getLogger().info("Using profile {} for authorization", profile);
		
		builder.withCredentials(aws.newCredentialsProvider(profile));
		
		if (getRegion() != null) {
			getProject().getLogger().info("Using region {} from the Secrets Manager extension", getRegion());
			builder.withRegion(getRegion());
		} else if (aws.getRegion() != null) {
			getProject().getLogger().info("Using region {} from the AWS extension", getRegion());
			builder.withRegion(aws.getRegion());
		}
		
		return (AWSSecretsManagerClient) builder.build();
	}
}
