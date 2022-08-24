package models;

import java.time.LocalDate;

/**
 * Interface con un get y set de un atributo ID.
 *
 * Es usado por las clases genéricas para operar sobre el ID de un modelo.
 * Cualquier modelo que desee usar las clases genéricas, debe implementar
 * esto también.
 */
public interface SimpleModel {

  Long getId();

  void setId(Long id);
}
