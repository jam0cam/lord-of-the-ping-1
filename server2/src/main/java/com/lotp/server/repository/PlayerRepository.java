package com.lotp.server.repository;

import com.lotp.server.entity.Player;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * User: mruno
 * Date: 5/3/14
 * Time: 12:06 PM
 */
public interface PlayerRepository extends PagingAndSortingRepository<Player, Long> {

    Player findByEmail(String email);

    Player findByEmailAndPassword(String email, String password);

    Player findByPasswordResetResetHash(String resetHash);

}
