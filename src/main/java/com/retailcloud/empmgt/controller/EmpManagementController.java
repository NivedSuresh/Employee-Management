package com.retailcloud.empmgt.controller;


import com.retailcloud.empmgt.utils.validation.PayloadValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;


/**
 * Context Path = /api/management
 * <p></p>
 * Management endpoint's should be restricted from user's with out a management role.
 * ie devs, ops etc
 */
@RequiredArgsConstructor
@RestController
public class EmpManagementController {


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


}
