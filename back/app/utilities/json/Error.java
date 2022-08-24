package utilities.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vavr.control.Option;
import org.postgresql.util.PSQLException;
import play.libs.Json;

import java.sql.SQLException;

/**
 * Clase para generar mensajes de error en formato JSON:API.
 */
public class Error {

  public static JsonNode getError(String title, Throwable e) {
    Option<String> map = Option.of(e.getCause())
      .filter(PSQLException.class::isInstance)
      .map(PSQLException.class::cast)
      .map(SQLException::getSQLState);
    return getError(title, e.getLocalizedMessage(), map);
  }

  public static JsonNode getError(String title, String detail, Option<String> sqlCode) {
    final ObjectNode js = Json.newObject();
    js.put("title", title);
    js.put("detail", detail);

    if (sqlCode.isDefined()) {
      js.put("code", sqlCode.get());
    }

    final ObjectNode err = Json.newObject();
    err.putArray("errors").add(js);
    return err;
  }

  public static JsonNode getError(String title, String detail) {
    final ObjectNode js = Json.newObject();
    js.put("title", title);
    js.put("detail", detail);

    final ObjectNode err = Json.newObject();
    err.putArray("errors").add(js);
    return err;
  }
}
