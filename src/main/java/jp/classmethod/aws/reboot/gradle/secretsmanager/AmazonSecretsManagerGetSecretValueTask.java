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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import lombok.Getter;
import lombok.Setter;

import org.gradle.api.GradleException;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;

public class AmazonSecretsManagerGetSecretValueTask extends ConventionTask {
	
	@Getter(onMethod = @__(@OutputFile))
	@Setter
	private File destination;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String secretName;
	
	
	public AmazonSecretsManagerGetSecretValueTask() {
		setDescription("Retrieve secret value into a file");
		setGroup("AWS");
	}
	
	@TaskAction
	public void retrieveSecretValue() {
		getLogger().trace("Retrieving secrets from {} into {}", secretName, destination);
		
		File destination = getDestination();
		String secretName = getSecretName();
		
		if (secretName == null) {
			throw new GradleException("Must specify secret name");
		}
		if (destination == null) {
			throw new GradleException("Must provide the destination file");
		}
		
		AmazonSecretsManagerPluginExtension ext = getProject()
			.getExtensions()
			.getByType(AmazonSecretsManagerPluginExtension.class);
		
		AWSSecretsManager sm = ext.getClient();
		
		GetSecretValueRequest request = new GetSecretValueRequest().withSecretId(secretName);
		
		GetSecretValueResult result = sm.getSecretValue(request);
		
		try {
			Files.write(destination.toPath(), result.getSecretString().getBytes(StandardCharsets.UTF_8));
			getLogger().info("Secrets from {} has been written into {}", secretName, destination);
		} catch (IOException e) {
			getLogger().error("Exception writing the secrets file", e);
		}
	}
	
}
