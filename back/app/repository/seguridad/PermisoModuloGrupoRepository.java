package repository.seguridad;

import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import models.seguridad.PermisoModuloGrupo;
import play.cache.AsyncCacheApi;
import play.db.ebean.EbeanConfig;
import utilities.database.DatabaseExecutionContext;
import utilities.generic.GenericRepository;

import javax.inject.Inject;

import static utilities.security.PermissionChecker.CACHE_GRUPO_PREFIJO;

public class PermisoModuloGrupoRepository extends GenericRepository<PermisoModuloGrupo> {

  private final AsyncCacheApi cache;

  @Inject
  public PermisoModuloGrupoRepository(
    EbeanConfig ebeanConfig,
    DatabaseExecutionContext databaseExecutionContext,
    AsyncCacheApi cache
  ) {
    super(ebeanConfig, databaseExecutionContext, PermisoModuloGrupo.class, "permisoModuloGrupo");
    super.idNameDB = "ID";
    this.cache = cache;
  }

  @Override
  public Future<Either<String, Boolean>> delete(Long id) {
    return Future.fromCompletableFuture(
      cache.removeAll()
        .toCompletableFuture())
      .flatMap(x -> super.delete(id));
  }

  @Override
  public Future<Either<String, PermisoModuloGrupo>> update(Long id, PermisoModuloGrupo item) {
    return Future.fromCompletableFuture(
      cache.remove(CACHE_GRUPO_PREFIJO + item.idGrupo)
        .toCompletableFuture())
      .flatMap(x -> super.update(id, item));
  }

  @Override
  public Future<Either<String, PermisoModuloGrupo>> insert(PermisoModuloGrupo item) {
    return Future.fromCompletableFuture(
      cache.remove(CACHE_GRUPO_PREFIJO + item.idGrupo)
        .toCompletableFuture())
      .flatMap(x -> super.insert(item));
  }

}
