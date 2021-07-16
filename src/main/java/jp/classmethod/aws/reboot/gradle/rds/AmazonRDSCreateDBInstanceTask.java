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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

import org.gradle.api.GradleException;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.Tag;

public class AmazonRDSCreateDBInstanceTask extends ConventionTask {
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String dbName;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String dbInstanceIdentifier;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Integer allocatedStorage;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String dbInstanceClass;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String engine;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String masterUsername;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String masterUserPassword;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private List<String> vpcSecurityGroupIds;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String dbSubnetGroupName;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String preferredMaintenanceWindow;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String dbParameterGroupName;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Integer backupRetentionPeriod;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String preferredBackupWindow;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Integer port;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Boolean multiAZ;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String engineVersion;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Boolean autoMinorVersionUpgrade;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String licenseModel;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Integer iops;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String optionGroupName;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Boolean publiclyAccessible;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String characterSetName;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String storageType;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String tdeCredentialArn;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String tdeCredentialPassword;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Boolean storageEncrypted;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String kmsKeyId;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Boolean copyTagsToSnapshot;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Integer promotionTier;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String dbClusterIdentifier;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private String availabilityZone;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private List<String> securityGroups;
	
	@Getter(onMethod = @__(@Input))
	private DBInstance dbInstance;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private Map<String, String> tags;
	
	
	public AmazonRDSCreateDBInstanceTask() {
		setDescription("Create RDS instance.");
		setGroup("AWS");
	}
	
	@TaskAction
	public void createDBInstance() {
		// to enable conventionMappings feature
		String dbInstanceIdentifier = getDbInstanceIdentifier();
		String dbInstanceClass = getDbInstanceClass();
		String engine = getEngine();
		
		if (dbInstanceClass == null) {
			throw new GradleException("dbInstanceClass is required");
		}
		if (dbInstanceIdentifier == null) {
			throw new GradleException("dbInstanceIdentifier is required");
		}
		if (engine == null) {
			throw new GradleException("engine is required");
		}
		
		AmazonRDSPluginExtension ext = getProject().getExtensions().getByType(AmazonRDSPluginExtension.class);
		AmazonRDS rds = ext.getClient();
		
		CreateDBInstanceRequest request = new CreateDBInstanceRequest()
			.withDBName(getDbName())
			.withDBInstanceIdentifier(dbInstanceIdentifier)
			.withAllocatedStorage(getAllocatedStorage())
			.withDBInstanceClass(dbInstanceClass)
			.withEngine(engine)
			.withMasterUsername(getMasterUsername())
			.withMasterUserPassword(getMasterUserPassword())
			.withVpcSecurityGroupIds(getVpcSecurityGroupIds())
			.withDBSecurityGroups(getSecurityGroups())
			.withDBSubnetGroupName(getDbSubnetGroupName())
			.withPreferredMaintenanceWindow(getPreferredMaintenanceWindow())
			.withDBParameterGroupName(getDbParameterGroupName())
			.withBackupRetentionPeriod(getBackupRetentionPeriod())
			.withPreferredBackupWindow(getPreferredBackupWindow())
			.withPort(getPort())
			.withMultiAZ(getMultiAZ())
			.withEngineVersion(getEngineVersion())
			.withAutoMinorVersionUpgrade(getAutoMinorVersionUpgrade())
			.withLicenseModel(getLicenseModel())
			.withIops(getIops())
			.withOptionGroupName(getOptionGroupName())
			.withPubliclyAccessible(getPubliclyAccessible())
			.withCharacterSetName(getCharacterSetName())
			.withStorageType(getStorageType())
			.withTdeCredentialArn(getTdeCredentialArn())
			.withTdeCredentialPassword(getTdeCredentialPassword())
			.withStorageEncrypted(getStorageEncrypted())
			.withKmsKeyId(getKmsKeyId())
			.withCopyTagsToSnapshot(getCopyTagsToSnapshot())
			.withPromotionTier(getPromotionTier())
			.withDBClusterIdentifier(getDbClusterIdentifier())
			.withAvailabilityZone(getAvailabilityZone())
			.withTags(getTags().entrySet().stream()
				.map(it -> new Tag()
					.withKey(it.getKey().toString())
					.withValue(it.getValue().toString()))
				.collect(Collectors.toList()));
		
		dbInstance = rds.createDBInstance(request);
		getLogger().info("Create RDS instance requested: {}", dbInstance.getDBInstanceIdentifier());
	}
}
