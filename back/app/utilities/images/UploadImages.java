package utilities.images;


import com.typesafe.config.Config;
import io.vavr.control.Either;
import io.vavr.control.Try;

import play.Logger;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.ByteArrayOutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;


public class UploadImages {

  private final String baseFolder;
  private final String baseUrl;

  @Inject
  UploadImages(Config config) {
    this.baseFolder = Try.of(() -> config
        .getString("sisdep.secured.smb.baseFolder"))
      .getOrElse("/IMAGENES");
    this.baseUrl = Try.of(() -> config
        .getString("sisdep.secured.smb.baseUrl"))
      .getOrElse("https://sisdep.geosat.com.co/");
  }


  /**
   * https://stackoverflow.com/questions/8972357/manipulate-an-image-without-deleting-its-exif-data
   * https://stackoverflow.com/questions/17108234/setting-jpg-compression-level-with-imageio-in-java
   *
   * @param img
   * @return
   */
  public Either<String, byte[]> recompress(java.io.File img) {

    ImageReader reader = ImageIO.getImageReadersBySuffix("jpg").next();

    ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
    ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
    jpgWriteParam.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
    jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    jpgWriteParam.setCompressionQuality(0.8f);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    Try<IIOImage> lectura = Try.of(() -> {
      reader.setInput(ImageIO.createImageInputStream(img));
      IIOMetadata metadata = reader.getImageMetadata(0);
      BufferedImage imagen = reader.read(0);
      reader.dispose();
      return new IIOImage(imagen, null, metadata);
    });

    Try<byte[]> escritura = lectura.flatMap(metaImagen -> Try.of(() -> {
      jpgWriter.setOutput(new MemoryCacheImageOutputStream(outputStream));
      jpgWriter.write(null, metaImagen, jpgWriteParam);
      jpgWriter.dispose();
      outputStream.flush();
      byte[] bytesSalida = outputStream.toByteArray();
      outputStream.close();
      Logger.of("application").debug("Recomprimiendo imagen.");
      return bytesSalida;
    }));

    return escritura.toEither().mapLeft(ex -> {
      Logger.of("application").warn("[Error][UploadImages.recompress()] - ", ex);
      return "Error comprimiendo imagen. " + ex.getLocalizedMessage();
    });
  }

  /**
   * @param bytesFile
   * @param uriImage
   * @param fileName
   * @return
   */
  public Either<String, URL> saveImage(byte[] bytesFile, String uriImage, String fileName) {

    final Try<Path> pathFile = Try.of(() -> Paths.get(baseFolder, uriImage, fileName + ".jpg"));
    final Try<URL> urlPath = Try.of(() -> new URL(baseUrl + "/" + uriImage + "/" + fileName + ".jpg"));

    Try<Path> folderPath = pathFile.flatMap(path -> Try.of(() -> Files.createDirectories(path.getParent())));
    Try<Path> savedPath = folderPath.flatMap(folder -> pathFile.flatMap(path -> Try.of(() -> Files.write(path, bytesFile))));
    Try<URL> urlImage = savedPath.flatMap(p -> urlPath.flatMap(url -> Try.of(() -> new URL(url.toString()))));

    return urlImage.toEither().mapLeft(ex -> {
      Logger.of("application").warn("[Error][UploadImages.saveImage()] - ", ex);
      return "Error guardando imagen. " + ex.getLocalizedMessage();
    });
  }

  /**
   * @param uriImage
   * @param fileName
   * @return
   */
  public Either<String, URL> deleteImage(String uriImage, String fileName) {

    final Try<Path> pathFile = Try.of(() -> Paths.get(baseFolder, uriImage, fileName + ".jpg"));
    final Try<URL> urlPath = Try.of(() -> new URL(baseUrl + "/" + uriImage + "/" + fileName + ".jpg"));

    Try<Boolean> deleted = pathFile.flatMap(path -> Try.of(() -> Files.deleteIfExists(path)));

    Try<URL> urlImage = deleted.flatMap(d -> urlPath.flatMap(url -> Try.of(() -> new URL(url.toString()))));

    return urlImage.toEither().mapLeft(ex -> {
      Logger.of("application").warn("[Error][UploadImages.deleteImage()] - ", ex);
      return "Error eliminando imagen. " + ex.getLocalizedMessage();
    });
  }

  /**
   * @param uriImageFrom
   * @param uriImageTo
   * @param fileName
   * @return
   */
  public Either<String, URL> moveImage(String uriImageFrom, String uriImageTo, String fileName) {

    final Try<Path> pathFileFrom = Try.of(() -> Paths.get(baseFolder, uriImageFrom, fileName + ".jpg"));
    final Try<Path> pathFileTo = Try.of(() -> Paths.get(baseFolder, uriImageTo, fileName + ".jpg"));
    final Try<URL> urlStringTo = Try.of(() -> new URL(baseUrl + "/" + uriImageTo + "/" + fileName + ".jpg"));

    Try<Path> folderPath = pathFileTo.flatMap(pathTo -> Try.of(() -> Files.createDirectories(pathTo.getParent())));

    Try<Path> movedPath = folderPath.flatMap(folder -> pathFileFrom.flatMap(source -> pathFileTo.flatMap(target -> Try.of(() -> Files.move(source, target)))));

    Try<URL> urlImage = movedPath.flatMap(m -> urlStringTo.flatMap(url -> Try.of(() -> new URL(url.toString()))));

    return urlImage.toEither().mapLeft(ex -> {
      Logger.of("application").warn("[Error][UploadImages.moveImage()] - ", ex);
      return "Error moviendo imagen. " + ex.getLocalizedMessage();
    });
  }

  /**
   * @param uriImage
   * @param fileNameOld
   * @param fileNameNew
   * @return
   */
  public Either<String, URL> renameImage(String uriImage, String fileNameOld, String fileNameNew) {

    final Try<Path> pathFileFrom = Try.of(() -> Paths.get(baseFolder, uriImage, fileNameOld + ".jpg"));
    final Try<URL> urlStringTo = Try.of(() -> new URL(baseUrl + "/" + uriImage + "/" + fileNameNew + ".jpg"));

    Try<Path> renamedPath = pathFileFrom.flatMap(source -> Try.of(() -> Files.move(source, source.resolveSibling(fileNameNew + ".jpg"))));

    Try<URL> urlImage = renamedPath.flatMap(r -> urlStringTo.flatMap(url -> Try.of(() -> new URL(url.toString()))));

    return urlImage.toEither().mapLeft(ex -> {
      Logger.of("application").warn("[Error][UploadImages.renameImage()] - ", ex);
      return "Error renombrando imagen. " + ex.getLocalizedMessage();
    });
  }
}
