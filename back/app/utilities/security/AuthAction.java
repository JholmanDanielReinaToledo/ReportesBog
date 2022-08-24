package utilities.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import play.Logger;
import play.libs.typedmap.TypedKey;
import play.mvc.Http;
import play.mvc.Result;
import utilities.json.Error;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Clase que se inyecta en un endpoint para comprobar el login.
 */
public class AuthAction extends play.mvc.Action.Simple {
  public static final  TypedKey<UserInfoContext > userInfoKey = TypedKey.<UserInfoContext>create("userInfo");
  private static final Try<Algorithm> baseAlgorithm = Try.of(() ->
    Algorithm.HMAC512("uvhA7TK<¿ZvH7`NVPMD1<d=lH`8s5A9N2?PiVNWq4H3Noqk`RXiE*E>6EuVjhKQA")); //ToDo
  private static final String ISSUER = "SISDEP";
  private static final long tokenLifetimeDays = 7;

  private static final String AuthTokenHeader = "x-access";

  /**
   * Crea un nuevo token basado en un userInfoContext
   *
   * @param info
   * @return
   */
  public static Either<String, String> createToken(UserInfoContext info) {
    final Date expiresTime =
      Date.from(
        LocalDate.now()
          .plusDays(tokenLifetimeDays)
          .atStartOfDay(ZoneId.systemDefault())
          .toInstant());

    return baseAlgorithm.map(
        algorithm ->
          JWT.create()
            .withIssuer(ISSUER)
            .withExpiresAt(expiresTime)
            .withClaim("userId", info.userId)
            .withClaim("username", info.username)
            .withClaim("ipAddress", info.ipAddress)
            .withClaim("groupId", info.groupId)
            .sign(algorithm))
      .toEither().mapLeft(Throwable::getLocalizedMessage);
  }

  /**
   * Lee un token, verificando que sí sea correcto, y genera el userInfoContext.
   *
   * @param token
   * @return
   */
  private Either<String, UserInfoContext> readToken(String token) {
    return baseAlgorithm
      .map(algorithm -> JWT.require(algorithm).withIssuer(ISSUER).build().verify(token))
      .map(decodedJWT ->
        new UserInfoContext(
          decodedJWT.getClaim("userId").asLong(),
          decodedJWT.getClaim("username").asString(),
          decodedJWT.getClaim("ipAddress").asString(),
          decodedJWT.getClaim("groupId").asLong()))
      .toEither().mapLeft(Throwable::getLocalizedMessage);
  }

  @Override
  public CompletionStage<Result> call(Http.Request request) {
    final Either<String, String> gettingToken =
      Option.ofOptional(request.header(AuthTokenHeader))
        .toEither("No se halló el token de sesión.");

    final Either<String, UserInfoContext> info = gettingToken.flatMap(this::readToken);

    return info
      .flatMap((UserInfoContext userInfoContext) -> request.remoteAddress().equals(userInfoContext.ipAddress) ?
        Either.right(userInfoContext) : Either.left("La dirección de acceso no corresponde a la de login."))
      .map(userInfoContext -> delegate.call(request.addAttr(userInfoKey, userInfoContext)))
      .getOrElseGet(error ->
        CompletableFuture.completedFuture(
          unauthorized(Error.getError("Sin acceso", error))));
  }
}
