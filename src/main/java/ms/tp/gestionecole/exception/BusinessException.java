package ms.tp.gestionecole.exception;

/**
 * Violation d'une règle métier (doublon, capacité atteinte, étudiant non inscrit...).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
