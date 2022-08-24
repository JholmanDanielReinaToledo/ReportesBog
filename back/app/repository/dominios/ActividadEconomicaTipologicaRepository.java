package repository.dominios;

import models.dominios.ActividadEconomicaTipologica;
import play.db.ebean.EbeanConfig;
import utilities.database.DatabaseExecutionContext;
import utilities.generic.GenericRepository;

import javax.inject.Inject;

public class ActividadEconomicaTipologicaRepository extends GenericRepository<ActividadEconomicaTipologica> {

  @Inject
  public ActividadEconomicaTipologicaRepository(
    EbeanConfig ebeanConfig,
    DatabaseExecutionContext databaseExecutionContext
  ) {
    super(ebeanConfig, databaseExecutionContext, ActividadEconomicaTipologica.class, "actividadEconomicaTipologica");
    super.idNameDB = "ID";
  }
}