package repository.seguridad;

import models.seguridad.UsuarioPublic;
import play.db.ebean.EbeanConfig;
import utilities.database.DatabaseExecutionContext;
import utilities.generic.GenericRepositoryReportes;

import javax.inject.Inject;

public class UsuarioPublicRepository extends GenericRepositoryReportes<UsuarioPublic> {

  @Inject
  public UsuarioPublicRepository(
    EbeanConfig ebeanConfig,
    DatabaseExecutionContext databaseExecutionContext
  ) {
    super(ebeanConfig, databaseExecutionContext, UsuarioPublic.class, "usuarioPublic");
  }
}
