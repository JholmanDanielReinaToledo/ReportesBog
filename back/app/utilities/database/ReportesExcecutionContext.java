package utilities.database;

import akka.actor.ActorSystem;
import play.db.ebean.EbeanDynamicEvolutions;

import javax.inject.Inject;

public class ReportesExcecutionContext extends ExcecutionContext {

  @Inject
  public ReportesExcecutionContext(
    ActorSystem actorSystem,
    EbeanDynamicEvolutions ebeanDynamicEvolutions
  ) {
    super("database.reportes-dispatcher", actorSystem, ebeanDynamicEvolutions);
  }
}
