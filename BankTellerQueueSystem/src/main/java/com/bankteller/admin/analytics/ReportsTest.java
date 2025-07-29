package com.bankteller.admin.analytics;

import javax.swing.*;

public class ReportsTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Reports Panel UI Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 847);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new ReportsPanelUI());
            frame.setVisible(true);
        });
    }
}
