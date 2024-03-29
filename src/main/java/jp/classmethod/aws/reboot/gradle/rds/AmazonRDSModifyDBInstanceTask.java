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

import lombok.Getter;
import lombok.Setter;

import org.gradle.api.GradleException;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.ModifyDBInstanceRequest;

public class AmazonRDSModifyDBInstanceTask extends ConventionTask {
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String dbInstanceIdentifier;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private Integer allocatedStorage;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String dbInstanceClass;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String masterUserPassword;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private List<String> vpcSecurityGroupIds;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String preferredMaintenanceWindow;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String dbParameterGroupName;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private Integer backupRetentionPeriod;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String preferredBackupWindow;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private Boolean multiAZ;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String engineVersion;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private Boolean autoMinorVersionUpgrade;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private Integer iops;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String optionGroupName;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String storageType;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String tdeCredentialArn;
	
	@Getter(onMethod = @__(@Internal))
	@Setter
	private String tdeCredentialPassword;
	
	@Getter(onMethod = @__(@Internal))
	private DBInstance dbInstance;
	
	
	public AmazonRDSModifyDBInstanceTask() {
		setDescription("Modify RDS instance.");
		setGroup("AWS");
	}
	
	@TaskAction
	public void modifyDBInstance() {
		// to enable conventionMappings feature
		String dbInstanceIdentifier = getDbInstanceIdentifier();
		
		if (dbInstanceIdentifier == null) {
			throw new GradleException("dbInstanceIdentifier is required");
		}
		
		AmazonRDSPluginExtension ext = getProject().getExtensions().getByType(AmazonRDSPluginExtension.class);
		AmazonRDS rds = ext.getClient();
		
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
