module com.tugalsan.api.file.csv {
    requires org.apache.commons.csv;
    requires com.tugalsan.api.unsafe;
    requires com.tugalsan.api.list;
    exports com.tugalsan.api.file.csv.server;
}
