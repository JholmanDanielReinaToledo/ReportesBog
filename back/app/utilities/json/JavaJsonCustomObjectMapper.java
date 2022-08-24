package utilities.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.vavr.jackson.datatype.VavrModule;
import play.libs.Json;
import utilities.json.geoJson.PostGISModule;

/**
 * Clase para aplicar configuraciones sobre Jackson.
 * Carga el módulo de Vavr, y configura las fechas con LocalDate.
 * <p>
 * Basada en:
 * https://www.playframework.com/documentation/2.6.x/JavaJsonActions#advanced-usage
 * https://geowarin.github.io/correctly-handle-jsr-310-java-8-dates-with-jackson/
 */
public class JavaJsonCustomObjectMapper {
  JavaJsonCustomObjectMapper() {
    ObjectMapper mapper = Json.mapper()
      // Módulo para mapear tipos de datos de PostGIS.
      .registerModule(new PostGISModule())
      // Módulo para mapear tipos de datos de Vavr.
      .registerModule(new VavrModule())
      // Configuración para evitar las fechas como [2018,1,1], y ponerlas como "2018-01-01".
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);

    Json.setObjectMapper(mapper);
  }
}