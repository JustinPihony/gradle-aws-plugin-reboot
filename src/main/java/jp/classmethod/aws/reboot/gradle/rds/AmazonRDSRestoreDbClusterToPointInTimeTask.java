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
package jp.classmethod.aws.reboot.gradle.rds;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import org.gradle.api.GradleException;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.model.DBCluster;
import com.amazonaws.services.rds.model.RestoreDBClusterToPointInTimeRequest;
import com.amazonaws.services.rds.model.Tag;

public class AmazonRDSRestoreDbClusterToPointInTimeTask extends ConventionTask { // NOPMD
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String dbClusterIdentifier;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String sourceDBClusterIdentifier;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String dbSubnetGroupName;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String kmsKeyId;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String optionGroupName;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private int port;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private Date restoreToTime;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String restoreType;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private Boolean useLatestRestorableTime;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private Map<String, String> tags;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private List<String> vpcSecurityGroupIds;
	
	@Getter(onMethod = @__(@Internal))
	private DBCluster dbCluster;
	
	
	public AmazonRDSRestoreDbClusterToPointInTimeTask() {
		setDescription("Restore DbCluster To Point In Time.");
		setGroup("AWS");
	}
	
	@TaskAction
	public void restoreDbClusterToPointInTime() { // NOPMD
		// to enable conventionMappings feature
		String dbClusterIdentifier = getDbClusterIdentifier();
		
		if (dbClusterIdentifier == null) {
			throw new GradleException("dbClusterIdentifier is not specified");
		}
		
		AmazonRDSPluginExtension ext = getProject().getExtensions().getByType(AmazonRDSPluginExtension.class);
		AmazonRDS rds = ext.getClient();
		
		RestoreDBClusterToPointInTimeRequest request = new RestoreDBClusterToPointInTimeRequest()
			.withDBClusterIdentifier(dbClusterIdentifier)
			.withDBSubnetGroupName(getDbSubnetGroupName())
			.withKmsKeyId(getKmsKeyId())
			.withRestoreType(getRestoreType())
			.withRestoreToTime(getRestoreToTime())
			.withSourceDBClusterIdentifier(getSourceDBClusterIdentifier())
			.withUseLatestRestorableTime(getUseLatestRestorableTime())
			.withVpcSecurityGroupIds(getVpcSecurityGroupIds())
			.withTags(getTags().entrySet().stream()
				.map(it -> new Tag()
					.withKey(it.getKey().toString())
					.withValue(it.getValue().toString()))
				.collect(Collectors.toList()));
		if (getPort() != 0) {
			request.setPort(getPort());
		}
		dbCluster = rds.restoreDBClusterToPointInTime(request);
		getLogger().info("Restored an RDS cluster to point in time: {}", dbCluster.getDBClusterIdentifier());
		
	}
}
