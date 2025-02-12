package scaler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;
import scaler.config.WaterConnectionConfiguration;
import scaler.util.UserUtil;
import scaler.web.models.WaterConnection;
import scaler.web.models.WaterConnectionCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.user.CreateUserRequest;
import org.egov.common.contract.user.UserDetailResponse;
import org.egov.common.contract.user.UserSearchRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private UserUtil userUtils;
    private WaterConnectionConfiguration config;

    private ObjectMapper objectMapper;

    @Autowired
    public UserService(UserUtil userUtils, WaterConnectionConfiguration config,ObjectMapper objectMapper) {
        this.userUtils = userUtils;
        this.config = config;
        this.objectMapper=objectMapper;
    }

    /**
     * Calls user service to enrich user from search or upsert user for WaterConnection
     * @param request
     */
    public void callUserService(WaterConnectionCreateRequest request) {
        WaterConnection connection = request.getWaterConnection();

        connection.getConnectionHolders().forEach(holder -> {
            if (!StringUtils.isEmpty(holder.getUuid())) {
                enrichUser(holder, request.getRequestInfo(), connection.getTenantId());
            } else {
                User user = createConnectionHolderUser(holder, connection.getTenantId());
                holder.setUuid(upsertUser(user, request.getRequestInfo()).getUuid());
            }
        });
    }

    private User createConnectionHolderUser(User holder, String tenantId) {
        return User.builder()
                .userName(holder.getName().toLowerCase().replaceAll(" ", "_"))
                .name(holder.getName())
                .mobileNumber(holder.getMobileNumber())
                .emailId(holder.getEmailId())
                .tenantId(tenantId)
                .type("CITIZEN")
                .roles(holder.getRoles())
                .build();
    }

    private void enrichUser(User holder, RequestInfo requestInfo, String tenantId) {
        UserDetailResponse userDetailResponse = searchUser(userUtils.getStateLevelTenant(tenantId), holder.getUuid(), null);

        if (CollectionUtils.isEmpty(userDetailResponse.getUser())) {
            throw new CustomException("INVALID_ACCOUNTID", "No user exists for the given UUID: " + holder.getUuid());
        }

        User userFromSearch = userDetailResponse.getUser().get(0);
        holder.setName(userFromSearch.getName());
        holder.setMobileNumber(userFromSearch.getMobileNumber());
        holder.setEmailId(userFromSearch.getEmailId());
        holder.setRoles(userFromSearch.getRoles());
    }

    private User upsertUser(User user, RequestInfo requestInfo) {
        String tenantId = user.getTenantId();
        User userServiceResponse;

        // Search user by username
        UserDetailResponse userDetailResponse = searchUser(userUtils.getStateLevelTenant(tenantId), null, user.getUserName());
        if (!userDetailResponse.getUser().isEmpty()) {
            User userFromSearch = userDetailResponse.getUser().get(0);
            log.info("User found: {}", userFromSearch);

            if (!user.getUserName().equalsIgnoreCase(userFromSearch.getUserName())) {
                userServiceResponse = updateUser(requestInfo, user, userFromSearch);
            } else {
                userServiceResponse = userFromSearch;
            }
        } else {
            userServiceResponse = createUser(requestInfo, tenantId, user);
        }

        return userServiceResponse;
    }

    private User createUser(RequestInfo requestInfo, String tenantId, User userInfo) {
        userUtils.addUserDefaultFields(userInfo.getMobileNumber(), tenantId, userInfo);
        StringBuilder uri = new StringBuilder(config.getUserHost())
                .append(config.getUserContextPath())
                .append(config.getUserCreateEndpoint());

        CreateUserRequest user = new CreateUserRequest(requestInfo, userInfo);
        log.info("Creating user: {}", user.getUser());
        UserDetailResponse userDetailResponse = userUtils.userCall(user, uri);
        return userDetailResponse.getUser().get(0);
    }

    private User updateUser(RequestInfo requestInfo, User user, User userFromSearch) {
        userFromSearch.setName(user.getName());

        StringBuilder uri = new StringBuilder(config.getUserHost())
                .append(config.getUserContextPath())
                .append(config.getUserUpdateEndpoint());

        UserDetailResponse userDetailResponse = userUtils.userCall(new CreateUserRequest(requestInfo, userFromSearch), uri);
        return userDetailResponse.getUser().get(0);
    }

    /**
     * Calls the user search API based on the given accountId and userName
     * @param stateLevelTenant
     * @param accountId
     * @param userName
     * @return
     */
    public UserDetailResponse searchUser(String stateLevelTenant, String accountId, String userName)  {
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setActive(false);
        userSearchRequest.setTenantId(stateLevelTenant);

        if (StringUtils.isEmpty(accountId) && StringUtils.isEmpty(userName)) {
            return null;
        }

        if (!StringUtils.isEmpty(accountId)) {
            userSearchRequest.setUuid(Collections.singletonList(accountId));
        }

        if (!StringUtils.isEmpty(userName)) {
            userSearchRequest.setUserName(userName);
        }

        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        try {
            String requestJson = objectMapper.writeValueAsString(userSearchRequest);
            System.out.println("User Request: " + requestJson);


        } catch (Exception e){
            throw new CustomException("ABC","here");
        }
        // Print or log the JSON string
        return userUtils.userCall(userSearchRequest, uri);
    }

    /**
     * Calls the user search API based on the given list of user uuids
     * @param uuids
     * @return
     */
    private Map<String, User> searchBulkUser(List<String> uuids) {
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setActive(false);
        //userSearchRequest.setUserType("CITIZEN");

        if (!CollectionUtils.isEmpty(uuids)) {
            userSearchRequest.setUuid(uuids);
        }

        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        UserDetailResponse userDetailResponse = userUtils.userCall(userSearchRequest, uri);
        List<User> users = userDetailResponse.getUser();

        if (CollectionUtils.isEmpty(users)) {
            throw new CustomException("USER_NOT_FOUND", "No user found for the uuids");
        }

        return users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));
    }
}
