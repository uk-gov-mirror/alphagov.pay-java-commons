package uk.gov.pay.commons.api.resources;

class ResourceStrings {
    private static final String API_OPERATION_NOTES_AUTH = "The Authorisation token needs to be specified in the 'authorization' header ";
    private static final String API_OPERATION_NOTES_BEARER = "as 'authorization: Bearer YOUR_API_KEY_HERE'";
    static final String API_OPERATION_NOTES_AUTH_BEARER = API_OPERATION_NOTES_AUTH + API_OPERATION_NOTES_BEARER;
    static final String API_RESPONSE_ERROR_MSG_CREDENTIALS_REQ = "Credentials are required to access this resource";
    static final String API_RESPONSE_OK = "OK";
    static final String API_RESPONSE_CREATED = "Created";
    static final String API_RESPONSE_ERROR_MSG_BAD_REQUEST = "Bad request";
    static final String API_RESPONSE_ERROR_MSG_TOO_MANY_REQUESTS = "Too many requests";
    static final String API_OPERATION_ERROR_MSG_NOT_FOUND = "Not found";
    static final String API_OPERATION_ERROR_MSG_DOWNSTREAM_SYS_ERROR = "Downstream system error";
}
