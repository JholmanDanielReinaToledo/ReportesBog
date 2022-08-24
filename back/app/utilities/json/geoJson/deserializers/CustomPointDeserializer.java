package utilities.json.geoJson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.postgis.Point;

import java.io.IOException;
import java.sql.SQLException;

public class CustomPointDeserializer  extends JsonDeserializer<Point> {
  @Override
  public Point deserialize(JsonParser jp, DeserializationContext dc)
    throws IOException, JsonProcessingException {
    // los puntos deben ser cargados como WKT, el backend los responde como geoJSON.
    // se hace de esta forma debido a un error con los setters en la librería org.postgis
    try {
      return new Point(jp.getText());
    } catch (SQLException throwables) {
      return null;
    }
  }
}
