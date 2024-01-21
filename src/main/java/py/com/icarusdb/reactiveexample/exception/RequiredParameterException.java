package py.com.icarusdb.reactiveexample.exception;

import jakarta.ws.rs.WebApplicationException;

/**
 * @author Betto McRose [icarus] / icarusdb@gmail.com
 */
public class RequiredParameterException extends WebApplicationException {

    private static final long serialVersionUID = 5883316873116685802L;

    public RequiredParameterException(String message) {
        super(message);
    }

}
