package kg.balance.test.configs;

import kg.balance.test.dto.BaseResponse;
import kg.balance.test.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.Valid;

@ControllerAdvice
public class RestExceptionHandlers extends ResponseEntityExceptionHandler {
    @ExceptionHandler({CompanyNotFound.class, SellPointNotFound.class, UserNotFound.class})
    public ResponseEntity<?> handleBalanceExceptions(CodedException ex) {
        return ResponseEntity.status(500).body(new BaseResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleBalanceExceptions(Exception ex) {
        return ResponseEntity.status(500).body(new BaseResponse("unhandled_exception", ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Valid valid_annotation = ex.getParameter().getParameterAnnotation(Valid.class);
        if (valid_annotation != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Object) new BaseResponse("validation_error", ex.getBindingResult().toString()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Object) new BaseResponse("parameter_error", ex.getBindingResult().toString()));
    }
}
