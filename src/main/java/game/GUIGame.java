package main.java.game;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import main.java.gui.GUI;

public class GUIGame {
    private GUI gui;

    public GUIGame() {
        gui = new GUI();

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }
                gui.setVisible(true);
                gui.startGame();
            }
        });
    }
}

