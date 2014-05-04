package com.lotp.server.repository;

import com.lotp.server.entity.LeaderboardItem;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * User: mruno
 * Date: 5/3/14
 * Time: 1:42 PM
 */
public interface LeaderboardItemRepository extends PagingAndSortingRepository<LeaderboardItem, Long> {
    LeaderboardItem findOneByPlayerId(long playerId);
}
