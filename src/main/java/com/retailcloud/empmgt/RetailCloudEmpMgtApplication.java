package com.retailcloud.empmgt;

import com.retailcloud.empmgt.config.RolesConfig.CompanyRoles;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = CompanyRoles.class)
public class RetailCloudEmpMgtApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetailCloudEmpMgtApplication.class, args);
    }

}
