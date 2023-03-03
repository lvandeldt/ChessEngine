package org.vandeldt.Chess;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        JFrame test = new JFrame("Testing Board Render");

        BoardRenderer test2 = new BoardRenderer();

        test.add(test2);

        test.pack();
        test.setVisible(true);

    }

}