package com.lotp.server.repository;

import com.lotp.server.entity.Match;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: mruno
 * Date: 5/3/14
 * Time: 1:03 PM
 */
public interface MatchRepository extends PagingAndSortingRepository<Match, Long> {
    List<Match> findByPlayerOneIdOrPlayerTwoId(long playerOneId, long playerTwoId);
}
