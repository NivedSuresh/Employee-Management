package com.retailcloud.empmgt.controller;


import com.retailcloud.empmgt.model.entity.Branch;
import com.retailcloud.empmgt.model.payload.BranchDto;
import com.retailcloud.empmgt.model.payload.NewBranch;
import com.retailcloud.empmgt.service.Branch.BranchService;
import com.retailcloud.empmgt.utils.mapper.ModelMapper;
import com.retailcloud.empmgt.utils.validation.PayloadValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


/**
 * Context Path = /api/management
 * <p></p>
 * Management endpoint's should be restricted from user's with out a management role.
 * ie devs, ops etc
 */
@RequestMapping("/branch")
@RequiredArgsConstructor
@RestController
public class BranchController {

    private final BranchService branchService;

    /**
     * Trims white spaces from user input.
     * <p></p>
     * ie: If user input for the field 'username' only consists of white spaces
     * then it'd be trimmed down to an empty spring thus leading @{@link org.springframework.validation.BindException}.
     */
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        PayloadValidator.setCustomEditorForWebBinder(dataBinder);
    }


    @PostMapping
    public ResponseEntity<BranchDto> addBranch(@RequestBody final NewBranch newBranch){
        final Branch branch = this.branchService.addBranch(newBranch);
        return ResponseEntity.accepted().body(ModelMapper.toDto(branch));
    }


}
