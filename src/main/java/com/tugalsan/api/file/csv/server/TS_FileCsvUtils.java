package com.tugalsan.api.file.csv.server;

import org.apache.commons.csv.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.union.client.TGS_Union;
import com.tugalsan.api.union.client.TGS_UnionUtils;
import java.io.IOException;

public class TS_FileCsvUtils {

    private static void printRecord(CSVPrinter printer, List data) {
        if (data == null) {
            return;
        }
        try {
            printer.printRecord(data);
        } catch (IOException ex) {
            TGS_UnionUtils.throwAsRuntimeException(ex);
        }
    }

    public static TGS_Union<Path> toFile(TGS_ListTable source, Path destFile, boolean excelStyle) {
        try (var writer = Files.newBufferedWriter(destFile);) {
            var f = excelStyle ? CSVFormat.EXCEL.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim() : CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
            var csvPrinter = new CSVPrinter(writer, f);
            source.getRows().stream().forEachOrdered(row -> printRecord(csvPrinter, row == null ? null : (List) row));
            csvPrinter.flush();
            return TGS_Union.of(destFile);
        } catch (IOException ex) {
            return TGS_Union.ofThrowable(ex);
        }
    }

    public static TGS_Union<TGS_ListTable> toTable(Path sourceFile, boolean excelStyle) {
        try (var reader = Files.newBufferedReader(sourceFile);) {
            var destTable = TGS_ListTable.ofStr();
            var f = excelStyle ? CSVFormat.EXCEL.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim() : CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
            var csvTable = new CSVParser(reader, f);
            var csvHeaders = csvTable.getHeaderNames();
            IntStream.range(0, csvHeaders.size()).forEachOrdered(ci -> destTable.setValue(0, ci, csvHeaders.get(ci)));
            csvTable.forEach(csvRow -> {
                var rowSize = destTable.getRowSize();
                IntStream.range(0, csvHeaders.size()).forEachOrdered(ci -> destTable.setValue(rowSize, ci, csvRow.get(ci)));
            });
            return TGS_Union.of(destTable);
        } catch (IOException ex) {
            return TGS_Union.ofThrowable(ex);
        }
    }

}
