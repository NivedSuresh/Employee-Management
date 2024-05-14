package com.retailcloud.empmgt.service.Department;


import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.payload.EmployeeDepartmentUpdate;
import com.retailcloud.empmgt.model.payload.NewDepartment;

/*
 * TODO: An interface might not be required as there wont be another impl for this service.
 *       Make it clear on review.
 * */
public interface DepartmentService {
    Department addDepartment(NewDepartment newDepartment, Long employeeId);

    Department assignNewHeadForDept(EmployeeDepartmentUpdate update, Long principalId);

    Department assignNewHeadForDept(Department department, Employee employee, boolean validatePermissions, Long principalId, boolean updateRmForTeamsLeads);

    void deleteDepartment(Long deptId, Long principalId);
}
