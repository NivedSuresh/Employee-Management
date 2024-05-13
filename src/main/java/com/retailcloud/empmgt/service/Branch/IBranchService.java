package com.retailcloud.empmgt.service.Branch;

import com.retailcloud.empmgt.advice.exception.BranchAlreadyExistsException;
import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.payload.NewBranch;
import com.retailcloud.empmgt.repository.BranchRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



/**
 * Adding or updating branch information should only be allowed for
 * Roles with chief access, endpoint access should be restricted
 * from gateway/auth server using claims for better performance.
 **/
@Service
@RequiredArgsConstructor
class IBranchService implements BranchService {

    private final BranchRepo branchRepo;


    @Override
    public Branch addBranch(NewBranch newBranch) {

        if(this.branchRepo.existsByZipcodeOrEmailOrPhoneNumber(newBranch.zipcode(), newBranch.email(), newBranch.phoneNumber())){
            throw new BranchAlreadyExistsException();
        }

        final Branch branch = Branch.builder()
                .buildingName(newBranch.buildingName())
                .street(newBranch.street())
                .city(newBranch.city())
                .state(newBranch.state())
                .country(newBranch.country())
                .zipcode(newBranch.zipcode())
                .email(newBranch.email())
                .phoneNumber(newBranch.phoneNumber())
                .build();

        return this.branchRepo.save(branch);
    }
}
