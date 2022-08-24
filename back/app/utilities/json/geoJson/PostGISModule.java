package utilities.json.geoJson;

import org.postgis.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import utilities.json.geoJson.deserializers.GeometryDeserializer;
import utilities.json.geoJson.serializers.GeometrySerializer;

/**
 * Module for loading serializers/deserializers.
 * 
 * @author Maycon Viana Bordin <mayconbordin@gmail.com>
 */
public class PostGISModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public PostGISModule() {
        super("PostGISModule");

        addSerializer(Geometry.class, new GeometrySerializer());
        addDeserializer(Geometry.class, new GeometryDeserializer());
    }
}
