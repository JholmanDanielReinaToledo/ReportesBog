package controllers;

import com.typesafe.config.Config;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import play.Logger;
import play.mvc.*;
import repository.seguridad.AccessRepository;
import utilities.json.Error;
import utilities.security.*;

import javax.inject.Inject;
import java.io.File;
import java.util.concurrent.CompletionStage;


enum Rutas{
  registro_entrega("registro-entrega"),
  resolucion("pdf-resoluciones"),
  notificacion("guia-notificacion"),
  visita_administrativa("visita-administrativa"),
  ventero("pdf-venteros"),
  evidencia_oferta_social("evidencias-oferta");

  private String idRuta;

  Rutas(String id) {
    this.idRuta = id;
  }

  public String getId() {
    return idRuta;
  }
}

public class ArchivosServiceController extends Controller {
  private final String baseFolder;
  private final AccessRepository accessRepository;

  Map<String, List<Modulo>> modulos = HashMap.ofEntries(
    Tuple.of(Rutas.registro_entrega.getId(), List.of(Modulo.regulaciones_modulos)),
    Tuple.of(Rutas.resolucion.getId(), List.of(Modulo.regulaciones_autorizacion, Modulo.pev_regulaciones_pev)),
    Tuple.of(Rutas.notificacion.getId(), List.of(Modulo.regulaciones_autorizacion, Modulo.pev_regulaciones_pev)),
    Tuple.of(Rutas.visita_administrativa.getId(), List.of(Modulo.regulaciones_autorizacion)),
    Tuple.of(Rutas.ventero.getId(), List.of(Modulo.solo_lectura)),
    Tuple.of(Rutas.evidencia_oferta_social.getId(), List.of(Modulo.social_ofertas))
  );

  @Inject
  ArchivosServiceController(
    AccessRepository accessRepository,
    Config config) {
    this.accessRepository = accessRepository;
    this.baseFolder = Try.of(() -> config
        .getString("sisdep.secured.smb.baseFolder"))
      .getOrElse("/archivos");
  }

  private Future<Option<Result>> checkPermission(Long groupId, List<Modulo> modulo) {
    return PermissionChecker.checkPermission(
      Permission.LEER,
      groupId,
      modulo.map(Modulo::getId),
      accessRepository.getPermisosByGrupo(groupId));
  }

  @With({AuthAction.class})
  public CompletionStage<Result> getFile(String file, Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    String ruta = request.uri().replace("/sisdep/api/archivos/", "").split("/")[0];
    Option<List<Modulo>> lists = modulos.get(ruta);
    Future<Result> response = lists.fold(
      () -> Future.of(() -> unauthorized(Error.getError(
        "Ruta " + ruta + " no encontrada",
        "La ruta a la que trata de acceder no existe."
        ))),
      listaModulos -> {
        return checkPermission(userInfo.groupId, listaModulos)
          .flatMap(access -> {
            Future<? extends Result> result;
            if (access.isDefined()) {
              result = Future.of(Results::forbidden);
            } else {
              File myFile = new File(baseFolder +"/"+ruta+ "/" + file);
              result = Future.of(() -> ok(myFile));
            }
            return result;
          });
      }
    );

    return response
      .recover(ex -> {
        Logger.of("application").warn(
          "Excepción en la consulta de archivos, userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error consultando", ex));
      })
      .toCompletableFuture();
  }
}
