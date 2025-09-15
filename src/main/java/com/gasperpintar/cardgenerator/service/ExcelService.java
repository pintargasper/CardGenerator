package com.gasperpintar.cardgenerator.service;

import com.gasperpintar.cardgenerator.model.CardData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelService {

    public List<CardData> readExcelFile(File excelFile) throws IOException {
        List<CardData> cardDataList = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                List<String> rowColumns = new ArrayList<>();
                short lastCellNum = row.getLastCellNum();

                for (int i = 0; i < lastCellNum; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String cellValue = getCellValue(cell);
                    rowColumns.add(cellValue);
                }

                if (!rowColumns.getFirst().isBlank() && !rowColumns.getFirst().isEmpty()) {
                    cardDataList.add(new CardData(rowColumns));
                }
            }
        }
        return cardDataList;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double numericValue = cell.getNumericCellValue();
                if (numericValue == Math.floor(numericValue)) {
                    yield String.valueOf((int) numericValue);
                }
                yield String.valueOf(numericValue);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> switch (cell.getCachedFormulaResultType()) {
                case NUMERIC -> {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        yield String.valueOf((int) numericValue);
                    }
                    yield String.valueOf(numericValue);
                }
                case STRING -> cell.getStringCellValue();
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                default -> "";
            };
            default -> "";
        };
    }
}
