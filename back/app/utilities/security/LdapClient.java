package utilities.security;


import com.typesafe.config.Config;
import io.vavr.API;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import play.Application;
import play.Logger;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import java.util.*;

public class LdapClient {

  private final String FQDN;
  private final String server;
  private final String username;
  private final String password;

  @javax.inject.Inject
  public LdapClient(Config config) {
//    Config config = app.config();
    FQDN = Try.of(() -> config
      .getString("SENA.secured.ldap.fqdn"))
      .getOrElse("medellin.gov.co");
    server = Try.of(() -> config
      .getString("SENA.secured.ldap.server"))
      .getOrElse("10.1.225.1");
    username = Try.of(() -> config
      .getString("SENA.secured.ldap.principal"))
      .getOrElse("servicios.docker");
    password = Try.of(() -> config
      .getString("SENA.secured.ldap.password"))
      .getOrElse("medellin2018*");


  }

  private Either<String, DirContext> getContext(String username, String password) {
    final Hashtable<String, String> ldapEnv = new Hashtable<>(11);
    ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    ldapEnv.put(Context.PROVIDER_URL, "ldap://" + server + ":389/" + toDC(FQDN));
    ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
    ldapEnv.put(Context.SECURITY_PRINCIPAL, username + "@" + FQDN);
    ldapEnv.put(Context.SECURITY_CREDENTIALS, password);
    ldapEnv.put("com.sun.jndi.ldap.connect.timeout", "5000");
    // ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
    // ldapEnv.put(Context.SECURITY_PROTOCOL, "simple");
    return Try.of(() -> new InitialDirContext(ldapEnv))
      .onFailure(ex -> Logger.of("application").debug("[LdapClient][getContext] Error LDAP.", ex))
      .toEither().mapLeft(Throwable::getLocalizedMessage)
      .map(context -> context);
  }

  private Either<String, DirContext> getContext() {
    return getContext(username + "," + toDC(FQDN), password);
  }

  private Either<String, NamingEnumeration<SearchResult>> search(String searchFilter, String[] attrs) {
    final SearchControls controls = new SearchControls();
    controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    controls.setReturningAttributes(attrs);

    return getContext().flatMap(context ->
      Try.of(() -> {
        final NamingEnumeration<SearchResult> renum = context.search("", searchFilter, controls);
        context.close();
        return renum;
      })
        .onFailure(ex -> Logger.of("application").debug("[LdapClient][search] Error LDAP.", ex))
        .toEither().mapLeft(Throwable::getLocalizedMessage)
    );
  }

  private Either<String, NamingEnumeration<SearchResult>> search(String ldapFilter) {
    String returnedAtts[] = new String[]{
      "sAMAccountName",
      "userPrincipalName",
      "givenName",
      "sn",
      "title",
      "name",
      "cn"
    };
    return search(ldapFilter, returnedAtts);
  }

  public Either<String, LDAPUser> getUserDetail(String username) {
    final Either<String, NamingEnumeration<SearchResult>> tryRenum =
      search("(&(objectClass=person)(|(userPrincipalName=" + username + ")(sAMAccountName=" + username + ")))");

    final Either<String, SearchResult> trySearch =
      tryRenum.flatMap(renum -> !renum.hasMoreElements() ?
        Either.left("No se encontró al usuario '$queryUsername'") :
        Try.of(() -> {
          final SearchResult theNext = renum.next();
          renum.close();
          return theNext;
        })
          .onFailure(ex -> Logger.of("application").debug("[LdapClient][getUserDetail] Error LDAP.", ex))
          .toEither().mapLeft(Throwable::getLocalizedMessage)
      );

    final Either<String, List<Tuple2<String, Option<String>>>> tryAttList =
      trySearch.map(searchResult -> {
        final Attributes attributes = searchResult.getAttributes();
        return List.ofAll(Collections.list(attributes.getIDs()))
          .map(id -> {
            final Option<Attribute> att = Option.of(attributes.get(id));
            final Option<String> value = att.flatMap(a ->
              Try.of(() -> a.get())
                .toOption()
                .map(i -> (String) i)
            );
            return Tuple.of(id, value);
          });
      });

    return tryAttList.flatMap(attList -> API.For(
      attList.find(tupla -> tupla._1.equals("sAMAccountName")).map(i -> i._2),
      attList.find(tupla -> tupla._1.equals("userPrincipalName")).map(i -> i._2),
      attList.find(tupla -> tupla._1.equals("givenName")).map(i -> i._2),
      attList.find(tupla -> tupla._1.equals("sn")).map(i -> i._2),
      attList.find(tupla -> tupla._1.equals("title")).map(i -> i._2),
      attList.find(tupla -> tupla._1.equals("name")).map(i -> i._2),
      attList.find(tupla -> tupla._1.equals("cn")).map(i -> i._2)
    ).yield(LDAPUser::new)
      .toEither("No se pudo construir el objeto LDAPUser"));
  }

  private String toDC(String domainName) {
    return List.of(domainName.split("."))
      .map(str -> "DC=" + str)
      .mkString(",");
  }

  public Either<String, Boolean> authenticate(String username, String password) {
    if (username == null || username.isEmpty() || password == null || password.isEmpty())
      return Either.left("Clave o usuario vacíos");

    return getContext(username, password).flatMap(context ->
      Try.of(() -> {
        context.close();
        return true;
      })
        .onFailure(ex -> Logger.of("application").debug("[LdapClient][authenticate] Error LDAP.", ex))
        .toEither().mapLeft(Throwable::getLocalizedMessage)
    );
  }
}

final class LDAPUser {
  public Option<String> sAMAccountName; // Cuenta de usuario
  public Option<String> userPrincipalName; // Correo
  public Option<String> givenName; // Nombres
  public Option<String> sn; // Apellidos
  public Option<String> title;
  public Option<String> name;
  public Option<String> cn;

  public LDAPUser(
    Option<String> sAMAccountName, // cuenta de usuario
    Option<String> userPrincipalName, // Correo
    Option<String> givenName, // Nombres
    Option<String> sn, // Apellidos
    Option<String> title,
    Option<String> name,
    Option<String> cn
  ) {
    this.sAMAccountName = sAMAccountName;
    this.userPrincipalName = userPrincipalName;
    this.givenName = givenName;
    this.sn = sn;
    this.title = title;
    this.name = name;
    this.cn = cn;
  }
}
