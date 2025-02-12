package scaler.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.models.AuditDetails;
import org.egov.common.contract.models.Document;
import org.egov.common.contract.models.Workflow;
import org.egov.common.contract.request.User;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * WaterConnection
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2025-01-07T15:17:21.586197+05:30[Asia/Kolkata]")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WaterConnection {
    @JsonProperty("id")

    @Size(min = 1, max = 64)
    private String id = null;

    @JsonProperty("tenantId")
    @NotNull

    @Size(min = 2, max = 64)
    private String tenantId = null;

    @JsonProperty("applicationNo")

    @Size(min = 1, max = 64)
    private String applicationNo = null;

    @JsonProperty("applicationStatus")

    private String applicationStatus = null;

    @JsonProperty("auditDetails")

    @Valid
    private AuditDetails auditDetails = null;

    @JsonProperty("channel")

    private String channel = null;

    @JsonProperty("connectionExecutionDate")

    private Long connectionExecutionDate = null;

    @JsonProperty("connectionHolders")

    private List<User> connectionHolders = null;

    @JsonProperty("connectionNo")

    @Size(min = 1, max = 64)
    private String connectionNo = null;

    @JsonProperty("connectionType")
    @NotNull

    @Size(min = 2, max = 32)
    private String connectionType = null;

    @JsonProperty("workflow")

    @Valid
    private Workflow workflow = null;

    @JsonProperty("propertyId")

    private String propertyId = null;

    @JsonProperty("status")

    private Boolean status = null;

    @JsonProperty("additionalDetails")

    private Object additionalDetails = null;

    @JsonProperty("documents")
    @Valid
    private List<Document> documents = null;

    @JsonProperty("")


    public WaterConnection addConnectionHoldersItem(User connectionHoldersItem) {
        if (this.connectionHolders == null) {
            this.connectionHolders = new ArrayList<>();
        }
        this.connectionHolders.add(connectionHoldersItem);
        return this;
    }

    public WaterConnection addDocumentsItem(Document documentsItem) {
        if (this.documents == null) {
            this.documents = new ArrayList<>();
        }
        this.documents.add(documentsItem);
        return this;
    }

}
