package py.com.icarusdb.reactiveexample.exception;

import jakarta.persistence.PersistenceException;

/**
 * @author Betto McRose [icarus] / icarusdb@gmail.com
 */
public class CustomPersistanceException extends PersistenceException {

    private static final long serialVersionUID = -6845224774837953189L;

    public CustomPersistanceException() {
        super();
    }

    public CustomPersistanceException(Throwable throwable) {
        super(throwable);
    }

}
