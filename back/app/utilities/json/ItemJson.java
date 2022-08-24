package utilities.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.Model;
import io.vavr.collection.List;
import models.SimpleModel;
import play.libs.Json;


/**
 * Esta clase implementa las funciones para generar Json normalizados.
 * Está hecho para cumplir la especificación mencionada en:
 * https://github.com/paularmstrong/normalizr
 *
 * Un ejemplo es el siguiente:
 * <code>
 * {
 *  "entities": {
 *    "infraesAsociada": {
 *      "1": {
 *        "id": 1,
 *        "codigo": 1,
 *        "idContrato": 1,
 *        "idElemento": 1,
 *        "idSegmento": 5013512,
 *        "desde": 0.0,
 *        "hasta": 46.18451316,
 *        "offset": -1.2,
 *        "ordenELemento": 3
 *      },
 *      "73": {
 *       "id": 73,
 *       "codigo": 348,
 *       "idContrato": 137,
 *       "idElemento": 1,
 *       "idSegmento": 5009639,
 *       "desde": 0.0,
 *       "hasta": 44.33857721,
 *       "offset": 3.65,
 *       "ordenELemento": 3
 *      },
 *    }
 *  }
 * }
 * </code>
 */
public class ItemJson {

  /**
   * Genera un Json normalizado con base en un objeto.
   *
   * @param entityName Nombre de la entidad.
   * @param item       Objeto que se quiere convertir.
   * @param <U>        Tipo genérico. Asegura que el objeto viene del modelo.
   * @return Json en formato normalizado.
   */
  public static <U extends Model & SimpleModel> JsonNode generateJson(String entityName, U item) {
    final String idName = item.getId().toString();
    final ObjectNode normalizedItem = Json.newObject();
    normalizedItem.set(idName, Json.toJson(item));
    final ObjectNode js = Json.newObject();
    js.set(entityName, normalizedItem);

    return Json.newObject().set("entities", js);
  }

  /**
   * Genera un Json normalizado con base en una lista de objetos.
   * La lista hace uso de la función foldLeft de vavr (esto simplifica el proceso).
   *
   * @param entityName Nombre de la entidad.
   * @param itemList   Lista de elementos, usando la clase `List` de vavr.
   * @param <U>        Tipo genérico. Asegura que el objeto viene del modelo.
   * @return Json en formato normalizado.
   */
  public static <U extends Model & SimpleModel> JsonNode generateJson(String entityName, List<U> itemList) {
    JsonNode normalizedItems =
      itemList.foldLeft(
        Json.newObject(),
        (innerJs, item) -> {
          final String idName = item.getId().toString();
          innerJs.set(idName, Json.toJson(item));
          return innerJs;
        });

    final ObjectNode js = Json.newObject();
    js.set(entityName, normalizedItems);

    return Json.newObject().set("entities", js);
  }
}
