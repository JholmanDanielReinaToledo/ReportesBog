package utilities.excel;

import akka.stream.javadsl.Source;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
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
import java.util.Set;
import java.util.function.Function;

/**
 * https://www.jeejava.com/generic-way-of-writing-data-in-excel-using-apache-poi/
 */
public class ExcelGeneratorFromMap {

  /**
   * @param data
   * @param <T>
   * @return
   */
  public static <T> Try<byte[]> generateExcel( Seq<HashMap<String, Object>> data) {
    if (data.isEmpty()) {
      return Try.failure(new Exception("Mapa de datos vacío."));
    }

    List<String> fieldNames = List.ofAll(data.get(0).keySet());

    final SXSSFWorkbook leBook = new SXSSFWorkbook(5000);
    Map<String, CellStyle> stringCellStyleMap = stylesMap(leBook);

    final SXSSFWorkbook leBookWithSheet = addSheet2Excel(leBook, data, fieldNames, stringCellStyleMap);
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
  private static <T> SXSSFWorkbook addSheet2Excel(
    SXSSFWorkbook workbook,
    Seq<HashMap<String, Object>> data,
    List<String> excelNamer,
    Map<String, CellStyle> leStyles
  ) {
    final Sheet sheet = workbook.createSheet();
        final List<Tuple2<String, Integer>> zippedNames = excelNamer.zipWithIndex();

        final Row header = sheet.createRow(0);
        zippedNames.forEach(tuplaName -> {
          final String name = tuplaName._1;
          final Integer idx = tuplaName._2;
          final Cell cell = header.createCell(idx);
          cell.setCellValue(name);
        });
        data.zipWithIndex().forEach(tuplaData -> {
          final HashMap<String, ?> itemObj = tuplaData._1;
          final Integer itemIdx = tuplaData._2;
          final Row row = sheet.createRow(itemIdx + 1);
          zippedNames.forEach(tuplaName -> {
            final String fieldName = tuplaName._1;
            final Integer fieldIdx = tuplaName._2;

            final Cell cell = row.createCell(fieldIdx);


            /**
             * Dependiendo del tipo de dato, se le aplica un tipo de formato de excel.
             * http://stackoverflow.com/questions/319438/basic-excel-currency-format-with-apache-poi
             */
            setCellValue(cell, itemObj.get(fieldName), leStyles);
          });
        });

    return workbook;
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
                                              Set<String> fieldNames) {
    final Sheet sheet = workbook.createSheet();
    Row rowHeader = sheet.createRow(0);
    String[] filedsArray = (String[]) fieldNames.toArray();
    for (int i = 0 ; i < filedsArray.length; i++) {
      final Cell cell = rowHeader.createCell(i);
      cell.setCellValue(filedsArray[i]);
    };
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

  private static void setCellValue(Cell cell, Object value, Map<String, CellStyle> leStyles) {
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
  }

}
