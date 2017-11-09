package ru.nikitasemiklit;

import ru.nikitasemiklit.gui.Window;

import javax.swing.*;
import java.io.File;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                Window mn = new Window("Sticker Searcher");
                mn.pack();
                mn.setVisible(true);
            }
        });

    }
}



