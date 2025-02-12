package scaler.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import scaler.service.WaterConnectionService;
import scaler.util.ResponseInfoFactory;
import scaler.web.models.WaterConnection;
import scaler.web.models.WaterConnectionCreateRequest;
import scaler.web.models.WaterConnectionResponse;
import scaler.web.models.WaterConnectionSearchRequest;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/connection")
public class WaterServicesApiController {

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private final WaterConnectionService waterConnectionService;
    private final ResponseInfoFactory responseInfoFactory;

    @Autowired
    public WaterServicesApiController(
            ObjectMapper objectMapper,
            HttpServletRequest request,
            WaterConnectionService waterConnectionService,
            ResponseInfoFactory responseInfoFactory
    ) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.waterConnectionService = waterConnectionService;
        this.responseInfoFactory = responseInfoFactory;
    }

    /**
     * API to create a new water connection.
     *
     * @param body The request object containing details of the water connection.
     * @return ResponseEntity containing the response object.
     */
    @RequestMapping(value = "/v1/_create", method = RequestMethod.POST)
    public ResponseEntity<WaterConnectionResponse> createWaterConnection(
            @Parameter(in = ParameterIn.DEFAULT, description = "Details for the new water connection + RequestInfo meta data.", required = true, schema = @Schema())
            @Valid @RequestBody WaterConnectionCreateRequest body
    ) {
        // Call the service layer to process the request
        var waterConnection = waterConnectionService.createWaterConnection(body);

        // Create the ResponseInfo object
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(body.getRequestInfo(), true);

        // Build the response
        WaterConnectionResponse response = WaterConnectionResponse.builder()
                .responseInfo(responseInfo)
                .waterConnection(Collections.singletonList(waterConnection))
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/v1/waterconnection/_search", method = RequestMethod.POST)
    public ResponseEntity<WaterConnectionResponse> searchWaterConnections(@Valid @RequestBody WaterConnectionSearchRequest waterConnectionSearchRequest) {

        // Search for water connections based on criteria
        List<WaterConnection> connections = waterConnectionService.searchWaterConnections(
                waterConnectionSearchRequest.getWaterConnectionSearchCriteria()
        );

        // Create response info
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(
                waterConnectionSearchRequest.getRequestInfo(), true
        );

        // Build the response
        WaterConnectionResponse response = WaterConnectionResponse.builder()
                .waterConnection(connections)
                .responseInfo(responseInfo)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
