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

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.gradle.api.GradleException;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.ModifyDBInstanceRequest;

public class AmazonRDSMigrateDBInstanceTask extends ConventionTask {
	
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
	private DBInstance dbInstance;
	
	@Getter(onMethod = @__(@Input))
	@Setter
	private List<String> stableStatuses = Arrays.asList(
			"available", "backing-up", "storage-full");
	
	
	public AmazonRDSMigrateDBInstanceTask() {
		setDescription("Create / Migrate RDS instance.");
		setGroup("AWS");
	}
	
	@TaskAction
	public void createOrUpdateDBInstance() throws InterruptedException {
		AmazonRDSPluginExtension ext = getProject().getExtensions().getByType(AmazonRDSPluginExtension.class);
		AmazonRDS rds = ext.getClient();
		
		try {
			DescribeDBInstancesResult describeDBInstancesResult =
					rds.describeDBInstances(new DescribeDBInstancesRequest()
						.withDBInstanceIdentifier(dbInstanceIdentifier));
			DBInstance dbInstance = describeDBInstancesResult.getDBInstances().get(0);
			if (stableStatuses.contains(dbInstance.getDBInstanceStatus())) {
				modifyDBInstance(rds);
			} else {
				throw new GradleException("Invalid status for update: " + dbInstance.getDBInstanceStatus());
			}
		} catch (DBInstanceNotFoundException e) {
			getLogger().info(e.getMessage());
			createDBInstance(rds);
		}
	}
	
	private void createDBInstance(AmazonRDS rds) {
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
		
		CreateDBInstanceRequest request = new CreateDBInstanceRequest()
			.withDBName(getDbName())
			.withDBInstanceIdentifier(dbInstanceIdentifier)
			.withAllocatedStorage(getAllocatedStorage())
			.withDBInstanceClass(dbInstanceClass)
			.withEngine(engine)
			.withMasterUsername(getMasterUsername())
			.withMasterUserPassword(getMasterUserPassword())
			.withVpcSecurityGroupIds(getVpcSecurityGroupIds())
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
			.withKmsKeyId(getKmsKeyId());
		dbInstance = rds.createDBInstance(request);
		getLogger().info("Create RDS instance requested: {}", dbInstance.getDBInstanceIdentifier());
	}
	
	private void modifyDBInstance(AmazonRDS rds) {
		// to enable conventionMappings feature
		String dbInstanceIdentifier = getDbInstanceIdentifier();
		
		if (dbInstanceIdentifier == null) {
			throw new GradleException("dbInstanceIdentifier is required");
		}
		
		ModifyDBInstanceRequest request = new ModifyDBInstanceRequest()
			.withDBInstanceIdentifier(dbInstanceIdentifier)
			.withAllocatedStorage(getAllocatedStorage())
			.withDBInstanceClass(getDbInstanceClass())
			.withMasterUserPassword(getMasterUserPassword())
			.withVpcSecurityGroupIds(getVpcSecurityGroupIds())
			.withPreferredMaintenanceWindow(getPreferredMaintenanceWindow())
			.withDBParameterGroupName(getDbParameterGroupName())
			.withBackupRetentionPeriod(getBackupRetentionPeriod())
			.withPreferredBackupWindow(getPreferredBackupWindow())
			.withMultiAZ(getMultiAZ())
			.withEngineVersion(getEngineVersion())
			.withAutoMinorVersionUpgrade(getAutoMinorVersionUpgrade())
			.withIops(getIops())
			.withOptionGroupName(getOptionGroupName())
			.withStorageType(getStorageType())
			.withTdeCredentialArn(getTdeCredentialArn())
			.withTdeCredentialPassword(getTdeCredentialPassword());
		dbInstance = rds.modifyDBInstance(request);
		getLogger().info("Modify RDS instance requested: {}", dbInstance.getDBInstanceIdentifier());
	}
}
