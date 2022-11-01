package ro.siit.FinalProject.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleObjectNotFoundException (ObjectNotFoundException ex, Model model){
        LOGGER.error(ex.getMessage());

        model.addAttribute("exceptionMessage", ex.getMessage());

        return "Exception/customExceptionPage";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleIllegalArgumentException (IllegalArgumentException ex, Model model){
        LOGGER.error(ex.getMessage());

        model.addAttribute("exceptionMessage", "Username is not available. Please go back and insert a different username to register.");

        return "Exception/customExceptionPage";
    }

}
