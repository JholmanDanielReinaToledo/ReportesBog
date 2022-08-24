package utilities.generic;

import akka.NotUsed;
import akka.stream.javadsl.*;
import io.ebean.Model;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Try;
import models.SimpleModel;
import play.Logger;
import play.db.ebean.EbeanConfig;
import utilities.database.ExcecutionContext;

import java.util.Map;
import java.util.Optional;

/**
 * Clase abstracta usada para consultar en las vistas de reporte,
 * donde solo se puede consultar pero no modificar nada.
 * <p>
 * Y además, se usa cuando las tablas no tienen ID.
 *
 * @param <U>
 */
public abstract class GenericRepositoryLogs<U extends Model & SimpleModel> extends GenericRepositoryReportes<U> {

  public GenericRepositoryLogs(
    EbeanConfig ebeanConfig,
    ExcecutionContext executionContext,
    Class<U> typeClass,
    String entityName
  ) {
    this(ebeanConfig.defaultServer(), executionContext, typeClass, entityName);
  }

  public GenericRepositoryLogs(
    String serverName,
    ExcecutionContext executionContext,
    Class<U> typeClass,
    String entityName
  ) {
    super(serverName, executionContext, typeClass, entityName);
  }

  public Future<Either<String, List<U>>> findByQuery(Map<String, String[]> queryParams) {
    return Future.of(executorService,
      () -> Either.left("Por regla de negocio, esta acción no puede ser ejecutada."));
  }

  public Source<U, NotUsed> findByQueryStream(Map<String, String[]> queryParams) {
    return Source.failed(new Exception("Por regla de negocio, esta acción no puede ser ejecutada."));
  }

  /**
   * Va generando un índice sobre cada elemento del stream.
   *
   * @return
   */
  public Source<U, NotUsed> getAllStream() {
    final Source<U, NotUsed> baseStream = Source.unfoldResource(
      () -> database.createQuery(typeClass).findIterate(),
      iterableQuery -> iterableQuery.hasNext() ? Optional.of(iterableQuery.next()) : Optional.empty(),
      iterableQuery -> iterableQuery.close()
    );

    return baseStream.zipWithIndex().map(pair -> {
      final U item = pair.first();
      final Long i = (Long) pair.second();

      item.setId(i);
      return item;
    });
  }

  /**
   * Genera un índice sobre cada elemento de la lista.
   *
   * @return
   */
  public Future<Either<String, List<U>>> getAll() {
    return Future.of(executorService, () ->
      Try.of(() -> {
          List<U> list = List.ofAll(database.createQuery(typeClass).setMaxRows(10000).findList());
          return list.zipWithIndex().map(pair -> {
            final U item = pair._1;
            final Long i = pair._2.longValue();

            item.setId(i);
            return item;
          });
        })
        .onFailure(ex -> Logger.of("application").warn("[Repository][getAll][" + entityName + "] -> ", ex))
        .toEither().mapLeft(Throwable::getLocalizedMessage));
  }

}
