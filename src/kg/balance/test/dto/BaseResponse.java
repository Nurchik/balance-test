package kg.balance.test.dto;

import com.fasterxml.jackson.annotation.JsonRootName;

public class BaseResponse {
    private String errorCode;
    private String errorText;

    private Result result;

    public BaseResponse (String errorCode, String errorText) {
        this.errorCode = errorCode;
        this.errorText = errorText;
        this.result = null;
    }

    public BaseResponse (String errorCode, String errorText, Result result) {
        this.errorCode = errorCode;
        this.errorText = errorText;
        this.result = result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
