package utilities.generic;

import io.ebean.Model;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import models.SimpleModel;
import play.db.ebean.EbeanConfig;
import utilities.database.ExcecutionContext;

/**
 * Clase abstracta usada para consultar en las vistas de reporte,
 * donde solo se puede consultar pero no modificar nada.
 *
 * @param <U>
 */
public abstract class GenericRepositoryReportes<U extends Model & SimpleModel> extends GenericRepository<U> {

  public GenericRepositoryReportes(EbeanConfig ebeanConfig, ExcecutionContext executionContext, Class<U> typeClass, String entityName) {
    this(ebeanConfig.defaultServer(), executionContext, typeClass, entityName);
  }

  public GenericRepositoryReportes(String serverName, ExcecutionContext executionContext, Class<U> typeClass, String entityName) {
    super(serverName, executionContext, typeClass, entityName);
  }

  public Future<Either<String, Boolean>> delete(Long id) {
    return Future.of(executorService,
      () -> Either.left("Por regla de negocio, esta acción no puede ser ejecutada."));
  }

  public Future<Either<String, U>> update(Long id, U item) {
    return Future.of(executorService,
      () -> Either.left("Por regla de negocio, esta acción no puede ser ejecutada."));
  }

  public Future<Either<String, U>> insert(U item) {
    return Future.of(executorService,
      () -> Either.left("Por regla de negocio, esta acción no puede ser ejecutada."));
  }

  public Future<Either<String, U>> findById(Long id) {
    return Future.of(executorService,
      () -> Either.left("Por regla de negocio, esta acción no puede ser ejecutada."));
  }

//  public Future<Either<String, List<U>>> findByQuery(Map<String, String[]> queryParams) {
//    return Future.of(executorService,
//      () -> Either.left("Por regla de negocio, esta acción no puede ser ejecutada."));
//  }
//  public Source<U, NotUsed> findByQueryStream(Map<String, String[]> queryParams) {
//    return Source.failed(new Exception("Por regla de negocio, esta acción no puede ser ejecutada."));
//  }
}
