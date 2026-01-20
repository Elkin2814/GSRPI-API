package co.unicauca.gsrpi_api.auth.domain.model.dto.response;

public class LoginResponse {
    private String message;
    private String accessToken;

    public LoginResponse(){}

    public LoginResponse(String message, String accessToken) {
        this.message = message;
        this.accessToken = accessToken;
    }

    // Getters
    public String getMessage() { return message; }
    public String getAccessToken() { return accessToken; }
}
