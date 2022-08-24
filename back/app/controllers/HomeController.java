package controllers;


//import play.libs.concurrent.HttpExecutionContext;

import play.mvc.*;
import utilities.json.Error;
import utilities.security.AuthAction;

//import javax.inject.Inject;
//import java.util.concurrent.CompletionStage;

/**
 * Home controller
 */
public class HomeController extends Controller {

//  private final HttpExecutionContext httpExecutionContext;
//  @Inject
//  public HomeController(
//    HttpExecutionContext httpExecutionContext
//  ) {
//    this.httpExecutionContext = httpExecutionContext;
//  }

  /**
   * Handle default path requests, redirect to computers list
   */
  public Result index(String anyPath) {
    return ok(views.html.index.render());
  }

  @With({AuthAction.class})
  public Result apiNotImplemented(String wrongPath) {
    return Results.status(
      Http.Status.NOT_IMPLEMENTED,
      Error.getError(
        "Api no implementado",
        "El api '/api/" + wrongPath + "', no está implementado."));
  }


  public Result eventsourceTest() {
    return ok(views.html.eventsource.render());
  }
}

