package kg.balance.test.configs;

import kg.balance.test.dto.BaseResponse;
import kg.balance.test.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({AccessDenied.class, CompanyNotFound.class, SellPointNotFound.class, UserNotFound.class})
    public ResponseEntity<?> handleBalanceExceptions(CodedException ex) {
        return ResponseEntity.status(500).body(new BaseResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleBalanceExceptions(Exception ex) {
        return ResponseEntity.status(500).body(new BaseResponse("unhandled_exception", ex.getMessage()));
    }
}
