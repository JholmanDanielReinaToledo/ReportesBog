package repository.seguridad;

import models.seguridad.Grupo;
import play.db.ebean.EbeanConfig;
import utilities.database.DatabaseExecutionContext;
import utilities.generic.GenericRepository;

import javax.inject.Inject;

public class GrupoRepository extends GenericRepository<Grupo> {

  @Inject
  public GrupoRepository(
    EbeanConfig ebeanConfig,
    DatabaseExecutionContext databaseExecutionContext
  ) {
    super(ebeanConfig, databaseExecutionContext, Grupo.class, "grupo");
    super.idNameDB = "ID";
  }
}