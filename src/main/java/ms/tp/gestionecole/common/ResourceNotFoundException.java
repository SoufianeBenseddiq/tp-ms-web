package ms.tp.gestionecole.common;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " introuvable (id=" + id + ")");
    }
}
