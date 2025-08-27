module com.example.cafe {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.base;
    requires transitive javafx.graphics;

    opens com.example.cafe to javafx.fxml;
    opens com.example.cafe.menu to javafx.fxml;
    exports com.example.cafe;
    exports com.example.cafe.menu;
}