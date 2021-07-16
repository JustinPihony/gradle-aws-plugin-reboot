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
package jp.classmethod.aws.reboot.gradle.common;

import javax.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;

import org.gradle.api.Project;
import org.gradle.api.tasks.Input;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.ClientConfiguration;

import jp.classmethod.aws.reboot.gradle.AwsPluginExtension;

public abstract class BasePluginExtension<T extends AmazonWebServiceClient> {
	
	private final Class<T> awsClientClass;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Project project;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String profileName;
	
	@Getter(lazy = true, onMethod = @__({
		@SuppressWarnings("unchecked"),
		@Input
	}))
	private final T client = initClient();
	
	
	public BasePluginExtension(Project project, Class<T> awsClientClass) {
		this.project = project;
		this.awsClientClass = awsClientClass;
	}
	
	protected T initClient() {
		AwsPluginExtension aws = project.getExtensions().getByType(AwsPluginExtension.class);
		return aws.createClient(awsClientClass, profileName, buildClientConfiguration());
	}
	
	/**
	 * Allow subclasses to build a custom client configuration.
	 *
	 * @return  AWS ClientConfiguration
	 */
	@Nullable
	protected ClientConfiguration buildClientConfiguration() { // NOPMD
		return null;
	}
	
}
