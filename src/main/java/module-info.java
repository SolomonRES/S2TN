module com.example.s2tn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires json.simple;
    requires com.google.gson;
    requires freetts;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.s2tn.model to com.google.gson;
    opens com.s2tn to javafx.fxml;
    exports com.s2tn;

}