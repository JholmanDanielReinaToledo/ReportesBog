package controllers.dominios;

import io.vavr.collection.List;
import models.dominios.ActividadEconomicaTipologica;
import play.libs.concurrent.HttpExecutionContext;
import repository.dominios.ActividadEconomicaTipologicaRepository;
import repository.seguridad.AccessRepository;
import utilities.generic.GenericController;
import utilities.security.Modulo;

import javax.inject.Inject;

public class ActividadEconomicaTipologicaController extends GenericController<ActividadEconomicaTipologica> {
  @Inject
  public ActividadEconomicaTipologicaController(
    ActividadEconomicaTipologicaRepository repository,
    HttpExecutionContext httpExecutionContext,
    AccessRepository accessRepository
  ) {
    super(repository, httpExecutionContext, accessRepository);
    super.modulosAsociados = List.of(Modulo.solo_lectura, Modulo.admin_dominios_social, Modulo.admin_dominios_regulaciones)
      .map(Modulo::getId);
  }
}
