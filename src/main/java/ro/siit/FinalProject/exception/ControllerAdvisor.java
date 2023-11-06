package ro.siit.FinalProject.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleObjectNotFoundException (ObjectNotFoundException ex, Model model){
        log.error(ex.getMessage());

        model.addAttribute("exceptionMessage", ex.getMessage());

        return "Exception/customExceptionPage";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException (IllegalArgumentException ex, Model model){
        log.error(ex.getMessage());

        model.addAttribute("exceptionMessage", ex.getMessage());

        return "Exception/customExceptionPage";
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException (NullPointerException ex, Model model){
        log.error(ex.getMessage());

        model.addAttribute("exceptionMessage", ex.getMessage());

        return "Exception/customExceptionPage";
    }

}
