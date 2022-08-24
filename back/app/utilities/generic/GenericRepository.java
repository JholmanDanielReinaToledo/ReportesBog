package utilities.generic;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import io.ebean.*;
import io.vavr.Tuple;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import models.SimpleModel;
import play.Logger;
import play.db.ebean.EbeanConfig;
import utilities.database.Page;
import utilities.database.ExcecutionContext;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

/**
 * Clase abstracta para la creación de un repositorio para una clase en concreto.
 *
 * @param <U>
 */
public abstract class GenericRepository<U extends Model & SimpleModel> implements GenericRepositoryInterface<U> {

  protected final EbeanServer database;
  protected final ExcecutionContext executionContext;
  protected final ExecutorService executorService;
  protected final Class<U> typeClass;
  protected final String entityName;

  // Para los casos en que el nombre del ID sea diferente.
  protected String idNameDB = "ID";

  public GenericRepository(
    EbeanConfig ebeanConfig,
    ExcecutionContext executionContext,
    Class<U> typeClass,
    String entityName
  ) {
    this(ebeanConfig.defaultServer(), executionContext, typeClass, entityName);
  }

  public GenericRepository(
    String serverName,
    ExcecutionContext executionContext,
    Class<U> typeClass, String entityName
  ) {
    this.database = Ebean.getServer(serverName);
    this.executionContext = executionContext;
    this.executorService = executionContext.getExecutorService();
    this.typeClass = typeClass;
    this.entityName = entityName;
  }

  /**
   * @return
   */
  public String getEntityName() {
    return this.entityName;
  }

  /**
   * @return
   */
  public String getIdNameDB() {
    return this.idNameDB;
  }

  /**
   * @return
   */
  public Class<U> getTypeClass() {
    return this.typeClass;
  }

  /**
   * @param id
   * @return
   */
  public Future<Either<String, Boolean>> delete(Long id) {
    return Future.of(executorService,
      () -> {
        Either<String, Boolean> item = Option
          .ofOptional(database.createQuery(typeClass).setId(id).findOneOrEmpty())
          .toEither("Item no encontrado.")
          .map(i -> database.createQuery(typeClass).setId(id).delete() == 1)  // para garantizar la ejecución en la DB correcta
          .flatMap(b -> b ? Either.right(true) : Either.left("No se pudo eliminar el item."));
        item.swap().forEach(msg ->
          Logger.of("application").warn("[Repository][delete][" + entityName + "] -> " + msg));
        return item;
      }
    );
  }

  /**
   * @param id
   * @param item
   * @return
   */
  public Future<Either<String, U>> update(Long id, U item) {
    return Future.of(executorService,
      () -> Option
        .ofOptional(database.createQuery(typeClass).setId(id).findOneOrEmpty())
        .toEither("Item no encontrado.")
        .flatMap(u -> u.getId().equals(item.getId()) ?
          Either.right(item) : Either.left("El id del item no concuerda."))
        .flatMap(i ->
          Try.of(() -> {
              database.update(i);
              // i.update(); //ebeanServer.getName()
              Logger.of("application").debug("Se actualizó el item: " + item.toString());
              return i;
            }).onFailure(ex -> Logger.of("application").warn("[Repository][update][" + entityName + "] -> ", ex))
            .toEither().mapLeft(Throwable::getLocalizedMessage)));
  }

  /**
   * @param item
   * @return
   */
  public Future<Either<String, U>> insert(U item) {
    return Future.of(executorService,
      () -> Try.of(() -> {
                database.insert(item,database.currentTransaction());
                // System.out.println();
                Logger.of("application").debug("Se insertó el item: " + item.toString() + " con id " + item.getId());
                // return item;
          return item;
              }).onFailure(ex -> Logger.of("application").warn("[Repository][insert][" + entityName + "] -> ", ex))
              .toEither().mapLeft(Throwable::getLocalizedMessage)

    );
  }


  /**
   * @return
   */
  public Future<Either<String, List<U>>> getAll() {
    return Future.of(executorService, () ->
      Try.of(() -> List.ofAll(database.createQuery(typeClass).setMaxRows(10000).findList()))
        .onFailure(ex -> Logger.of("application").warn("[Repository][getAll][" + entityName + "] -> ", ex))
        .toEither().mapLeft(Throwable::getLocalizedMessage));
  }


  /**
   * @param queryParams
   * @return
   */
  public Future<Either<String, List<U>>> findByQuery(Map<String, String[]> queryParams) {
    final Query<U> myQuery = buildQuery(database.createQuery(typeClass), queryParams);
    return Future.of(executorService, () ->
      Try.of(() -> List.ofAll(myQuery.setMaxRows(10000).findList()))
        .onFailure(ex -> Logger.of("application").warn("[Repository][findByQuery][" + entityName + "] -> ", ex))
        .toEither().mapLeft(Throwable::getLocalizedMessage));
  }


  /**
   * @param id
   * @return
   */
  public Future<Either<String, U>> findById(Long id) {
    return Future.of(executorService,
      () -> Option
        .ofOptional(database.createQuery(typeClass).setId(id).findOneOrEmpty())
        .toEither("La consulta a la base de datos retornó vacía."));
  }


  //---------------------------------------------

  /**
   * Función que devuelve un Source de AkkaStreams, con todos los registros de la tabla.
   *
   * @return
   */
  public Source<U, NotUsed> getAllStream() {
    return Source.unfoldResource(
      () -> database.createQuery(typeClass).findIterate(),
      iterableQuery -> iterableQuery.hasNext() ? Optional.of(iterableQuery.next()) : Optional.empty(),
      QueryIterator::close
    );
  }

  public Source<U, NotUsed> findByQueryStream(Map<String, String[]> queryParams) {
    final Query<U> myQuery = buildQuery(database.createQuery(typeClass), queryParams);
    return Source.unfoldResource(
      myQuery::findIterate,
      iterableQuery -> iterableQuery.hasNext() ? Optional.of(iterableQuery.next()) : Optional.empty(),
      QueryIterator::close
    );
  }


  public Future<Either<String, Page<U>>> getAllPaginated(Integer page, Integer pageSize, Map<String, String[]> queryParams) {
    final Query<U> myQuery = buildQuery(database.createQuery(typeClass), queryParams)
      .setFirstRow(page * pageSize)
      .setMaxRows(pageSize);
    return Future.of(executorService, () ->
      Try.of(() -> {
          PagedList<U> pagedList = myQuery.findPagedList();
          pagedList.loadCount();
          List<U> list = List.ofAll(pagedList.getList());
          int totalCount = pagedList.getTotalCount();
          int totalPageCount = pagedList.getTotalPageCount();
          return new Page<>(list, totalCount, totalPageCount, this.entityName);
        })
        .onFailure(ex -> Logger.of("application").warn("[Repository][getAllPaginated][" + entityName + "] -> ", ex))
        .toEither().mapLeft(Throwable::getLocalizedMessage));
  }

  //---------------------------------------------

  /**
   * Construye una consulta estilo `WHERE (att1 in (val11, val12)) and (att2 in (val21, val22)) ...`
   *
   * @param tableQuery  Referencia de la tabla de consulta.
   * @param queryParams Map con los query params, como los trae el Controller de Play.
   * @return La consulta con las expresiones aplicadas.
   * <p>
   * el case de esta función se hace para evitar un "bug" que impedía el envío de booleanos al motor de
   * postgres por eso el case instanceof Boolean (nótese que no se usa el primitivo porque en los modelos no se usa)
   */
  protected Query<U> buildQuery(Query<U> tableQuery, Map<String, String[]> queryParams) {
    final ExpressionList<U> build = HashMap.ofAll(
        parseParams(queryParams)
      )
      .foldLeft(
        tableQuery.where(),
        (query, tuple) -> Match(tuple._2[0]).of(
          Case($(instanceOf(Boolean.class)), () -> query.and().add(Expr.eq(tuple._1, tuple._2[0]))),
          Case($(), () -> query.and().add(Expr.in(tuple._1, tuple._2)))
        )
      );
    return build.query();
  }

  protected Map<String, Object[]> parseParams(Map<String, String[]> queryParams) {

    return HashMap.ofAll(queryParams)
      .map((key, values) -> {
        Try<Object[]> map = Try.of(() -> typeClass.getField(key))
          .mapTry(field -> field.getType().getConstructor(String.class))
          .map(constructor ->
            List.of(values)
              .map(value ->
                Try.<Object>of(() -> constructor.newInstance(value))
                  .onFailure(throwable ->
                    Logger.of("application").error(String.format("Failed parse field: %s value: %s in typeClass: %s.\n", key, value, typeClass.getName()), throwable)
                  )
                  .getOrElse(value)
              ).toJavaArray())
          .onFailure(throwable -> Logger.of("application").error(String.format("Failed accessing field: %s in typeClass %s", key, typeClass.getName()), throwable));
        Object[] orElse = map.getOrElse(values);
        return Tuple.of(key, orElse);
      }).toJavaMap();
  }

  public Future<Boolean> deleteAll() {
    return Future.of(executorService,
      () -> Optional
        .ofNullable(typeClass.getAnnotation(Table.class))
        .map(table -> Tuple(table.name(), table.schema()))
        .map(tableInfo -> {
          CallableSql sql = database.createCallableSql(String.format("TRUNCATE %s.%s", tableInfo._2, tableInfo._1));
          return database.execute(sql) > 0;
        }).orElse(false)
    );
  }

  public Future<Integer> saveAll(List<U> entities) {
    Logger.of("application").info("Inicia Guardado " + entities.size() + " " + entityName);
    Instant start = Instant.now();
    return Future.of(executorService,
      () -> {
        int insertsCount = 0;
        try (Transaction transaction = database.beginTransaction()) {
          Connection connection = transaction.getConnection();
          Table annotationTable = typeClass.getAnnotation(Table.class);
          Array<Field> fields = Array.ofAll(Stream.of(typeClass.getFields()))
            .peek(field -> field.setAccessible(true))
            .filter(field -> field.getAnnotation(Id.class) == null && field.getAnnotation(Column.class) != null);

          Array<String> columns = fields.map(field -> field.getAnnotation(Column.class)).map(Column::name);

          String tableName = annotationTable.schema().concat(".").concat(annotationTable.name());
          String into = String.join(",", columns.toJavaList());
          String values = String.join(",", columns.map(column -> "?").toJavaList());

          String query = "INSERT INTO " + tableName + " (" + into + ") VALUES (" + values + ");";

          PreparedStatement preparedStatement = connection.prepareStatement(query);
          final int batchSize = 1000;
          int count = 0;
          for (U entity : entities) {
            count++;
            for (int i = 0; i < fields.size(); i++) {
              preparedStatement.setObject(i + 1, fields.get(i).get(entity));
            }
            preparedStatement.addBatch();
            if (count % batchSize == 0) {
              insertsCount += Array.ofAll(preparedStatement.executeBatch()).sum().intValue();
            }
          }
          insertsCount += Array.ofAll(preparedStatement.executeBatch()).sum().intValue();
          preparedStatement.close();
          transaction.commit();
        } catch (Exception exception) {
          Logger.of("application").error("Error saveAll", exception);
          throw new RuntimeException("Error saveAll: " + exception.getLocalizedMessage(), exception);
        } finally {
          Instant end = Instant.now();
          Duration between = Duration.between(start, end);
          Logger.of("application").info("Tiempo guardando " + insertsCount + " de " + entities.size() + " " + entityName + " " + (between.toMillis()/1000.0) + "s");
        }
        return insertsCount;
      });
  }

  public Future<Integer> count(){
    return Future.of(
      executorService,
      () -> database.createQuery(typeClass).findCount()
    );
  }
}
