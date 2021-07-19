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

import org.gradle.api.GradleException;
import org.gradle.api.tasks.Internal;

public class S3File {
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String bucketName;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String key;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String objectVersion;
	
	
	protected S3File() {
		/*
		An empty constructor is needed so that gradle can resolve variable to an instance,
		eg to make this work as a nested task property
		 */
	}
	
	/**
	 * Validates that both bucketName and key are provided.
	 */
	public void validate() {
		boolean missingBucketName = isNullOrBlank(bucketName);
		boolean missingKey = isNullOrBlank(key);
		if (missingBucketName || missingKey) {
			throw new GradleException("bucketName and key are required for an S3File");
		}
	}
	
	private boolean isNullOrBlank(String str) {
		if (str == null) {
			return true;
		}
		String trimmedStr = str.trim();
		return trimmedStr.length() == 0;
	}
}
