package scaler.enrichment;

import scaler.util.IdgenUtil;
import scaler.web.models.WaterConnection;
import scaler.web.models.WaterConnectionCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.models.AuditDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class WaterConnectionEnrichment {

    @Autowired
    private IdgenUtil idgenUtil;

    /**
     * Enriches the WaterConnection object during creation.
     *
     * @param waterConnectionRequest The request object containing the water connection details.
     */
    public void enrichWaterConnection(WaterConnectionCreateRequest waterConnectionRequest) {
        WaterConnection waterConnection = waterConnectionRequest.getWaterConnection();

        // Enrich audit details
        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(waterConnectionRequest.getRequestInfo().getUserInfo().getUuid())
                .createdTime(System.currentTimeMillis())
                .lastModifiedBy(waterConnectionRequest.getRequestInfo().getUserInfo().getUuid())
                .lastModifiedTime(System.currentTimeMillis())
                .build();
        waterConnection.setAuditDetails(auditDetails);

        // Enrich unique ID for the water connection
        waterConnection.setId(UUID.randomUUID().toString());

        // Generate unique connection ID
       // String connectionId = idgenUtil.getIdList(waterConnectionRequest.getRequestInfo(), waterConnectionRequest.getWaterConnection().getTenantId(), "btr.registrationid", "", 1).get(0);
        //waterConnection.setConnectionNo(connectionId);
        log.info("Enriched WaterConnectionRequest: {}", waterConnectionRequest);
    }
}
