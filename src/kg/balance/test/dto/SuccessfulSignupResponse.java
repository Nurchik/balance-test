package kg.balance.test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import kg.balance.test.models.User;

@JsonRootName(value = "result")
class Result {
    @JsonProperty("auth_token")
    private String jwtToken;

    @JsonProperty("user_data")
    private User userData;

    public String getJwtToken() {
        return jwtToken;
    }

    Result(String jwtToken, User userData) {
        this.jwtToken = jwtToken;
        this.userData = userData;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public User getUserData() {
        return userData;
    }

    public void setUserData(User userData) {
        this.userData = userData;
    }
}

public class SuccessfulSignupResponse extends BaseResponse {

    private Result result;

    public SuccessfulSignupResponse(String jwtToken, User userData) {
        super("ok", null);
        result = new Result(jwtToken, userData);
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
