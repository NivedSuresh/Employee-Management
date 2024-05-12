package com.retailcloud.empmgt.utils.validation;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;

public class PayloadValidator {

    public static void setCustomEditorForWebBinder(WebDataBinder binder) {
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, ste);
    }

    public static String fetchFirstError(BindingResult bindingResult) {
        for (FieldError error : bindingResult.getFieldErrors()) {
            return error.getDefaultMessage();
        }
        return "Validation error occurred";
    }

}
