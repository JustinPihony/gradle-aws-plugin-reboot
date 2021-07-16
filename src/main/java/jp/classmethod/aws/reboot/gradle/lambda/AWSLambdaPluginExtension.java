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
package jp.classmethod.aws.reboot.gradle.lambda;

import lombok.Getter;
import lombok.Setter;

import org.gradle.api.Project;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.lambda.AWSLambdaClient;

import jp.classmethod.aws.reboot.gradle.common.BaseRegionAwarePluginExtension;
import org.gradle.api.tasks.Input;

public class AWSLambdaPluginExtension extends BaseRegionAwarePluginExtension<AWSLambdaClient> {
	
	public static final String NAME = "lambda";
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Integer maxErrorRetry = -1;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Integer requestTimeout = -1;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Integer clientExecutionTimeout = -1;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Integer connectionTimeout = -1;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Integer socketTimeout = -1;
	
	
	public AWSLambdaPluginExtension(Project project) {
		super(project, AWSLambdaClient.class);
	}
	
	@Override
	protected ClientConfiguration buildClientConfiguration() {
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		if (maxErrorRetry > 0) {
			clientConfiguration.setMaxErrorRetry(maxErrorRetry);
		}
		
		if (requestTimeout > 0) {
			clientConfiguration.setRequestTimeout(requestTimeout);
		}
		
		if (clientExecutionTimeout > 0) {
			clientConfiguration.setClientExecutionTimeout(clientExecutionTimeout);
		}
		
		if (socketTimeout > 0) {
			clientConfiguration.setSocketTimeout(socketTimeout);
		}
		
		if (connectionTimeout > 0) {
			clientConfiguration.setConnectionTimeout(connectionTimeout);
		}
		
		return clientConfiguration;
	}
}
