package com.tugalsan.api.file.csv.server;

import com.tugalsan.api.function.client.maythrow.checkedexceptions.TGS_FuncMTCEUtils;
import org.apache.commons.csv.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import com.tugalsan.api.list.client.*;


public class TS_FileCsvUtils {

    private static void printRecord(CSVPrinter printer, List data) {
        TGS_FuncMTCEUtils.run(() -> {
            if (data == null) {
                return;
            }
            printer.printRecord(data);
        });
    }

    public static Path toFile(TGS_ListTable source, Path destFile, boolean excelStyle) {
        return TGS_FuncMTCEUtils.call(() -> {
            try ( var writer = Files.newBufferedWriter(destFile);) {
                var f = excelStyle ? CSVFormat.EXCEL.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim() : CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
                var csvPrinter = new CSVPrinter(writer, f);
                source.getRows().stream().forEachOrdered(row -> printRecord(csvPrinter, row == null ? null : (List) row));
                csvPrinter.flush();
                return destFile;
            }
        });
    }

    public static TGS_ListTable toTable(Path sourceFile, boolean excelStyle) {
        return TGS_FuncMTCEUtils.call(() -> {
            try ( var reader = Files.newBufferedReader(sourceFile);) {
                var destTable = TGS_ListTable.ofStr();
                var f = excelStyle ? CSVFormat.EXCEL.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim() : CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
                var csvTable = new CSVParser(reader, f);
                var csvHeaders = csvTable.getHeaderNames();
                IntStream.range(0, csvHeaders.size()).forEachOrdered(ci -> destTable.setValue(0, ci, csvHeaders.get(ci)));
                csvTable.forEach(csvRow -> {
                    var rowSize = destTable.getRowSize();
                    IntStream.range(0, csvHeaders.size()).forEachOrdered(ci -> destTable.setValue(rowSize, ci, csvRow.get(ci)));
                });
                return destTable;
            }
        });
    }

}
