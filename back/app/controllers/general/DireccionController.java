package controllers.general;

import io.vavr.collection.List;
import models.general.Direccion;
import org.bouncycastle.math.raw.Mod;
import play.libs.concurrent.HttpExecutionContext;
import repository.general.DireccionRepository;
import repository.seguridad.AccessRepository;
import utilities.generic.GenericController;
import utilities.security.Modulo;

import javax.inject.Inject;

public class DireccionController extends GenericController<Direccion> {
    @Inject
    public DireccionController(
            DireccionRepository repository,
            HttpExecutionContext httpExecutionContext,
            AccessRepository accessRepository
    ) {
        super(repository, httpExecutionContext, accessRepository);
        super.modulosAsociados = List.of(
            Modulo.solo_lectura,
            Modulo.pev_registro_persona,
            Modulo.social_registro_ventero,
            Modulo.personas,
            Modulo.regulaciones_modulos,
            Modulo.regulaciones_autorizacion
            )
                .map(Modulo::getId);
    }
}
