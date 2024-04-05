package com.tugalsan.api.file.csv.server;

import org.apache.commons.csv.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import com.tugalsan.api.list.client.*;
import com.tugalsan.api.union.client.TGS_Union;
import java.io.IOException;

public class TS_FileCsvUtils {

    public static TGS_Union<List<TGS_Union<Boolean>>> toFile(TGS_ListTable source, Path destFile, boolean excelStyle) {
        List<TGS_Union<Boolean>> results = TGS_ListUtils.of();
        try (var writer = Files.newBufferedWriter(destFile);) {
            var f = excelStyle ? CSVFormat.EXCEL.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim() : CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
            var csvPrinter = new CSVPrinter(writer, f);
            source.getRows().stream()
                    .map(row -> row == null ? TGS_ListUtils.of() : (List) row)
                    .map(row -> {
                        TGS_Union<Boolean> u;
                        try {
                            csvPrinter.printRecord(row);
                            u = TGS_Union.of(true);
                        } catch (IOException ex) {
                            u = TGS_Union.ofThrowable(ex);
                        }
                        return u;
                    }).forEachOrdered(u -> results.add((TGS_Union<Boolean>) u));
            csvPrinter.flush();
            return TGS_Union.of(results);
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
