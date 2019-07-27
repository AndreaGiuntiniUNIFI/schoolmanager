package apt.project.backend.repository;

public class RepositoryException extends Exception {

    private static final long serialVersionUID = 1L;

    public RepositoryException(String message) {
        super("Repository exception: " + message);
    }

}
