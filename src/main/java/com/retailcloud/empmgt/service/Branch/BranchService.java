package com.retailcloud.empmgt.service.Branch;

import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.payload.NewBranch;

public interface BranchService {

    Branch addBranch(NewBranch newBranch);

}
