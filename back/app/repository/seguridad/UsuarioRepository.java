package repository.seguridad;

import io.ebean.SqlUpdate;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import models.seguridad.Usuario;
import play.db.ebean.EbeanConfig;
import utilities.database.DatabaseExecutionContext;
import utilities.generic.GenericRepository;

import javax.inject.Inject;
import javax.persistence.Table;

public class UsuarioRepository extends GenericRepository<Usuario> {

  @Inject
  public UsuarioRepository(
    EbeanConfig ebeanConfig,
    DatabaseExecutionContext databaseExecutionContext
  ) {
    super(ebeanConfig, databaseExecutionContext, Usuario.class, "usuario");
  }

  /**
   * Permite buscar un usuario por su nombre de usuario en el sistema.
   * <p>
   * Esto es usado en el Login antes de validar la clave en el LDAP.
   *
   * @param username
   * @return
   */
  public Future<Either<String, Usuario>> findByUsername(String username) {
    System.out.println(username);
    return Future.of(executorService,
      () -> Option
        .ofOptional(
          database.createQuery(typeClass)
            .where().eq("identificacion", username)
            .findOneOrEmpty()
        ).toEither("Credenciales inválidas."));
  }

  public Future<Option<Usuario>> findByEmail(String email) {
    return Future.of(executorService,
      () -> Option
        .ofOptional(
          database.createQuery(typeClass)
            .where().eq("email", email)
            .findOneOrEmpty()
        ));
  }

  /**
   * @param username
   * @return
   */
  public Future<Option<String>> getPasswordBcrypted(String username) {
    return Future.of(executorService,
      () -> Option.of(typeClass.getAnnotation(Table.class))
        .map(annotation -> {
          String sql = "select password from " + annotation.schema() + "." + annotation.name() + " where identificacion = :username";
          return database.sqlQuery(sql)
            .setParameter("username", username)
            .setTimeout(5).findOne();
        })
        .flatMap(result -> Option.of(result.getString("password")))
    );
  }

  /**
   * @param id
   * @param passBcrypted
   * @return
   */
  public Future<Either<String, Boolean>> updatePassword(Long id, String passBcrypted) {
    return Future.of(executorService,
      () -> Option.of(typeClass.getAnnotation(Table.class))
        .map(annotation -> {
          String sql = "update " + annotation.schema() + "." + annotation.name() + " set password=:pass where id = :id";
          return database.sqlUpdate(sql)
            .setParameter("pass", passBcrypted)
            .setParameter("id", id);
        }).toTry()
        .flatMap((SqlUpdate update) -> Try.of(update::execute))
        .toEither().mapLeft(Throwable::getLocalizedMessage)
        .map(numRows -> numRows > 0)
    );
  }
}
