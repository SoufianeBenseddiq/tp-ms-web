package ms.tp.gestionecole.dto;

import jakarta.validation.constraints.NotNull;

/**
 * @param prerequisiteId module à valider (obligatoire)
 * @param equivalentId   module équivalent qui remplit aussi la condition (optionnel)
 */
public record CoursePrerequisiteRequest(@NotNull Long prerequisiteId, Long equivalentId) {
}
