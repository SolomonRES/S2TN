module com.example.s2tn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires json.simple;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.example.s2tn to javafx.fxml;
    exports com.example.s2tn;
}