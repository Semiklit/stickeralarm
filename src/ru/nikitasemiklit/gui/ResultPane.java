package ru.nikitasemiklit.gui;

import javax.swing.*;
import java.awt.*;

public class ResultPane extends JFrame {

    public ResultPane(String title, String data) throws HeadlessException {
        super(title);

        JTextArea criticalSensorsCountField = new JTextArea(data);
        JScrollPane criticalSensorsScrollPane = new JScrollPane(criticalSensorsCountField);

        Container mainContainer = this.getContentPane();
        mainContainer.add(criticalSensorsScrollPane);
    }
}
