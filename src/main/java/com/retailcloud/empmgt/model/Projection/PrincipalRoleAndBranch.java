package com.retailcloud.empmgt.model.Projection;

import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.entity.enums.Role;

/**
 * Projection for {@link Employee}
 */
public interface PrincipalRoleAndBranch {
    Role getRole();

    BranchInfo getBranch();
}