package com.flm.irai.common.exception;

import java.util.UUID;

// Exception personnalisee pour les ressources non trouvees
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, UUID id) {
        super(resourceName + " avec l'id " + id + " non trouve");
    }

    public ResourceNotFoundException(String resourceName, String field, String value) {
        super(resourceName + " avec " + field + " = '" + value + "' non trouve");
    }
}
