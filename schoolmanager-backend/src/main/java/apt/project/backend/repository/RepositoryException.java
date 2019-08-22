package apt.project.backend.repository;

public class RepositoryException extends Exception {

    private static final String REPOSITORY_EXCEPTION_MESSAGE = "Repository exception: ";
    private static final long serialVersionUID = 1L;

    public RepositoryException(Exception e) {
        super(REPOSITORY_EXCEPTION_MESSAGE, e);
    }

    public RepositoryException(String message) {
        super(REPOSITORY_EXCEPTION_MESSAGE + message);
    }

}
