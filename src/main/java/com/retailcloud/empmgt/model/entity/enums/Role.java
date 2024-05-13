package com.retailcloud.empmgt.model.entity.enums;

import com.retailcloud.empmgt.advice.exception.RoleNotFoundException;

import java.util.Set;

public enum Role {

    TEAM_LEAD,
    JUNIOR_ASSISTANT,
    SENIOR_ASSISTANT,
    BRANCH_MANAGER,
    BRANCH_DEPARTMENT_HEAD,
    CHIEF_OPERATING_OFFICER,
    INITIAL_CHIEF_ACCESS,
    NOT_REQUIRED,
    UNDEFINED;


    public Role getReportingManagerRole(Role this){
        switch (this){
            case JUNIOR_ASSISTANT, SENIOR_ASSISTANT-> { return TEAM_LEAD; }
            case TEAM_LEAD -> { return BRANCH_DEPARTMENT_HEAD; }
            case BRANCH_DEPARTMENT_HEAD -> { return BRANCH_MANAGER; }
            case BRANCH_MANAGER -> { return CHIEF_OPERATING_OFFICER; }
            case CHIEF_OPERATING_OFFICER -> {return NOT_REQUIRED;}
            default -> { return UNDEFINED; }
        }
    }

    public Set<Role> getRolesAbove(Role this){
        switch (this){
            case JUNIOR_ASSISTANT, SENIOR_ASSISTANT -> { return Set.of(TEAM_LEAD, BRANCH_DEPARTMENT_HEAD, BRANCH_MANAGER, CHIEF_OPERATING_OFFICER, INITIAL_CHIEF_ACCESS); }
            case TEAM_LEAD -> {return Set.of(BRANCH_DEPARTMENT_HEAD, BRANCH_MANAGER, CHIEF_OPERATING_OFFICER, INITIAL_CHIEF_ACCESS); }
            case BRANCH_DEPARTMENT_HEAD -> { return Set.of(BRANCH_MANAGER, CHIEF_OPERATING_OFFICER, INITIAL_CHIEF_ACCESS); }
            case BRANCH_MANAGER -> {return Set.of(CHIEF_OPERATING_OFFICER, INITIAL_CHIEF_ACCESS); }
            case CHIEF_OPERATING_OFFICER -> { return Set.of(INITIAL_CHIEF_ACCESS); }
            case INITIAL_CHIEF_ACCESS -> { return Set.of(); }
            default -> { throw new RoleNotFoundException(); }
        }
    }



}
