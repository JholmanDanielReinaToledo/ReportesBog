package utilities.security;

import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import models.seguridad.PermisoModuloGrupo;
import play.mvc.Result;
import utilities.json.Error;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static play.mvc.Results.forbidden;

public class PermissionChecker {

  /**
   * Es el prefijo que se usa para el nombre de la caché de los permisos.
   */
  static public final String CACHE_GRUPO_PREFIJO = "cache_grupo_";

  static boolean permisosEspeciales(
    Long groupId,
    List<Long> modulosAsociados
  ) {
    return modulosAsociados.contains(Modulo.solo_lectura.getId());
  }

  /**
   * Permite checkear si un grupo tiene los permisos adecuados sobre una lista de módulos asociados.
   * <p>
   * Si sí tiene permisos, devuelve un None.
   * Si no tiene, devuelve un Some(forbidden)
   *
   * @param permissionType
   * @param groupId
   * @param modulosAsociados
   * @param futListaPermisos
   * @return
   */

  static public Future<Option<Result>> checkPermission(
    Permission permissionType,
    Long groupId,
    List<Long> modulosAsociados,
    Future<List<PermisoModuloGrupo>> futListaPermisos
  ) {

    return futListaPermisos
      .map(listaPermisos -> {
        // Cuando es del grupo de super usuario, tiene permisos TOTALES.
        if (groupId == 0) return true;

        final List<PermisoModuloGrupo> filtrados =
          listaPermisos.filter(p -> modulosAsociados.contains(p.idModuloSistema));
        // Logger.of("application").debug(filtrados.toString());
        return Match(permissionType).of(
          Case($(Permission.LEER), () -> filtrados.map(p -> p.leer).contains(true) || permisosEspeciales(groupId, modulosAsociados)),
          Case($(Permission.ESCRIBIR), () -> filtrados.map(p -> p.escribir).contains(true)),
          Case($(Permission.BORRAR), () -> filtrados.map(p -> p.borrar).contains(true)),
          Case($(Permission.EXPORTAR), () -> filtrados.map(p -> p.exportar).contains(true)),
          Case($(), false)
        );
      })
      .map(canDo -> canDo ?
        Option.none() :
        Option.of(forbidden(Error.getError(
          "No autorizado",
          "No tienes permitido operar sobre el recurso al que tratas de accerder. " +
            "Pidele acceso a un administrador.")))
      );
  }
}
