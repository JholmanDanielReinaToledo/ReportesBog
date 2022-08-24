package utilities.generic;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import io.ebean.Model;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import utilities.database.Page;

import java.util.Map;

/**
 * Interface para poder implementar un repository en cualquier parte.
 * @param <U>
 */
public interface GenericRepositoryInterface<U extends Model> {
  String getEntityName();

  String getIdNameDB();

  Class<U> getTypeClass();

  Future<Either<String, Boolean>> delete(Long id);

  Future<Either<String, U>> update(Long id, U item);

  Future<Either<String, U>> insert(U item);

  Future<Either<String, List<U>>> getAll();

  Future<Either<String, List<U>>> findByQuery(Map<String, String[]> queryParams);

  Future<Either<String, U>> findById(Long id);

  Source<U, NotUsed> getAllStream();

  Source<U, NotUsed> findByQueryStream(Map<String, String[]> queryParams);

  Future<Either<String, Page<U>>> getAllPaginated(Integer page, Integer pageSize, Map<String, String[]> queryParams);

  Future<Integer> count();
}
