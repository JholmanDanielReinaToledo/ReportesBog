package repository.seguridad;

import models.seguridad.ModuloSistema;
import play.db.ebean.EbeanConfig;
import utilities.database.DatabaseExecutionContext;
import utilities.generic.GenericRepository;

import javax.inject.Inject;

public class ModuloSistemaRepository extends GenericRepository<ModuloSistema> {

  @Inject
  public ModuloSistemaRepository(
    EbeanConfig ebeanConfig,
    DatabaseExecutionContext databaseExecutionContext
  ) {
    super(ebeanConfig, databaseExecutionContext, ModuloSistema.class, "modulo");
    super.idNameDB = "ID";
  }
}
