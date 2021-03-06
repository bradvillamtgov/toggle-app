package gov.mt.dnrc.toggle.software.repository;

import gov.mt.dnrc.toggle.software.models.Software;
import org.springframework.data.repository.CrudRepository;

/**
 * Crud style repository following the Spring Data model.
 *
 * @author Brad Villa
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SoftwareRepository  extends CrudRepository<Software, Long> {
}
