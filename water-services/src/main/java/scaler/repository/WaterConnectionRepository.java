package scaler.repository;

import scaler.repository.querybuilder.WaterConnectionQueryBuilder;
import scaler.repository.rowmapper.WaterConnectionRowMapper;
import scaler.web.models.WaterConnection;
import scaler.web.models.WaterConnectionSearchCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class WaterConnectionRepository {

    @Autowired
    private WaterConnectionQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WaterConnectionRowMapper rowMapper;

    /**
     * Fetch water connections from the database based on the search criteria.
     *
     * @param searchCriteria The criteria for searching water connections.
     * @return A list of matching water connections.
     */
    public List<WaterConnection> getConnections(WaterConnectionSearchCriteria searchCriteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getWaterConnectionSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }
}
