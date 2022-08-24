package repository.general;

import models.general.Direccion;
import play.db.ebean.EbeanConfig;
import utilities.database.DatabaseExecutionContext;
import utilities.generic.GenericRepository;

import javax.inject.Inject;

public class DireccionRepository extends GenericRepository<Direccion> {
    @Inject
    public DireccionRepository(
            EbeanConfig ebeanConfig,
            DatabaseExecutionContext databaseExecutionContext
    ) {
        super(ebeanConfig, databaseExecutionContext, Direccion.class, "direccion");
        super.idNameDB = "ID";
    }
}
