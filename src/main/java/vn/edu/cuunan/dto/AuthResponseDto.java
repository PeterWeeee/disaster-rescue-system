package vn.edu.cuunan.dto;

public class AuthResponseDto {

    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String fullName;
    private String role;
    private String message;

    public AuthResponseDto() {
    }

    public AuthResponseDto(String token, String tokenType, Long id, String username, String fullName, String role, String message) {
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.message = message;
    }

    public static AuthResponseDtoBuilder builder() {
        return new AuthResponseDtoBuilder();
    }

    public static class AuthResponseDtoBuilder {
        private String token;
        private String tokenType = "Bearer";
        private Long id;
        private String username;
        private String fullName;
        private String role;
        private String message;

        public AuthResponseDtoBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthResponseDtoBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public AuthResponseDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuthResponseDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthResponseDtoBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public AuthResponseDtoBuilder role(String role) {
            this.role = role;
            return this;
        }

        public AuthResponseDtoBuilder message(String message) {
            this.message = message;
            return this;
        }

        public AuthResponseDto build() {
            return new AuthResponseDto(token, tokenType, id, username, fullName, role, message);
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
