package digital.zil.hl.module1.controller.exception;

/**
 * Своя бизнес-ошибка приложения.
 * Её бросаем, когда нарушены правила бронирования или валидации.
 */
public class AirlineException extends RuntimeException {

    public AirlineException(String message) {
        super(message);
    }
}
