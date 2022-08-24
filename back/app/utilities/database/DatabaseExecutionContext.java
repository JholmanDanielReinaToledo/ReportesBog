package utilities.database;

import akka.actor.ActorSystem;
import play.db.ebean.EbeanDynamicEvolutions;
import play.libs.concurrent.CustomExecutionContext;
import utilities.scala.ExecutionContextExecutorServiceBridge;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;

/**
 * Custom execution context, so that blocking database operations don't
 * happen on the rendering thread pool.
 * <p>
 * link https://www.playframework.com/documentation/latest/ThreadPools
 */
public class DatabaseExecutionContext extends ExcecutionContext {

  @Inject
  public DatabaseExecutionContext(
    ActorSystem actorSystem,
    EbeanDynamicEvolutions ebeanDynamicEvolutions
  ) {
    super("database.dispatcher", actorSystem, ebeanDynamicEvolutions);
  }

}
