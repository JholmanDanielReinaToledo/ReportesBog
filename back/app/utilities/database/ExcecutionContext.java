package utilities.database;

import akka.actor.ActorSystem;
import play.db.ebean.EbeanDynamicEvolutions;
import play.libs.concurrent.CustomExecutionContext;
import utilities.scala.ExecutionContextExecutorServiceBridge;

import java.util.concurrent.ExecutorService;
public abstract class ExcecutionContext extends CustomExecutionContext {
  /**
   * OJO, debido a un bug en la inyección de dependencias (una condición de carrera donde
   * la configuración no está disponible antes que se haga algún llamado a la base de datos),
   * y con base en sugerencias encontradas en un issue de github, toca inyectar la clase
   * ebeanDynamicEvolutions acá, aunque no la usamos realmente.
   * Solo está ahí para forzar la carga de la configuración.
   *
   * @param actorSystem
   * @param ebeanDynamicEvolutions
   */
  public ExcecutionContext(
    String dispatcherName,
    ActorSystem actorSystem,
    /*NO BORRAR*/ EbeanDynamicEvolutions ebeanDynamicEvolutions
  ) {
    super(actorSystem, dispatcherName);
  }

  /**
   * Convierte este ExcecutionContext en un ExecutorService.
   * Esto se usa para por ejemplo, poder usar el mismo pool con API de otras librerias, como vavr.
   *
   * @return
   */
  public ExecutorService getExecutorService() {
    return ExecutionContextExecutorServiceBridge.apply(this);
  }
}
