package controllers;

import com.typesafe.config.Config;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.With;
import repository.seguridad.AccessRepository;
import utilities.json.Error;
import utilities.security.*;

import javax.inject.Inject;
import java.io.File;
import java.util.concurrent.CompletionStage;

public class FotosServiceController extends Controller {
  private final String baseFolder;
  private final AccessRepository accessRepository;

  @Inject
  FotosServiceController(
    AccessRepository accessRepository,
    Config config) {
    this.accessRepository = accessRepository;
    this.baseFolder = Try.of(() -> config
        .getString("sisdep.secured.smb.baseFolder"))
      .getOrElse("/archivos");
  }
  protected Future<Option<Result>> checkPermission(Permission permissionType, Long groupId) {
    return PermissionChecker.checkPermission(
      permissionType,
      groupId,
      List.of(Modulo.solo_lectura).map(Modulo::getId),
      accessRepository.getPermisosByGrupo(groupId));
  }
  @With({AuthAction.class})
  public CompletionStage<Result> getFile(String file, Http.Request request){
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    Future<Result> response = checkPermission(Permission.LEER, userInfo.groupId)
      .flatMap(access -> {
        if (access.isDefined()) {
          return Future.of(() -> forbidden());
        } else {
          File myfile = new File (baseFolder + "/fotos-ventero/" + file);
          return Future.of(() -> ok(myfile));
        }
      });
    return response
      .recover(ex -> {
        Logger.of("application").warn(
          "Excepción en la consulta de fotos, userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error creando", ex));
      })
      .toCompletableFuture();
  }
}
