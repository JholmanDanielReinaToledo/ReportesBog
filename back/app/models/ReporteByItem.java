package models;

import java.util.HashMap;

public interface ReporteByItem extends  SimpleModel{
  void setIdItem(Long idItem);
  Long getIdItem();
  void setIdAnalisis(Long idAnalisis);
  Long getIdAnalisis();
  void setCantidad(String cantidad);
  String getCantidad();

  HashMap<String, Object> getOthers();
}
