package digital.zil.hl.module1.controller.exeption;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
