package com.bankteller.admin.analytics;

import javax.swing.*;

public class ReportsTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Reports Panel Preview");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1220, 920);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ReportsPanelUIDraft());
            frame.setVisible(true);
        });
    }
}
