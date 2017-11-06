package ru.nikitasemiklit;

import ru.nikitasemiklit.gui.Window;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Window mn =  new Window("Stiker Searcher");
                mn.pack();
                mn.setVisible(true);
            }
        });
    }
}



