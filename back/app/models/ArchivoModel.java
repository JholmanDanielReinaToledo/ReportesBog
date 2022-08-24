package models;

import java.time.LocalDate;

public interface ArchivoModel extends SimpleModel {
  void setIp(String ip);

  String getIp();

  LocalDate getFechaEdita();

  void setFechaEdita(LocalDate fechaEdita);

  Long getIdUsuarioEdita();

  void setIdUsuarioEdita(Long idUsuarioEdita);
}
