package utilities.generic;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.Model;
import io.vavr.collection.Iterator;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import akka.stream.javadsl.Source;
import io.vavr.control.Option;
import io.vavr.control.Try;
import models.DominioItem;
import models.ReporteByItem;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import repository.seguridad.AccessRepository;
import utilities.excel.ExcelGenerator;
import utilities.excel.ExcelGeneratorFromMap;
import utilities.json.Error;
import utilities.security.AuthAction;
import utilities.security.Permission;
import utilities.security.UserInfoContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CompletionStage;

public class GenericControllerReportesWithItem<U extends Model & ReporteByItem, D extends Model & DominioItem> extends GenericController<U>{

  protected GenericRepositoryInterface<D> repositoryDominioItems;

  public GenericControllerReportesWithItem(
    GenericRepositoryInterface<U> repository,
    GenericRepositoryInterface<D> repositoryDominioItems,
    HttpExecutionContext httpExecutionContext,
    AccessRepository accessRepository
  ) {
    super(repository, httpExecutionContext, accessRepository);
    this.repositoryDominioItems = repositoryDominioItems;
  }

  Future<HashMap<Long, String>> getMapItem() {
    return repositoryDominioItems.getAll()
      .map(dbResult -> dbResult.getOrElse(List::of))
      .flatMap(lists -> Future.of(() -> Option.of(new HashMap<Long, String>())
          .map(myMap -> {
            lists.forEach((dominio) -> {
              myMap.put(dominio.getId(), dominio.getDescripcion());
            });
            return myMap;
          }).get())
      );
  }

  @Override
  @With({AuthAction.class})
  @BodyParser.Of(BodyParser.TolerantJson.class)
  public CompletionStage<Result> getExcel2(Http.Request request) {
    final UserInfoContext userInfo = request.attrs().get(AuthAction.userInfoKey);
    final JsonNode json = request.body().asJson();
    Future<Result> results =
      checkPermission(Permission.EXPORTAR, userInfo.groupId)
        .flatMap(access -> {
          if (access.isDefined()) {
            return Future.fromCompletableFuture(getHttpExecutionService(), access.toCompletableFuture());
          }

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

          return getMapItem().flatMap(
            (mapItems) -> {
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

              final Future<byte[]> fileF = itemListF.flatMap(itemSeq -> {
                Map<Long, List<U>> agrupadas = itemSeq.groupBy(u -> u.getIdAnalisis());
                Seq<HashMap<String, Object>> map = agrupadas.map((entry) -> {
                  U u1 = entry._2.get();
                  HashMap<String, Object> others = u1.getOthers();
                  mapItems.forEach((a, b) -> {
                    others.put(b, null);
                  });
                  entry._2.forEach(u -> {
                    String s = mapItems.get(u.getIdItem());
                    others.put(s, u.getCantidad());
                  });
                  return others;
                });
                  return Future.fromTry(ExcelGeneratorFromMap.generateExcel(map));
                }
              );
              return fileF
                .map(bytes -> ok(bytes).as("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                  .withHeader("Content-Disposition", "attachment; filename=Reporte-" + repository.getEntityName() + ".xlsx"));

            }
          );
        });

    return results.recover(ex -> {
      Logger.of("application").warn("Excepción en la consulta a todos. entityName: " + repository.getEntityName(), ex);
      return internalServerError(
        Error.getError("Error consultado", ex));
    }).toCompletableFuture();
  }
}
