package com.lotp.server.repository;

import com.lotp.server.entity.Match;
import com.lotp.server.entity.PasswordReset;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * User: mruno
 * Date: 5/3/14
 * Time: 8:33 PM
 */
public interface PasswordResetRepository extends PagingAndSortingRepository<PasswordReset, Long> {
}
