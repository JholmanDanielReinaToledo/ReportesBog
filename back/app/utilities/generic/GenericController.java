package utilities.generic;

import akka.japi.pf.PFBuilder;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.Model;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import models.ArchivoModel;
import models.SimpleModel;
import java.time.LocalDate;
import play.Logger;
import play.libs.EventSource;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import repository.seguridad.AccessRepository;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.FiniteDuration;
import utilities.database.Page;
import utilities.excel.ExcelGenerator;
import utilities.json.Error;
import utilities.json.ItemJson;
import utilities.scala.ExecutionContextExecutorServiceBridge;
import utilities.security.*;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class GenericController<U extends Model & SimpleModel> extends Controller {
  private static final int DEFAULT_PAGE_SIZE = 100;

  protected GenericRepositoryInterface<U> repository;
  protected HttpExecutionContext httpExecutionContext;
  protected List<Long> modulosAsociados;
  protected Option<Map<String, String>> excelNamer;
  private final AccessRepository accessRepository;
  protected Integer streamRate = 100;
  @Inject
  protected Materializer materializer;

  public GenericController(
    GenericRepositoryInterface<U> repository,
    HttpExecutionContext httpExecutionContext,
    AccessRepository accessRepository
  ) {
    this.repository = repository;
    this.httpExecutionContext = httpExecutionContext;
    this.accessRepository = accessRepository;
    this.modulosAsociados = List.of(Modulo.solo_lectura).map(Modulo::getId); //modulosAsociados;
    this.excelNamer = Option.none();
  }

  // función para cuando se usa Future.
  protected ExecutorService getHttpExecutionService() {
    return ExecutionContextExecutorServiceBridge.apply((ExecutionContextExecutor) httpExecutionContext.current());
  }


  /**
   * Función privada que chequea los permisos sobre un recurso con base en los módulos asociados.
   * <p>
   * Tiene un caso especial, y es el de solo lectura.
   *
   * @param permissionType Tipo de permiso según el Enum Permission
   * @param groupId        El id del grupo.
   * @return Un tipo option. Si es None, es porque puede, si no, devuelve forbidden.
   */
  protected Future<Option<Result>> checkPermission(Permission permissionType, Long groupId) {
    return PermissionChecker.checkPermission(
      permissionType,
      groupId,
      modulosAsociados,
      accessRepository.getPermisosByGrupo(groupId));
  }


  /**
   * @return
   */
  @With({AuthAction.class})
  public CompletionStage<Result> getAllStream(Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    final Map<String, String[]> params = request.queryString();

    Future<Result> response =
      checkPermission(Permission.LEER, userInfo.groupId)
        .flatMap(access -> {
          if (access.isDefined()) {
            return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
          } else {
            // Caso cuando se mandan queryParams.
            final Source<U, ?> repositoryGetStream = params.isEmpty() ?
              repository.getAllStream() :
              repository.findByQueryStream(params);

            final Source<JsonNode, ?> jsonStream = repositoryGetStream
              .completionTimeout(FiniteDuration.create(5, TimeUnit.MINUTES))
              .grouped(streamRate)
              .map(Json::toJson);

            final Source<JsonNode, ?> recoveredJson = jsonStream.recover(
              new PFBuilder<Throwable, JsonNode>()
                .matchAny(e -> Error.getError("Error enviando el stream", e))
                .build()
            );

            final Source<EventSource.Event, ?> eventSource = recoveredJson
              .map(EventSource.Event::event);

            final Result myStream = ok()
              .chunked(eventSource.via(EventSource.flow()))
              .as(Http.MimeTypes.EVENT_STREAM);

            return Future.of(getHttpExecutionService(), () -> myStream);
          }
        });

    return response
      .recover(ex -> {
        Logger.of("application").warn("Excepción en la consulta en stream. entityName: " + repository.getEntityName() + ", userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error consultado", ex));
      })
      .toCompletableFuture();
  }


  /**
   * @return
   */
  @With({AuthAction.class})
  public CompletionStage<Result> getAll(Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    final Map<String, String[]> params = request.queryString();

    Future<Result> response =
      checkPermission(Permission.LEER, userInfo.groupId)
        .flatMap(access -> {
          if (access.isDefined()) {
            return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
          } else {
            final Future<Either<String, List<U>>> repositoryGet = params.isEmpty() ?
              repository.getAll() :
              repository.findByQuery(params);

            return repositoryGet.flatMap(
              dbResult -> Future.of(getHttpExecutionService(), () ->
                dbResult.fold(error -> {
                    // Logger.of("application").warn("Error consultado " + repository.getEntityName(), error);
                    return Results.status(
                      Http.Status.CONFLICT,
                      Error.getError(
                        "Error consultando.", error));
                  },
                  listItems -> ok(ItemJson.generateJson(repository.getEntityName(), listItems))
                )));
          }
        });

    return response
      .recover(ex -> {
        Logger.of("application").warn("Excepción en la consulta a todos. entityName: " + repository.getEntityName() + ", userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error consultado", ex));
      })
      .toCompletableFuture();
  }


  /**
   * @param id
   * @return
   */
  @With({AuthAction.class})
  public CompletionStage<Result> getOne(Long id, Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    Future<Result> response =
      checkPermission(Permission.LEER, userInfo.groupId)
        .flatMap(access -> {
          if (access.isDefined()) {
            return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
          } else {
            return repository.findById(id).flatMap(
              dbResult -> {
                return Future.of(getHttpExecutionService(), () ->
                  dbResult.fold(
                    error -> ok(),
                    item -> ok(ItemJson.generateJson(repository.getEntityName(), item))
                  ));
              });
          }
        });

    return response
      .recover(ex -> {
        Logger.of("application").warn("Excepción en la consulta a uno. entityName: " + repository.getEntityName() + ", userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error consultado", ex));
      })
      .toCompletableFuture();
  }


  /**
   * @return
   */
  @With({AuthAction.class})
  @BodyParser.Of(BodyParser.TolerantJson.class)
  public CompletionStage<Result> add(Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    final JsonNode json = request.body().asJson();
    Future<Result> response =
      checkPermission(Permission.ESCRIBIR, userInfo.groupId)
        .flatMap(access -> {
          if (access.isDefined()) {
            return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
          } else {
            return Try
              .of(() -> Json.fromJson(json, repository.getTypeClass()))
              .toEither().mapLeft(Throwable::getLocalizedMessage)
              .fold(errorJs ->
                  Future.successful(notAcceptable(
                    Error.getError("Error al pasar de Json a Objeto", errorJs)
                  )),
                itemJs -> {
                  if (itemJs instanceof ArchivoModel) {
                    String ip = request.header("X-Forwarded-For").orElse(null);
                    if (ip.isEmpty()) {
                      return Future.successful(notAcceptable(
                        Error.getError("Error al capturar los datos", "No se halló el IP")
                      ));
                    }
                    ((ArchivoModel) itemJs).setIp(ip);
                    ((ArchivoModel) itemJs).setIdUsuarioEdita(userInfo.userId);
                    ((ArchivoModel) itemJs).setFechaEdita(LocalDate.now());
                  }

                  return repository.insert(itemJs).flatMap(
                    dbResult -> Future.of(getHttpExecutionService(), () ->
                      dbResult.fold(
                        error -> {
                          // Logger.of("application").warn("Error creando " + repository.getEntityName(), error);
                          return Results.status(
                            Http.Status.CONFLICT,
                            Error.getError(
                              "Error creando", error));
                        },
                        item -> created(ItemJson.generateJson(repository.getEntityName(), item))))
                  );
                });
          }
        });

    return response
      .recover(ex -> {
        Logger.of("application").warn("Excepción en la creación. entityName: " + repository.getEntityName() + ", userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error creando", ex));
      })
      .toCompletableFuture();
  }


  /**
   * @param id
   * @return
   */
  @With({AuthAction.class})
  @BodyParser.Of(BodyParser.TolerantJson.class)
  public CompletionStage<Result> patch(Long id, Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    final JsonNode json = request.body().asJson();
    Future<Result> response =
      checkPermission(Permission.ESCRIBIR, userInfo.groupId)
        .flatMap(access -> {
          if (access.isDefined()) {
            return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
          } else {
            return Try
              .of(() -> Json.fromJson(json, repository.getTypeClass()))
              .toEither().mapLeft(Throwable::getLocalizedMessage)
              .fold(errorJs ->
                  Future.successful(notAcceptable(
                    Error.getError("Error al pasar de Json a Objeto", errorJs)
                  )),
                itemJs -> {
                  if (itemJs instanceof ArchivoModel) {
                    String ip = request.header("X-Forwarded-For").orElse(null);
                    if (ip.isEmpty()) {
                      return Future.successful(notAcceptable(
                        Error.getError("Error al capturar los datos", "No se halló el IP")
                      ));
                    }
                    ((ArchivoModel) itemJs).setIp(ip);
                    ((ArchivoModel) itemJs).setIdUsuarioEdita(userInfo.userId);
                    ((ArchivoModel) itemJs).setFechaEdita(LocalDate.now());
                  }

                  return repository.update(id, itemJs).flatMap(
                    dbResult -> Future.of(getHttpExecutionService(), () ->
                      dbResult.fold(
                        error -> {
                          // Logger.of("application").warn("Error actualizando " + repository.getEntityName(), error);
                          return Results.status(
                            Http.Status.CONFLICT,
                            Error.getError(
                              "Error actualizando", error));
                        },
                        item ->
                          Results.status(
                            Http.Status.ACCEPTED,
                            ItemJson.generateJson(repository.getEntityName(), item))))
                  );
                });
          }
        });

    return response
      .recover(ex -> {
        Logger.of("application").warn("Excepción en la actualización. entityName: " + repository.getEntityName() + ", userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error actualizando", ex));
      })
      .toCompletableFuture();
  }


  /**
   * @param id
   * @return
   */
  @With({AuthAction.class})
  public CompletionStage<Result> remove(Long id, Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);

    Future<Result> response =
      checkPermission(Permission.BORRAR, userInfo.groupId)
        .flatMap(access -> {
          if (access.isDefined()) {
            return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
          } else {
            return repository.delete(id).flatMap(
              dbResult -> Future.of(getHttpExecutionService(), () ->
                dbResult.fold(
                  error -> {
                    // Logger.of("application").warn("Error eliminando " + repository.getEntityName(), error);
                    return Results.status(
                      Http.Status.CONFLICT,
                      Error.getError(
                        "Error eliminando", error));
                  },
                  allOkay -> {
                    ObjectNode js = Json.newObject().put("id", id);
                    return Results.status(Http.Status.ACCEPTED, js);
                  })
              ));
          }
        });

    return response
      .recover(ex -> {
        Logger.of("application").warn("Excepción en la eliminación. entityName: " + repository.getEntityName() + ", userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error eliminando", ex));
      })
      .toCompletableFuture();
  }

  /**
   * @return
   */
  @With({AuthAction.class})
  public CompletionStage<Result> getExcel(Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    final Map<String, String[]> params = request.queryString();

    Future<Result> response =
      checkPermission(Permission.EXPORTAR, userInfo.groupId)
        .flatMap(access -> {
          if (access.isDefined()) {
            return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
          } else {
            // Caso cuando se mandan queryParams.
            final Source<U, ?> repositoryGetStream = params.isEmpty() ?
              repository.getAllStream() :
              repository.findByQueryStream(params);

            final Source<Try<byte[]>, ?> bytesExcel = repositoryGetStream
              .groupedWithin(Integer.MAX_VALUE, FiniteDuration.create(5, TimeUnit.MINUTES))
              .map(itemSeq -> ExcelGenerator.generateExcel(List.ofAll(itemSeq), excelNamer));

//            final Source<ByteString, ?> streamResponse = bytesExcel.take(1).map(ByteString::fromArray);

            final CompletionStage<Try<byte[]>> futureResponse =
              bytesExcel.runReduce((Try<byte[]> a, Try<byte[]> b) -> a, materializer);

            return Future.fromCompletableFuture(getHttpExecutionService(), futureResponse.toCompletableFuture())
              .map(leTry -> leTry.map(bytes -> ok(bytes).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                  .withHeader("Content-Disposition", "attachment; filename=Reporte-" + repository.getEntityName() + ".xlsx"))
                .getOrElseGet(ex -> internalServerError(Error.getError("Error generando excel", ex)))
              );
          }
        });

    return response
      .recover(ex -> {
        Logger.of("application").warn("Excepción en la consulta en stream. entityName: " + repository.getEntityName() + ", userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error consultado", ex));
      })
      .toCompletableFuture();
  }

  /**
   * @return
   */
  @With({AuthAction.class})
  @BodyParser.Of(BodyParser.TolerantJson.class)
  public CompletionStage<Result> getExcel2(Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    final JsonNode json = request.body().asJson();

    Future<Result> response =
      checkPermission(Permission.EXPORTAR, userInfo.groupId)
        .flatMap(access -> {
          if (access.isDefined()) {
            return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
          } else {
            final Try<IdsReader> jsonTry = Try
              .of(() -> Json.fromJson(json, IdsReader.class));

            final Try<Iterator<String[]>> idsGroupedTry = jsonTry.map(idsObj -> List
              .of(idsObj.ids)
              .grouped(1000)
              .map(ids -> ids.toJavaArray(String.class))
            );

            String columnQuery = jsonTry.toOption()
              .flatMap(idsReader -> idsReader.attribute)
              .getOrElse("id");


            final Future<Seq<List<U>>> listOfLists = Future.fromTry(idsGroupedTry).flatMap(itIds -> {
                Iterator<Future<List<U>>> groupF = itIds.map(ids -> repository
                  .findByQuery(Collections.singletonMap(columnQuery, ids))
                  .map(ei -> ei.get()));
                return Future.sequence(groupF);
              }
            );

            final Future<List<U>> itemListF = listOfLists.map(lista ->
              lista.tail().foldLeft(lista.head(), List::appendAll)
            );

            final Future<byte[]> fileF = itemListF.flatMap(itemSeq ->
              Future.fromTry(ExcelGenerator.generateExcel(itemSeq, excelNamer))
            );

            return fileF
              .map(bytes -> ok(bytes).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .withHeader("Content-Disposition", "attachment; filename=Reporte-" + repository.getEntityName() + ".xlsx"));
          }
        });

    return response
      .recover(ex -> {
        Logger.of("application").warn("Excepción en la consulta en stream. entityName: " + repository.getEntityName() + ", userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error consultado", ex));
      })
      .toCompletableFuture();
  }

  @With({AuthAction.class})
  public CompletionStage<Result> getAllPaginated(Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    final io.vavr.collection.HashMap<String, String[]> params = io.vavr.collection.HashMap.ofAll(request.queryString());
    Option<Integer> pageOption = params.get("page").map(page ->
      Try.of(() -> Integer.parseInt(page[0])).getOrElse(0)
    );
    Option<Integer> pageSizeOption = params.get("pageSize").map(pageSize ->
      Try.of(() -> Integer.parseInt(pageSize[0])).getOrElse(DEFAULT_PAGE_SIZE)
    );
    Map<String, String[]> queryParams = params.filterKeys(key -> !"page".equals(key) && !"pageSize".equals(key)).toJavaMap();
    Future<Result> response =
      checkPermission(Permission.LEER, userInfo.groupId)
        .flatMap(access -> {
          if (access.isDefined()) {
            return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
          } else {
            final Future<Either<String, Page<U>>> repositoryGet = repository.getAllPaginated(pageOption.getOrElse(0),
              pageSizeOption.getOrElse(DEFAULT_PAGE_SIZE),
              queryParams);

            return repositoryGet.flatMap(
              dbResult -> Future.of(getHttpExecutionService(), () ->
                dbResult.fold(error -> {
                    // Logger.of("application").warn("Error consultado " + repository.getEntityName(), error);
                    return Results.status(
                      Http.Status.CONFLICT,
                      Error.getError(
                        "Error consultando.", error));
                  },
                  page -> ok(Json.toJson(page))
                )));
          }
        });

    return response
      .recover(ex -> {
        Logger.of("application").warn("Excepción en la consulta a todos. entityName: " + repository.getEntityName() + ", userInfo: " + userInfo, ex);
        return internalServerError(
          Error.getError("Error consultado", ex));
      })
      .toCompletableFuture();
  }
  @With({AuthAction.class})
  public CompletionStage<Result> count(Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    Future<Result> response = checkPermission(Permission.LEER, userInfo.groupId)
      .flatMap(access -> {
        if(access.isDefined()) {
          return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
        } else {
          return this.repository.count()
            .map(count -> {
              ObjectNode object = Json.newObject();
              object.put(repository.getEntityName(), count);
              return ok(object);
            });
        }
      });

    return response
      .recover(ex -> {
        Logger.of("application").warn("Excepción al realizar count. entityName: " + repository.getEntityName(), ex);
        return internalServerError(
          Error.getError("Error consultado", ex));
      })
      .toCompletableFuture();
  }
}

/**
 * Clase auxiliar para cuando se quiere hacer descargas de múchos ids por excel.
 */
class IdsReader {
  public String[] ids;
  public Option<String> attribute = Option.none();
}