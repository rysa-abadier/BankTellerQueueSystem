package com.bankteller.admin.analytics;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ReportsPanel extends JPanel {

    public ReportsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createSummaryPanel());
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createTellerEfficiencyPanel());
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createCustomerVolumePanel());
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createHeatmapPanel());
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Summary Metrics"));

        panel.add(createMetricBox("Daily Customers", "125"));
        panel.add(createMetricBox("Average Wait Time", "08m 45s"));
        panel.add(createMetricBox("Transactions Today", "89"));
        panel.add(createMetricBox("Most Requested Service", "Cash Deposit"));

        return panel;
    }

    private JPanel createTellerEfficiencyPanel() {
        String[] columns = {"Teller", "Transactions", "Avg. Handling Time", "Efficiency"};
        Object[][] data = {
            {"Mariah", 25, "05m 30s", "Excellent"},
            {"Noah", 18, "07m 10s", "Good"},
            {"Isabella", 30, "04m 45s", "Excellent"},
            {"Rysa", 16, "08m 20s", "Fair"}
        };

        JTable table = new JTable(data, columns);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Teller Efficiency Report"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCustomerVolumePanel() {
        String[] columns = {"Period", "Customer Volume"};
        Object[][] data = {
            {"Today", 125},
            {"This Week", 645},
            {"This Month", 2750}
        };

        JTable table = new JTable(data, columns);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Customer Volume Report"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHeatmapPanel() {
        String[] hours = {"8AM", "9AM", "10AM", "11AM", "12PM", "1PM", "2PM", "3PM", "4PM", "5PM"};
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        Object[][] data = {
            {12, 15, 10, 8, 9, 11, 13, 16, 14, 10},
            {8, 10, 12, 14, 16, 13, 9, 7, 6, 4},
            {14, 12, 13, 15, 17, 16, 14, 12, 10, 9},
            {7, 6, 8, 10, 11, 12, 14, 13, 12, 11},
            {10, 11, 12, 13, 14, 15, 16, 14, 12, 10}
        };

        JTable table = new JTable(data, hours);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setEnabled(false);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSelected, hasFocus, row, col);
                if (val instanceof Integer) {
                    int v = (int) val;
                    if (v >= 15) {
                        c.setBackground(new Color(255, 102, 102)); // red
                    } else if (v >= 12) {
                        c.setBackground(new Color(255, 178, 102)); // orange
                    } else if (v >= 8) {
                        c.setBackground(new Color(255, 255, 153)); // yellow
                    } else {
                        c.setBackground(new Color(204, 255, 204)); // green
                    }
                } else {
                    c.setBackground(Color.white);
                }
                return c;
            }
        });

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Peak Traffic Hours Heatmap"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMetricBox(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(200, 70));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        valueLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.setEnabled(false);
    }
}
