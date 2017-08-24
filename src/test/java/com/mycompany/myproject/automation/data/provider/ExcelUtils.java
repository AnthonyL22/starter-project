package com.mycompany.myproject.automation.data.provider;

import com.pwc.core.framework.util.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;

import static com.pwc.logging.service.LoggerService.LOG;

public class ExcelUtils {

    public static final String RESOURCES_DIR_FILE_PATH = "data/";
    private static XSSFSheet ExcelWSheet;

    /**
     * Read .xls into TestNG data provider multi-dimensional array
     *
     * @param fileName  file name to read
     * @param sheetName sheet name to interrogate
     * @param totalCols 1 based number of columns to read
     * @return data provider array
     */
    public static Object[][] getTableArray(final String fileName, final String sheetName, final int totalCols) {

        String[][] tabArray = null;

        try {

            File file = PropertiesUtils.getFileFromResources(RESOURCES_DIR_FILE_PATH + fileName);
            FileInputStream ExcelFile = new FileInputStream(file);
            XSSFWorkbook excelWBook = new XSSFWorkbook(ExcelFile);
            ExcelWSheet = excelWBook.getSheet(sheetName);

            int startRow = 1;
            int startCol = 0;
            int ci, cj;
            int totalRows = ExcelWSheet.getLastRowNum();

            tabArray = new String[totalRows][totalCols];
            ci = 0;

            for (int i = startRow; i <= totalRows; i++, ci++) {
                cj = 0;
                for (int j = startCol; j <= totalCols; j++, cj++) {
                    String columnValue = getCellData(i, j);
                    if (StringUtils.isNoneEmpty(columnValue)) {
                        tabArray[ci][cj] = columnValue;
                        LOG(true, "Read spreadsheet value=%s", tabArray[ci][cj]);
                    }

                }
            }

        } catch (Exception e) {
            LOG(true, "Could not read the Excel sheet due to exception=%s", e.getMessage());
        }

        return (tabArray);

    }

    /**
     * Read CELL data from spreadsheet
     *
     * @param rowNumber    row number to read
     * @param columnNumber column number to read
     * @return <code>String</code> value of cell data
     * @throws Exception
     */
    private static String getCellData(int rowNumber, int columnNumber) throws Exception {
        try {

            XSSFCell cell = ExcelWSheet.getRow(rowNumber).getCell(columnNumber);
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    return (cell.getRichStringCellValue().getString());
                case Cell.CELL_TYPE_NUMERIC:
                    Double dValue = cell.getNumericCellValue();
                    Integer iValue = dValue.intValue();
                    return (String.valueOf(iValue));
                default:
                    return cell.getStringCellValue();
            }
        } catch (Exception e) {
            return "";
        }
    }
}
