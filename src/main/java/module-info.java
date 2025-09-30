module com.example.s2tn {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens com.example.s2tn to javafx.fxml;
    exports com.example.s2tn;
}