package com.retailcloud.empmgt.service;


import com.retailcloud.empmgt.advice.exception.OutOfScopeException;
import com.retailcloud.empmgt.config.RolesConfig.CompanyRoles;
import com.retailcloud.empmgt.model.Projection.PrincipalRoleAndBranch;
import com.retailcloud.empmgt.model.entity.Department;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final FetchService fetchService;
    private final CompanyRoles companyRoles;

    /** Apart from COO, Managers can also update departments But you have to make sure
    *  that managers are not updating departments that is out of their scope */
    public void validateUserPermissionsForDepartment(final Long principalId, Department department){
        /* This fetch here can be avoided if this information is part of the user claims after authorization */
        PrincipalRoleAndBranch roleAndBranch = this.fetchService.findRoleAndBranchIdElseThrow(principalId);
        if(!companyRoles.anyAccessAuthority().contains(roleAndBranch.getRole()) && !Objects.equals(roleAndBranch.getBranch().getBranchId(), department.getBranch().getBranchId())){
            throw new OutOfScopeException("Department cannot be updated as the user doesn't have necessary permissions to perform updates on this branch!");
        }
    }

    public void validateUserPermissionsForDepartment(Department department, PrincipalRoleAndBranch roleAndBranch){
        if(!companyRoles.anyAccessAuthority().contains(roleAndBranch.getRole()) && !Objects.equals(roleAndBranch.getBranch().getBranchId(), department.getBranch().getBranchId())){
            throw new OutOfScopeException("Department cannot be updated as the user doesn't have necessary permissions to perform updates on this branch!");
        }
    }


}
