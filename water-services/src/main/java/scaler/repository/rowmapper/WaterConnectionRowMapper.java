
package scaler.repository.rowmapper;

import scaler.web.models.*;
import org.egov.common.contract.models.AuditDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class WaterConnectionRowMapper implements ResultSetExtractor<List<WaterConnection>> {

    @Override
    public List<WaterConnection> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, WaterConnection> waterConnectionMap = new LinkedHashMap<>();

        while (rs.next()) {
            String id = rs.getString("id");
            WaterConnection waterConnection = waterConnectionMap.get(id);

            if (waterConnection == null) {

                Long lastModifiedTime = rs.getLong("lastModifiedTime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }

                AuditDetails auditDetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdBy"))
                        .createdTime(rs.getLong("createdTime"))
                        .lastModifiedBy(rs.getString("lastModifiedBy"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();

                waterConnection = WaterConnection.builder()
                        .id(rs.getString("id"))
                        .tenantId(rs.getString("tenantid"))
                        .applicationNo(rs.getString("applicationnumber"))
                        .applicationStatus(rs.getString("applicationstatus"))
                        .connectionNo(rs.getString("connectionno"))
                        .connectionType(rs.getString("connectiontype"))
                        .propertyId(rs.getString("propertyid"))
                        .channel(rs.getString("channel"))
                        .connectionExecutionDate(rs.getLong("connectionexecutiondate"))
                        .status(rs.getBoolean("status"))
                        .auditDetails(auditDetails)
                        .build();

                waterConnectionMap.put(id, waterConnection);
            }
        }

        return new ArrayList<>(waterConnectionMap.values());
    }
}
