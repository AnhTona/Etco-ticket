package com.esco.etco.util.constant;

public final class ApiPaths {

    public static final String API_V1_ADMIN = "/api/v1";

    // Module Paths
    public static final String PRODUCERS_API = API_V1_ADMIN + "/producers";
    public static final String SEATS_API = API_V1_ADMIN + "/seats";
    public static final String TRANSACTIONS_API = API_V1_ADMIN + "/transactions";
    public static final String USER_TICKETS_API = API_V1_ADMIN + "/user-tickets";
    public static final String EVENT_STAFFS_API = API_V1_ADMIN + "/event-staffs";

    // Other Admin Paths
    public static final String PERMISSIONS_API = API_V1_ADMIN + "/permissions";
    public static final String ROLES_API = API_V1_ADMIN + "/roles";
    public static final String USERS_API = API_V1_ADMIN + "/users";
    public static final String GENRES_API = API_V1_ADMIN + "/genres";
    public static final String EVENTS_API = API_V1_ADMIN + "/events";
    public static final String TICKETS_API = API_V1_ADMIN + "/tickets";

    // Client/Public Paths
    public static final String CLIENT_ORDERS_API = "/api/v1/orders";
    public static final String CLIENT_AI_API = "/api/v1/ai";
    public static final String AUTH_API = "/api/v1/auth";
}
