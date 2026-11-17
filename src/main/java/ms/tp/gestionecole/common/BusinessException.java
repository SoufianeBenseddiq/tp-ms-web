package ms.tp.gestionecole.common;

/**
 * Violation d'une règle métier (doublon, capacité atteinte, étudiant non inscrit...).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
