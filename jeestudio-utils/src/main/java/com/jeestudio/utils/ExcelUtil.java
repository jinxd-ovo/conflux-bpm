package com.jeestudio.utils;

import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @Description: Excel util
 * @author: Bob
 * @Date: 2019-09-19
 */
public class ExcelUtil {

    /**
     * Write data list to excel
     */
    public static ByteArrayInputStream LinkedHashListToExcel(List<List<LinkedHashMap<String, String>>> dataResult,
                                                             int dataRowNumber,
                                                             String templateFilePath) throws IOException {
        //Insert/update existing file
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            FileInputStream file = new FileInputStream(new File(templateFilePath));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            //start inserting data from second row
            int rowIdx = dataRowNumber;
            for (List<LinkedHashMap<String, String>> rowResult : dataResult) {
                Row row = sheet.createRow(rowIdx++);
                int cellIndex = 0;
                for(LinkedHashMap<String, String> linkedHM : rowResult)
                {
                    for (Map.Entry<String, String> entry : linkedHM.entrySet()) {
                        row.createCell(cellIndex).setCellValue(entry.getValue());
                        cellIndex++;
                    }
                }

            }
            file.close();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Fail to export data to Excel file:" + e.getMessage());
        }
    }

    /**
     * Read data list from excel
     */
    public static LinkedHashMap<String, List<LinkedHashMap<String, String>>> excelToLinkedHashList(InputStream is,
                                                                                                   int passRowNumber,
                                                                                                   String tableName,
                                                                                                   String parentTableName,
                                                                                                   String primaryKey,
                                                                                                   String columns)
    {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (parentTableName.equals("empty")) parentTableName = "";
            if (primaryKey.equals("empty")) primaryKey = "";
            String listKey = tableName+"," + columns + "," + parentTableName + "," + primaryKey;
            String[] columnArray = columns.split(",");
            int columnNumber = columnArray.length;
            ArrayList<LinkedHashMap<String, String>> dataResult = new ArrayList<LinkedHashMap<String, String>>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip rows
                if (rowNumber <= (passRowNumber - 1)) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                LinkedHashMap<String, String> rowResult = new LinkedHashMap<String, String>();
                int cellIndex=0;
                PrintStream out = new PrintStream(System.out, true, "UTF-8");
                while (cellsInRow.hasNext() && cellIndex < columnNumber) {
                    Cell currentCell = cellsInRow.next();
                    String cellStrValue = "";
                    switch(currentCell.getCellType())
                    {
                        case STRING:
                            cellStrValue = currentCell.getStringCellValue();
                            break;
                        case NUMERIC:
                            cellStrValue = String.valueOf(currentCell.getNumericCellValue());
                            break;
                        case BOOLEAN:
                            cellStrValue = String.valueOf(currentCell.getBooleanCellValue());
                            break;
                        default:
                            cellStrValue = String.valueOf(currentRow.getCell(cellIndex));
                    }
                    rowResult.put(columnArray[cellIndex], cellStrValue);
                    cellIndex++;
                }
                dataResult.add(rowResult);
                rowNumber++;
            }
            workbook.close();
            LinkedHashMap<String, List<LinkedHashMap<String, String>>> finalReturnResult =
                    new LinkedHashMap<>();
            finalReturnResult.put(listKey, dataResult);
            return finalReturnResult;
        } catch (IOException e) {
            throw new RuntimeException("Fail to parse Excel file:" + e.getMessage());
        }
    }
}
