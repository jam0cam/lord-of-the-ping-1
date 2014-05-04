package com.lotp.server.repository;

import com.lotp.server.command.MatchConfirmation;
import com.lotp.server.entity.PendingMatch;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: mruno
 * Date: 5/3/14
 * Time: 1:26 PM
 */
public interface PendingMatchRepository extends PagingAndSortingRepository<PendingMatch, Long> {

    PendingMatch findOneById(long pendingId);

    List<PendingMatch> findAllByPlayerTwoId(long playerId);
}
