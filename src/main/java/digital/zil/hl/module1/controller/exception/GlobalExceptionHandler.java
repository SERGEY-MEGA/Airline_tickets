package digital.zil.hl.module1.controller.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import digital.zil.hl.module1.controller.dto.ErrorResponse;

/**
 * Перехватывает бизнес-ошибки и превращает их в понятный HTTP-ответ.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Возвращает ошибку клиента с текстом проблемы.
     */
    @ExceptionHandler(AirlineException.class)
    public ResponseEntity<ErrorResponse> handleAirlineException(AirlineException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }
}
