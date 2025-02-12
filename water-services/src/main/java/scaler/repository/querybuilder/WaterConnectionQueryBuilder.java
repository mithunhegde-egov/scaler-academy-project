package scaler.repository.querybuilder;

import scaler.web.models.WaterConnectionSearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class WaterConnectionQueryBuilder {

    private static final String BASE_WC_QUERY = " SELECT wc.id as id, wc.tenantid as tenantid, wc.applicationnumber as applicationnumber, wc.applicationstatus as applicationstatus, " +
            "wc.connectionno as connectionno, wc.connectiontype as connectiontype, wc.propertyid as propertyid, wc.channel as channel, " +
            "wc.connectionexecutiondate as connectionexecutiondate, wc.status as status, wc.createdby as createdby, wc.lastmodifiedby as lastmodifiedby, " +
            "wc.createdtime as createdtime, wc.lastmodifiedtime as lastmodifiedtime ";

    private static final String FROM_TABLES = " FROM eg_wc_connection wc ";

    private final String ORDERBY_CREATEDTIME = " ORDER BY wc.createdtime DESC ";

    public String getWaterConnectionSearchQuery(WaterConnectionSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_WC_QUERY);
        query.append(FROM_TABLES);

        if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" wc.tenantid = ? ");
            preparedStmtList.add(criteria.getTenantId());
        }

        if (!CollectionUtils.isEmpty(criteria.getIds())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" wc.id IN ( ").append(createQuery(criteria.getIds())).append(" ) ");
            addToPreparedStatement(preparedStmtList, criteria.getIds());
        }

        // Order by created time in descending order
        query.append(ORDERBY_CREATEDTIME);

        return query.toString();
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }

    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(preparedStmtList::add);
    }
}
