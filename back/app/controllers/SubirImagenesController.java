package controllers;

import io.vavr.collection.List;
import io.vavr.control.Either;
import play.libs.Files;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import play.mvc.Http.MultipartFormData.FilePart;
import utilities.images.UploadImages;
import utilities.json.Error;
import utilities.security.AuthAction;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;


public class SubirImagenesController extends Controller {

  final UploadImages uploadImages;

  @Inject
  SubirImagenesController(UploadImages uploadImages) {
    this.uploadImages = uploadImages;
  }

  @With({AuthAction.class})
  public Result uploadFotoSegmento(Long idOp, Http.Request request) {
    final String folder = request.body().asMultipartFormData().asFormUrlEncoded().get("folder")[0];
    final String identificador = request.body().asMultipartFormData().asFormUrlEncoded().get("identificador")[0];
    final Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
    final List<FilePart<Files.TemporaryFile>> listFiles = List.ofAll(body.getFiles());
    if (listFiles.forAll(f -> f.getContentType().contains("image"))) {
      final List<Either<String, byte[]>> recomprimidosJpeg = listFiles.map(file ->
        uploadImages.recompress(file.getRef().path().toFile())
      );

      final Either<String, URL> subido = recomprimidosJpeg.head().flatMap(imgBytes ->
        uploadImages.saveImage(imgBytes, folder, identificador));

      return subido.map(url ->
        ok(Json.newObject()
          .put("idOp", idOp)
          .put("url", url.toExternalForm())
        )
      ).getOrElseGet(ex -> internalServerError(
        Error.getError("Error subiendo imagen", ex)));

    } else {
      return badRequest(
        Error.getError(
          "Error subiendo imagen",
          "Solo se procesan imágenes"));
    }
  }

  @With({AuthAction.class})
  public Result deleteFotoSegmento(Long idOp, Http.Request request) {
    final String folder = request.body().asMultipartFormData().asFormUrlEncoded().get("folder")[0];
    final String identificador = request.body().asMultipartFormData().asFormUrlEncoded().get("identificador")[0];

    final Either<String, URL> borrado = uploadImages.deleteImage(folder, identificador);

    return borrado.map(url ->
      ok(Json.newObject()
        .put("idOp", idOp)
        .put("url", url.toExternalForm())
      )
    ).getOrElseGet(ex -> internalServerError(
      Error.getError("Error borrando imagen", ex)));

  }

  @With({AuthAction.class})
  public Result moveFotoSegmento(Long idOp, Http.Request request) {
    final String folderFrom = request.body().asMultipartFormData().asFormUrlEncoded().get("folderFrom")[0];
    final String folderTo = request.body().asMultipartFormData().asFormUrlEncoded().get("folderTo")[0];
    final String identificador = request.body().asMultipartFormData().asFormUrlEncoded().get("identificador")[0];

    final Either<String, URL> movido = uploadImages.moveImage(folderFrom, folderTo, identificador);

    return movido.map(url ->
      ok(Json.newObject()
        .put("idOp", idOp)
        .put("url", url.toExternalForm())
      )
    ).getOrElseGet(ex -> internalServerError(
      Error.getError("Error moviendo imagen", ex)));

  }

  @With({AuthAction.class})
  public Result renameFotoSegmento(Long idOp, Http.Request request) {
    final String folder = request.body().asMultipartFormData().asFormUrlEncoded().get("folder")[0];
    final String identificadorOld = request.body().asMultipartFormData().asFormUrlEncoded().get("identificadorOld")[0];
    final String identificadorNew = request.body().asMultipartFormData().asFormUrlEncoded().get("identificadorNew")[0];

    final Either<String, URL> renombrado = uploadImages.renameImage(folder, identificadorOld, identificadorNew);

    return renombrado.map(url ->
      ok(Json.newObject()
        .put("idOp", idOp)
        .put("url", url.toExternalForm())
      )
    ).getOrElseGet(ex -> internalServerError(
      Error.getError("Error renombrando imagen", ex)));

  }
}
