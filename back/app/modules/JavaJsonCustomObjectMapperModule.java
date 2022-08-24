package modules;

import com.google.inject.AbstractModule;
import utilities.json.JavaJsonCustomObjectMapper;

/**
 * Módulo para cargar al inicio el ObjectMapper para los Json.
 * Más detalles en la clase JavaJsonCustomObjectMapper.
 *
 * @see utilities.json.JavaJsonCustomObjectMapper
 */
public class JavaJsonCustomObjectMapperModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(JavaJsonCustomObjectMapper.class).asEagerSingleton();
  }

}