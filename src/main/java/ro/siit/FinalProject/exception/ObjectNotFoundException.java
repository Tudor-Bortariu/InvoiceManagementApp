package ro.siit.FinalProject.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(){
        super("Object not found in database.");
    }
}
