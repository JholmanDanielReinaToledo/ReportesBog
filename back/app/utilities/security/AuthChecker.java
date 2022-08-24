package utilities.security;

import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.resources.Configuration;
import me.gosimple.nbvcxz.resources.ConfigurationBuilder;
import me.gosimple.nbvcxz.resources.Feedback;
import me.gosimple.nbvcxz.scoring.Result;
import models.seguridad.Usuario;
import org.mindrot.jbcrypt.BCrypt;
import repository.seguridad.UsuarioRepository;

import javax.inject.Inject;
import java.util.Locale;

public class AuthChecker {

  final private UsuarioRepository userRepository;
  final private Nbvcxz nbvcxz;

  @Inject
  public AuthChecker(
    UsuarioRepository userRepository
  ) {
    this.userRepository = userRepository;

    Configuration configuration = new ConfigurationBuilder()
      .setMinimumEntropy(16.5)
      .setLocale(Locale.forLanguageTag("es"))
      .createConfiguration();
    this.nbvcxz = new Nbvcxz(configuration);
  }

  /**
   * Si el usuario no tiene clave dentro del sistema, usa el ldap.
   *
   * @param username
   * @param password
   * @return
   */
  public Future<Either<String, Usuario>> performAuthentication(String username, String password) {

    // Comprueba que el usuario esté activo primero.
    return userRepository.findByUsername(username)
      .map(userE -> userE
        .flatMap(user -> (user.esActivo && user.idGrupo != null) ? Either.right(user) : Either.left("Usuario no activado por el administrador.")))
      .flatMap(userE -> userE
        .fold(err -> Future.successful(Either.left(err)),
          user -> userRepository.getPasswordBcrypted(user.usuario)
            // Si hay una clave interna, la prueba
            .map(dbPassOpt -> dbPassOpt.isDefined() ?
              dbPassOpt.map(passHashed -> BCrypt.checkpw(password, passHashed))
                .getOrElse(false) ? Either.right(user) : Either.left("Clave o usuario incorrecto."): Either.left("Clave o usuario incorrecto.") )
        )
      );
  }

  public Either<String, String> validatePassThenBcrypt(String password) {
    Result passEstimation = nbvcxz.estimate(password);
    if (passEstimation.isMinimumEntropyMet()) {
      return Either.right(BCrypt.hashpw(password, BCrypt.gensalt()));
    } else {
      Feedback feedback = passEstimation.getFeedback();
      String msgFeedback = Option.of(feedback.getWarning())
        .map(List::of).getOrElse(List.empty())
        .appendAll(feedback.getSuggestion()).mkString("; ");
      return Either.left(msgFeedback);
    }
  }
}
