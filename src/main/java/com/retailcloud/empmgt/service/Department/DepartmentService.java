package com.retailcloud.empmgt.service.Department;


import com.retailcloud.empmgt.model.entity.Department;
import com.retailcloud.empmgt.model.entity.Employee;
import com.retailcloud.empmgt.model.payload.DeptHeadUpdate;
import com.retailcloud.empmgt.model.payload.NewDepartment;

/*
 * TODO: An interface might not be required as there wont be another impl for this service.
 *       Make it clear on review.
 * */
public interface DepartmentService {
    Department addDepartment(NewDepartment newDepartment, Long employeeId);

    Department assignNewHeadForDept(DeptHeadUpdate update);

    Department assignNewHeadForDept(Department department, Employee employee);
}
