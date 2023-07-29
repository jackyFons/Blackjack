module com.blackjack.blackjack {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.blackjack to javafx.fxml;
    exports com.blackjack;
}