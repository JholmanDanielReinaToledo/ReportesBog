package utilities.mail;

import io.vavr.concurrent.Future;
import play.Logger;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

import javax.inject.Inject;

/**
 * https://github.com/playframework/play-mailer
 */
public class MailerService {
  @Inject
  MailerClient mailerClient;

  public static String BASE_HOST = "https://sisdep.geosat.com.co/";
  public final String HOST = BASE_HOST + "sisdep";

  public void sendEmail(
    String subject,
    String nameTo,
    String emailTo,
    String textBody,
    String htmlBody
  ) {
    Email email = new Email()
      .setSubject(subject)
      .setFrom("Sistema de Información Subsecretaría de la Defensa del Espacio Público - SISDEP <sisdep@gmail.com>")
      .addTo(nameTo + "<" + emailTo + ">")
      .setBodyText(textBody)
      .setBodyHtml(htmlBody);
    Logger.of("application").info("[MailerService][sendEmail] Enviando email al correo: " + emailTo);
    mailerClient.send(email);
  }

  public void sendEmailAsync(
    String subject,
    String nameTo,
    String emailTo,
    String textBody,
    String htmlBody
  ) {
    Future.of(() -> {
      sendEmail(subject, nameTo, emailTo, textBody, htmlBody);
      return true;
    });
  }
}
