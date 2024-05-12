package com.retailcloud.empmgt.config.RolesConfig;


import com.retailcloud.empmgt.model.entity.enums.Role;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;


@ConfigurationProperties(prefix = "company")
public record CompanyRoles(
        Set<Role> roles,
        Set<Role> anyAccessAuthority
) {
}
