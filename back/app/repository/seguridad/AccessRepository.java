package repository.seguridad;

import io.ebean.DB;
import io.ebean.Database;
import io.ebean.Expr;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import models.seguridad.PermisoModuloGrupo;
import models.seguridad.Usuario;
import play.Logger;
import play.cache.AsyncCacheApi;
import play.db.ebean.EbeanConfig;
import utilities.database.DatabaseExecutionContext;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static utilities.security.PermissionChecker.CACHE_GRUPO_PREFIJO;

public class AccessRepository {

  private final Database database;
  private final ExecutorService executorService;
  private final AsyncCacheApi cache;

  @Inject
  public AccessRepository(
    EbeanConfig ebeanConfig,
    DatabaseExecutionContext executionContext,
    AsyncCacheApi cache
  ) {
    this.database = DB.byName(ebeanConfig.defaultServer());
    this.executorService = executionContext.getExecutorService();
    this.cache = cache;
  }

  /**
   * Pide los permisos sobre los módulos para un grupo.
   * Usa el caché primero pero si no existe, entonces hace la consulta y la cachéa.
   *
   * @param idGrupo
   * @return
   */
  public Future<List<PermisoModuloGrupo>> getPermisosByGrupo(Long idGrupo) {

    //OJO, puede ser null
    final Optional<List<PermisoModuloGrupo>> cachePermisos =
      cache.sync().get(CACHE_GRUPO_PREFIJO + idGrupo);

    return Option.ofOptional(cachePermisos)
      .map(Future::successful)
      .getOrElse(() ->
        Future.of(executorService, () ->
          List.ofAll(
            database
              .createQuery(PermisoModuloGrupo.class)
              .where().add(Expr.eq("idGrupo", idGrupo))
              .findList()
          ))
          .map(dbPermisos -> {
            Logger.of("application").debug("[AccessRepository][getPermisosByGrupo]: CREANDO CACHÉ DEL GRUPO " + idGrupo);
            cache.sync().set(CACHE_GRUPO_PREFIJO + idGrupo, dbPermisos, 900);
            return dbPermisos;
          })
      );

  }

  public Future<List<PermisoModuloGrupo>> getPermisosByUsuario(String username) {
    return Future.of(executorService, () -> {
      final Usuario user = database.createQuery(Usuario.class)
        .where().eq("usuario", username)
        .select("idGrupo").findOne();
      return List.ofAll(
        database
          .createQuery(PermisoModuloGrupo.class)
          .where().add(Expr.eq("idGrupo", user.idGrupo))
          .findList()
      );
    });
  }
}
