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
package jp.classmethod.aws.gradle.lambda;

import java.io.FileNotFoundException;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

import org.gradle.api.GradleException;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.DeleteFunctionRequest;

public class AWSLambdaDeleteFunctionTask extends ConventionTask {
	
	@Getter
	@Setter
	private String functionName;
	
	
	public AWSLambdaDeleteFunctionTask() {
		setDescription("Delete Lambda function.");
		setGroup("AWS");
	}
	
	@TaskAction
	public void deleteFunction() throws FileNotFoundException, IOException {
		// to enable conventionMappings feature
		String functionName = getFunctionName();
		
		if (functionName == null) {
			throw new GradleException("functionName is required");
		}
		
		AWSLambdaPluginExtension ext = getProject().getExtensions().getByType(AWSLambdaPluginExtension.class);
		AWSLambda lambda = ext.getClient();
		
		DeleteFunctionRequest request = new DeleteFunctionRequest()
			.withFunctionName(functionName);
		lambda.deleteFunction(request);
		getLogger().info("Delete Lambda function requested: {}", functionName);
	}
}
