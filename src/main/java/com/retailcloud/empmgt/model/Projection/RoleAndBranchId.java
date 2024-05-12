package com.retailcloud.empmgt.model.Projection;

import com.retailcloud.empmgt.model.entity.enums.Role;

public record RoleAndBranchId(
        Role role,
        Long branchId
)
{}
