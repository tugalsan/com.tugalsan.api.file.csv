module com.tugalsan.api.file.csv {
    requires org.apache.commons.csv;
    requires com.tugalsan.api.list;
    requires com.tugalsan.api.union;
    exports com.tugalsan.api.file.csv.server;
}
