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
package jp.classmethod.aws.gradle.cloudformation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.gradle.api.GradleException;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.model.AmazonCloudFormationException;
import com.amazonaws.services.cloudformation.model.Capability;
import com.amazonaws.services.cloudformation.model.ChangeSetType;
import com.amazonaws.services.cloudformation.model.CreateChangeSetRequest;
import com.amazonaws.services.cloudformation.model.CreateChangeSetResult;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.Parameter;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudformation.model.Tag;
import com.google.common.base.Strings;

public class AmazonCloudFormationCreateChangeSetTask extends ConventionTask {
	
	@Getter
	@Setter
	private String stackName;
	
	@Getter
	@Setter
	private String cfnTemplateUrl;
	
	@Getter
	@Setter
	private File cfnTemplateFile;
	
	@Getter
	@Setter
	private List<Parameter> cfnStackParams = new ArrayList<>();
	
	@Getter
	@Setter
	private List<Tag> cfnStackTags = new ArrayList<>();
	
	@Getter
	@Setter
	private String cfnRoleArn;
	
	@Getter
	@Setter
	private boolean capabilityIam;
	
	@Getter
	@Setter
	private Capability useCapabilityIam;
	
	@Getter
	@Setter
	private List<String> stableStatuses = Arrays.asList(
			"CREATE_COMPLETE", "ROLLBACK_COMPLETE", "UPDATE_COMPLETE", "UPDATE_ROLLBACK_COMPLETE",
			"REVIEW_IN_PROGRESS");
	
	
	public AmazonCloudFormationCreateChangeSetTask() {
		setDescription("Create cfn change set.");
		setGroup("AWS");
	}
	
	@TaskAction
	public void createChangeSet() throws InterruptedException, IOException {
		// to enable conventionMappings feature
		String stackName = getStackName();
		List<String> stableStatuses = getStableStatuses();
		
		if (stackName == null) {
			throw new GradleException("stackName is not specified");
		}
		
		AmazonCloudFormationPluginExtension ext =
				getProject().getExtensions().getByType(AmazonCloudFormationPluginExtension.class);
		AmazonCloudFormation cfn = ext.getClient();
		
		try {
			DescribeStacksResult describeStackResult =
					cfn.describeStacks(new DescribeStacksRequest().withStackName(stackName));
			Stack stack = describeStackResult.getStacks().get(0);
			if (stableStatuses.contains(stack.getStackStatus())) {
				createChangeSet(cfn, ChangeSetType.UPDATE);
			} else {
				throw new GradleException("invalid status for create change set: " + stack.getStackStatus());
			}
		} catch (AmazonCloudFormationException e) {
			if (e.getMessage().contains("does not exist")) {
				// stack does not exist; create change set in CREATE mode
				createChangeSet(cfn, ChangeSetType.CREATE);
			} else {
				throw new GradleException("Failed to describe stack " + stackName, e);
			}
		}
	}
	
	private void createChangeSet(AmazonCloudFormation cfn, ChangeSetType changeSetType) throws IOException {
		// to enable conventionMappings feature
		String stackName = getStackName();
		String cfnTemplateUrl = getCfnTemplateUrl();
		List<Parameter> cfnStackParams = getCfnStackParams();
		List<Tag> cfnStackTags = getCfnStackTags();
		String cfnRoleArn = getCfnRoleArn();
		File cfnTemplateFile = getCfnTemplateFile();
		
		String changeSetName = changeSetName(stackName);
		getLogger().info("Create change set '{}' for stack '{}'", changeSetName, stackName);
		CreateChangeSetRequest req = new CreateChangeSetRequest()
			.withChangeSetName(changeSetName)
			.withStackName(stackName)
			.withParameters(cfnStackParams)
			.withTags(cfnStackTags)
			.withRoleARN(cfnRoleArn)
			.withChangeSetType(changeSetType);
		
		// If template URL is specified, then use it
		if (Strings.isNullOrEmpty(cfnTemplateUrl) == false) {
			req.setTemplateURL(cfnTemplateUrl);
			// Else, use the template file body
		} else {
			req.setTemplateBody(FileUtils.readFileToString(cfnTemplateFile));
		}
		
		if (isCapabilityIam()) {
			Capability selectedCapability =
					(getUseCapabilityIam() == null) ? Capability.CAPABILITY_IAM : getUseCapabilityIam();
			getLogger().info("Using IAM capability: " + selectedCapability);
			req.setCapabilities(Arrays.asList(selectedCapability.toString()));
		}
		CreateChangeSetResult createChangeSetResult = cfn.createChangeSet(req);
		getLogger().info("Create change set requested: {}", createChangeSetResult.getId());
	}
	
	private String changeSetName(String stackName) {
		return stackName + new SimpleDateFormat("-yyyyMMdd-HHmmss", Locale.ENGLISH).format(new Date());
	}
}
