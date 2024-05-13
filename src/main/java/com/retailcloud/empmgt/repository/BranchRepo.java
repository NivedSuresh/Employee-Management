package com.retailcloud.empmgt.repository;

import com.retailcloud.empmgt.model.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BranchRepo extends JpaRepository<Branch, Long> {

    Branch findByZipcode(final String zipcode);

    boolean existsByZipcodeOrEmailOrPhoneNumber(final String zipcode, final String email, final String phoneNumber);

}
