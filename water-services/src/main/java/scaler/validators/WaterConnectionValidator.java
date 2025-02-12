package scaler.validators;

import scaler.web.models.WaterConnection;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import scaler.web.models.WaterConnectionCreateRequest;


@Component
public class WaterConnectionValidator {


    /**
     * Validates the WaterConnectionRequest to ensure mandatory fields are present.
     *
     * @param waterConnectionRequest The water connection request to validate.
     */
    public void validateWaterConnectionRequest(WaterConnectionCreateRequest waterConnectionRequest) {
            if (ObjectUtils.isEmpty(waterConnectionRequest.getWaterConnection().getTenantId())) {
                throw new CustomException("EG_WC_ERR", "tenantId is mandatory for creating water connection applications");
            }
        }
    }



