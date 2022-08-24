package utilities.excel;

import akka.stream.javadsl.Source;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import play.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * https://www.jeejava.com/generic-way-of-writing-data-in-excel-using-apache-poi/
 */
public class ExcelGenerator {

  public static <T> Source<Try<InputStream>, ?> generateExcel(Source<java.util.List<T>, ?> listSource,
                                                              Option<Map<String, String>> excelNamer,
                                                              Class<T> type) {
    Logger.of("application").info("Generating excel with source");
    SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
    Map<String, CellStyle> leStyles = stylesMap(workbook);

    List<Tuple2<String, Integer>> fieldNames = getFieldNamesForClass(type)
      .get()
      .zipWithIndex();
    Sheet sheet = createSheetWithHeaders(workbook, excelNamer, fieldNames);

    io.vavr.collection.Map<String, Field> typeFields = Array.of(type.getFields())
      .toMap(field -> Tuple.of(field.getName(), field));

    return listSource.fold(sheet,
        (s, chunk) -> appendDataToSheet(chunk, s, leStyles, fieldNames, typeFields)
      )
      .map(s -> {
        Logger.of("application").info("writing SXSSFWorkbook");
        Path outPath = File.createTempFile("reportes-excel-".concat(type.getSimpleName()).concat("-"), ".xlsx").toPath();
        OutputStream out = Files.newOutputStream(outPath);
        return Try.of(() -> {
          workbook.write(out);
          workbook.dispose();
          workbook.close();
          out.close();
          return Files.newInputStream(outPath);
        }).onFailure(ex -> Logger.of("application").debug("[ExcelGenerator][generateExcel]", ex));
      });
  }

  /**
   * @param data
   * @param <T>
   * @return
   */
  public static <T> Try<byte[]> generateExcel(List<T> data, Option<Map<String, String>> excelNamer) {
    if (data.isEmpty()) {
      return Try.failure(new Exception("Lista de datos vacía."));
    }

    final SXSSFWorkbook leBook = new SXSSFWorkbook(5000);
    Map<String, CellStyle> stringCellStyleMap = stylesMap(leBook);

    final SXSSFWorkbook leBookWithSheet = addSheet2Excel(leBook, data, excelNamer, stringCellStyleMap);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    return Try.of(() -> {
      leBookWithSheet.write(bos);
      leBookWithSheet.close();
      bos.close();
      return bos.toByteArray();
    }).onFailure(ex -> Logger.of("application").debug("[ExcelGenerator][generateExcel]", ex));
  }

  /**
   * @param workbook
   * @param data
   * @param <T>
   * @return
   */
  private static <T> SXSSFWorkbook addSheet2Excel(SXSSFWorkbook workbook, List<T> data, Option<Map<String, String>> excelNamer, Map<String, CellStyle> leStyles) {
    Class<? extends Object> runtimeClassT = data.head().getClass();
    final Sheet sheet = workbook.createSheet();
    getFieldNamesForClass(runtimeClassT)
      .forEach(fieldNames -> {
        final List<Tuple2<String, Integer>> zippedNames = fieldNames.zipWithIndex();

        final Row header = sheet.createRow(0);
        zippedNames.forEach(tuplaName -> {
          final String name = excelNamer
            .flatMap(namer -> Option.of(namer.get(tuplaName._1)))
            .getOrElse(tuplaName._1);
          final Integer idx = tuplaName._2;
          final Cell cell = header.createCell(idx);
          cell.setCellValue(name);
        });

        data.zipWithIndex().forEach(tuplaData -> {
          final T itemObj = tuplaData._1;
          final Integer itemIdx = tuplaData._2;

          final Row row = sheet.createRow(itemIdx + 1);

          zippedNames.forEach(tuplaName -> {
            final String fieldName = tuplaName._1;
            final Integer fieldIdx = tuplaName._2;

            final Cell cell = row.createCell(fieldIdx);

            Try<Object> tryObject = Try.of(() -> {
              Field leField = runtimeClassT.getField(fieldName);
              return leField.get(itemObj);
            });

            tryObject.orElseRun(ex ->
              Logger.of("application").warn("[ExcelGenerator][addSheet2Excel][tryObject]", ex));

            /**
             * Dependiendo del tipo de dato, se le aplica un tipo de formato de excel.
             * http://stackoverflow.com/questions/319438/basic-excel-currency-format-with-apache-poi
             */
            setCellValue(cell, tryObject, leStyles);
          });
        });
      });

    return workbook;
  }

  private static Try<List<String>> getFieldNamesForClass(Class<?> clazz) {
    return Try.of(clazz::getDeclaredFields)
      .map(fields -> List.of(fields)
        .map(Field::getName)
        .filter(s -> !s.toLowerCase().startsWith("_ebean"))
      ).onFailure(ex -> Logger.of("application").debug("[ExcelGenerator][getFieldNamesForClass]", ex));
  }

  private static Map<String, CellStyle> stylesMap(SXSSFWorkbook workbook) {
    final HashMap<String, CellStyle> myMap = new HashMap<>();

    Function<Integer, CellStyle> getStyle = (Integer numType) -> {
      CellStyle styleFormat = workbook.createCellStyle();
      styleFormat.setDataFormat(numType.shortValue());
      return styleFormat;
    };
    myMap.put("String", getStyle.apply(0));
    myMap.put("Long", getStyle.apply(1));
    myMap.put("Double", getStyle.apply(4));
    myMap.put("BigDecimal", getStyle.apply(2));
    myMap.put("Date", getStyle.apply(15));

    return myMap;
  }

  private static Sheet createSheetWithHeaders(SXSSFWorkbook workbook,
                                              Option<Map<String, String>> excelNamer,
                                              List<Tuple2<String, Integer>> fieldNames) {
    final Sheet sheet = workbook.createSheet();
    Row rowHeader = sheet.createRow(0);

    fieldNames
      .forEach(tuple2 -> {
        String fieldName = tuple2._1;
        Integer fieldIndex = tuple2._2;

        String headerName = excelNamer
          .flatMap(namer -> Option.of(namer.get(fieldName)))
          .getOrElse(fieldName);
        final Cell cell = rowHeader.createCell(fieldIndex);
        cell.setCellValue(headerName);
      });
    return sheet;
  }

  private static <T> Sheet appendDataToSheet(
    java.util.List<T> data,
    Sheet sheet,
    Map<String, CellStyle> leStyles,
    List<Tuple2<String, Integer>> fieldNames,
    io.vavr.collection.Map<String, Field> typeFields
  ) {
    int rowNum = sheet.getLastRowNum() + 1;
    for (T t : data) {
      Row row = sheet.createRow(rowNum);
      for (Tuple2<String, Integer> fieldNameTuple : fieldNames) {
        String fieldName = fieldNameTuple._1();
        Integer fieldNameIndex = fieldNameTuple._2;

        Cell cell = row.createCell(fieldNameIndex);

        Try<Object> tryFieldValue = typeFields.get(fieldName)
          .toTry()
          .flatMap(field -> Try.of(() -> field.get(t)));
        tryFieldValue.orElseRun(ex ->
          Logger.of("application").warn("[ExcelGenerator][appendDataToSheet][tryFieldValue]", ex));

        setCellValue(cell, tryFieldValue, leStyles);
      }
      rowNum++;
    }
    return sheet;
  }

  private static void setCellValue(Cell cell, Try<Object> tryFieldValue, Map<String, CellStyle> leStyles) {
    tryFieldValue.forEach(value -> {
      if (value != null) {

        if (value instanceof String) {
          cell.setCellValue((String) value);
          cell.setCellStyle(leStyles.get("String"));

        } else if (value instanceof Long) {
          cell.setCellValue((Long) value);
          cell.setCellStyle(leStyles.get("Long"));

        } else if (value instanceof Integer) {
          cell.setCellValue((Integer) value);
          cell.setCellStyle(leStyles.get("Long"));

        } else if (value instanceof Double) {
          cell.setCellValue((Double) value);
          cell.setCellStyle(leStyles.get("Double"));

        } else if (value instanceof BigDecimal) {
          cell.setCellValue((value).toString());
          cell.setCellStyle(leStyles.get("BigDecimal"));

        } else if (value instanceof Date) {
          cell.setCellValue((Date) value);
          cell.setCellStyle(leStyles.get("Date"));

        } else if (value instanceof LocalDate) {
          cell.setCellValue(java.sql.Date.valueOf((LocalDate) value));
          cell.setCellStyle(leStyles.get("Date"));

        } else {
          cell.setCellValue(value.toString());
          cell.setCellStyle(leStyles.get("String"));
        }
      } else {
        cell.setCellValue("NULL");
      }
    });
  }

}
