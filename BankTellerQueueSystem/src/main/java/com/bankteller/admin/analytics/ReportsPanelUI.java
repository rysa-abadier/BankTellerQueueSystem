package com.bankteller.admin.analytics;

import com.bankteller.index.DBConnection;
import com.bankteller.admin.dashboard.*;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class ReportsPanelUI extends javax.swing.JFrame {
    private final DBConnection db = new DBConnection();
    private Connection conn;

    public ReportsPanelUI() {
        initComponents();
        
        setLocationRelativeTo(null);
        
        summaryMetrics();
        tellerEfficiency();
        customerVolume();
        heatMap();
        
        for (int i = 0; i < tblHeatMap.getColumnCount(); i++) {
            tblHeatMap.getColumnModel().getColumn(i).setCellRenderer(new HeatmapCellRenderer());
        }
    }
    
    class HeatmapCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            try {
                int traffic = Integer.parseInt(value.toString());

                if (traffic >= 15) {
                    c.setBackground(new Color(255, 102, 102)); // 🔴 Red
                } else if (traffic >= 13) {
                    c.setBackground(new Color(255, 178, 102)); // 🟧 Orange
                } else if (traffic >= 10) {
                    c.setBackground(new Color(255, 255, 153)); // 🟨 Yellow
                } else if (traffic >= 7) {
                    c.setBackground(new Color(204, 255, 153)); // 🟩 Light Green
                } else {
                    c.setBackground(new Color(192, 255, 232)); // 🟦 Mint Green
                }

                c.setForeground(Color.BLACK);
            } catch (NumberFormatException e) {
                // fallback for non-integer cells (e.g., header or null)
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }

            return c;
        }
    }
    
    private void summaryMetrics() {
        try {
            conn = db.connect();
            
            Statement stmt = conn.createStatement();
            ResultSet rs;
            
            // Daily Customers
            int total = 0, ctr = 0;
            
            rs = stmt.executeQuery("SELECT COUNT(*) AS No_Of_DailyCustomers FROM Customers GROUP BY DATE(Transaction_Date)");
            
            while (rs.next()) {
                ctr++;
                total += rs.getInt("No_Of_DailyCustomers");
            }
            
            int avgCustomers = total/ctr;
            String avgDailyCustomers = Integer.toString(avgCustomers);
            
            viewDailyCustomers.setText(avgDailyCustomers);
            
            // Transactions Today
            String num = "";
            
            rs = stmt.executeQuery("SELECT COUNT(*) AS No_Of_TransactionsToday FROM Customers WHERE DATE(Transaction_Date) = '2025-07-31'");
            
            while (rs.next()) {
                num = rs.getString("No_Of_TransactionsToday");
            }
            
            viewTransactionsToday.setText(num);
            
            // Average Waiting Time
            total = 0;
            ctr = 0;
            
            rs = stmt.executeQuery("SELECT Start_Time, TIMESTAMPDIFF(SECOND, TIME(transaction_date), start_time) AS WaitingTime FROM Customers");
            
            while (rs.next()) {
                if (!rs.getTime("Start_Time").toString().equals("00:00:00")) {
                    ctr++;
                    total += rs.getInt("WaitingTime");
                }
            }
            
            int avgSeconds  = total/ctr;
            int hours = avgSeconds / 3600;
            int minutes = (avgSeconds % 3600) / 60;
            int seconds = avgSeconds % 60;
            
            viewAveWait.setText(String.format("%02dh %02dm %02ds", hours, minutes, seconds));
            
            // Most Requested Service
            rs = stmt.executeQuery("SELECT s.name AS Service_Name, sub.Service_Count FROM (\n" +
                "    SELECT Service_ID, COUNT(*) AS Service_Count\n" +
                "    FROM Customers\n" +
                "    GROUP BY Service_ID\n" +
            ") AS sub\n" +
            "JOIN Services s ON s.id = sub.Service_ID\n" +
            "ORDER BY sub.Service_Count DESC\n" +
            "LIMIT 1;");
            
            while (rs.next()) {
                viewMostReq.setText(rs.getString("Service_Name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void tellerEfficiency() {
        try {
            conn = db.connect();
            
            Statement stmt = conn.createStatement();
            ResultSet rs;
            DefaultTableModel model = (DefaultTableModel) tblTellerEfficiency.getModel();
            model.setRowCount(0);
            
            rs = stmt.executeQuery("SELECT t.Service_ID, CONCAT(t.First_Name, ' ', t.Last_Name) AS Name, sub.Transactions_Count, (sub.Handling_Time_Sum/sub.Transactions_Count) AS AvgHandlingTime FROM (\n" +
                "   SELECT Teller_ID, COUNT(*) AS Transactions_Count, SUM(TIMESTAMPDIFF(SECOND, Start_Time, End_Time)) AS Handling_Time_Sum\n" +
                "   FROM Customers\n" +
                "   WHERE Status = 'Completed'\n" +
                "   GROUP BY Teller_ID\n" +
            ") AS sub\n" +
            "JOIN Tellers t ON t.id = sub.Teller_ID;");

            while (rs.next()) {
                int totalTransactions = rs.getInt("Transactions_Count");
                
                int avgSeconds = rs.getInt("AvgHandlingTime");
                int hours = avgSeconds / 3600;
                int minutes = (avgSeconds % 3600) / 60;
                int seconds = avgSeconds % 60;

                model.addRow(new Object[]{
                    rs.getString("Name"),
                    totalTransactions,
                    String.format("%02dh %02dm %02ds", hours, minutes, seconds),
                    getEfficiencyStatus(rs.getInt("Service_ID"), avgSeconds)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getEfficiencyStatus(int id, int seconds) {
        double reference = getReferenceHandlingTime(id);
        double status = 0;
        
        status = seconds/reference;
        
        if (status < 1) {
            return "Excellent";
        } else if (status == 1) {
            return "Good";
        } else if (status < 1.5) {
            return "Fair";
        } else
        
        return "Needs Improvement";
    }
    
    private double getReferenceHandlingTime(int id) {
        double avgReference = 0;
        
        try {
            conn = db.connect();
            
            Statement stmt = conn.createStatement();
            ResultSet rs;
            
            rs = stmt.executeQuery("SELECT TIME_TO_SEC(Average_Time) AS AvgHandlingTime FROM Services WHERE ID = " + id + ";");

            while (rs.next()) {
                avgReference = rs.getInt("AvgHandlingTime");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return avgReference;
    }
    
    private void customerVolume() {
        try {
            conn = db.connect();
            
            Statement stmt = conn.createStatement();
            ResultSet rs;
            DefaultTableModel model = (DefaultTableModel) tblCustomerVolume.getModel();
            model.setRowCount(0);
            
            String[] periods = {"Today", "Yesterday", "Last Week"};
            int[] volumes = new int[periods.length];
            
            // Today
            rs = stmt.executeQuery("SELECT COUNT(*) AS CustomerValue FROM Customers WHERE DATE(Transaction_Date) = '2025-07-31'");

            while (rs.next()) {
                volumes[0] = rs.getInt("CustomerValue");
            }
            
            // Yesterday
            rs = stmt.executeQuery("SELECT COUNT(*) AS CustomerValue FROM Customers WHERE DATE(Transaction_Date) = '2025-07-30'");

            while (rs.next()) {
                volumes[1] = rs.getInt("CustomerValue");
            }
            
            // Last Week
            rs = stmt.executeQuery("SELECT COUNT(*) AS CustomerValue FROM Customers WHERE DATE(Transaction_Date) BETWEEN '2025-07-20' AND '2025-07-26'");

            while (rs.next()) {
                volumes[2] = rs.getInt("CustomerValue");
            }
            
            for (int i = 0; i < periods.length; i++) {
                model.addRow(new Object[]{
                    periods[i],
                    volumes[i]
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void heatMap() {
        try {
            conn = db.connect();
            
            Statement stmt = conn.createStatement();
            ResultSet rs;
            DefaultTableModel model = (DefaultTableModel) tblHeatMap.getModel();
            model.setRowCount(0);
            
            int[] hours = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
            String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            int[][] avg = new int[days.length][hours.length];
            
            rs = stmt.executeQuery("SELECT DAYNAME(Transaction_Date) AS DayOfWeek, HOUR(Transaction_Date) AS HourOfDay, COUNT(*) AS TotalTransactions, ROUND(COUNT(*) / COUNT(DISTINCT DATE(Transaction_Date))) AS AvgTransactions FROM Customers WHERE Status = 'Completed' GROUP BY HOUR(Transaction_Date), DAYNAME(Transaction_Date);");

            while (rs.next()) {
                int dayIdx = dayOfWeek(rs.getString("DayOfWeek"));
                int hourIdx = hourOfDay(rs.getInt("HourOfDay"));
                
                
                if (hourIdx != -1 && dayIdx != -1) {
                    avg[dayIdx][hourIdx] += rs.getInt("AvgTransactions");
                }
            }
            
            for (int i = 0; i < days.length; i++) {
                Object[] row = new Object[hours.length];
                row[0] = days[i];
                for (int j = 0; j < hours.length; j++) {
                    row[j] = avg[i][j];
                }
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private int hourOfDay (int hour) {
        switch (hour) {
            case 8 -> {
                return 0;
            }
            case 9 -> {
                return 1;
            }
            case 10 -> {
                return 2;
            }
            case 11 -> {
                return 3;
            }
            case 12 -> {
                return 4;
            }
            case 13 -> {
                return 5;
            }
            case 14 -> {
                return 6;
            }
            case 15 -> {
                return 7;
            }
            case 16 -> {
                return 8;
            }
            case 17 -> {
                return 9;
            }
            default -> {
                return -1;
            }
        }
    }
    
    private int dayOfWeek(String day) {
        switch (day) {
            case "Monday" -> {
                return 0;
            }
            case "Tuesday" -> {
                return 1;
            }
            case "Wednesday" -> {
                return 2;
            }
            case "Thursday" -> {
                return 3;
            }
            case "Friday" -> {
                return 4;
            }
            default -> {
                return -1;
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAnalytics = new javax.swing.JPanel();
        pnlSummary = new javax.swing.JPanel();
        pnlDailyCustomers = new javax.swing.JPanel();
        lblDailyCustomers = new javax.swing.JLabel();
        viewDailyCustomers = new javax.swing.JLabel();
        pnlAveWait = new javax.swing.JPanel();
        lblAveWait = new javax.swing.JLabel();
        viewAveWait = new javax.swing.JLabel();
        pnlTransactionsToday = new javax.swing.JPanel();
        lblTransactionsToday = new javax.swing.JLabel();
        viewTransactionsToday = new javax.swing.JLabel();
        pnlMostReq = new javax.swing.JPanel();
        lblMostReq = new javax.swing.JLabel();
        viewMostReq = new javax.swing.JLabel();
        pnlTellerEfficiency = new javax.swing.JPanel();
        scrllTellerEfficiency = new javax.swing.JScrollPane();
        tblTellerEfficiency = new javax.swing.JTable();
        pnlCustomerVolume = new javax.swing.JPanel();
        scrllCustomerVolume = new javax.swing.JScrollPane();
        tblCustomerVolume = new javax.swing.JTable();
        pnlHeatMap = new javax.swing.JPanel();
        scrllHeatMap = new javax.swing.JScrollPane();
        tblHeatMap = new javax.swing.JTable();
        btnExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlSummary.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102), 1, true), "Summary Metrics"));

        pnlDailyCustomers.setBackground(new java.awt.Color(255, 255, 255));
        pnlDailyCustomers.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblDailyCustomers.setText("Daily Customers");

        viewDailyCustomers.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        viewDailyCustomers.setText("125");

        javax.swing.GroupLayout pnlDailyCustomersLayout = new javax.swing.GroupLayout(pnlDailyCustomers);
        pnlDailyCustomers.setLayout(pnlDailyCustomersLayout);
        pnlDailyCustomersLayout.setHorizontalGroup(
            pnlDailyCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDailyCustomersLayout.createSequentialGroup()
                .addGroup(pnlDailyCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDailyCustomersLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblDailyCustomers))
                    .addGroup(pnlDailyCustomersLayout.createSequentialGroup()
                        .addGap(250, 250, 250)
                        .addComponent(viewDailyCustomers)))
                .addContainerGap(265, Short.MAX_VALUE))
        );
        pnlDailyCustomersLayout.setVerticalGroup(
            pnlDailyCustomersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDailyCustomersLayout.createSequentialGroup()
                .addComponent(lblDailyCustomers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(viewDailyCustomers)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pnlAveWait.setBackground(new java.awt.Color(255, 255, 255));
        pnlAveWait.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblAveWait.setText("Average Waiting Time");

        viewAveWait.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        viewAveWait.setText("08m 45s");

        javax.swing.GroupLayout pnlAveWaitLayout = new javax.swing.GroupLayout(pnlAveWait);
        pnlAveWait.setLayout(pnlAveWaitLayout);
        pnlAveWaitLayout.setHorizontalGroup(
            pnlAveWaitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAveWaitLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAveWait)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAveWaitLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(viewAveWait)
                .addGap(216, 216, 216))
        );
        pnlAveWaitLayout.setVerticalGroup(
            pnlAveWaitLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAveWaitLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAveWait)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewAveWait)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pnlTransactionsToday.setBackground(new java.awt.Color(255, 255, 255));
        pnlTransactionsToday.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblTransactionsToday.setText("Transactions Today");

        viewTransactionsToday.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        viewTransactionsToday.setText("89");

        javax.swing.GroupLayout pnlTransactionsTodayLayout = new javax.swing.GroupLayout(pnlTransactionsToday);
        pnlTransactionsToday.setLayout(pnlTransactionsTodayLayout);
        pnlTransactionsTodayLayout.setHorizontalGroup(
            pnlTransactionsTodayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTransactionsTodayLayout.createSequentialGroup()
                .addGroup(pnlTransactionsTodayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTransactionsTodayLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTransactionsToday))
                    .addGroup(pnlTransactionsTodayLayout.createSequentialGroup()
                        .addGap(262, 262, 262)
                        .addComponent(viewTransactionsToday)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlTransactionsTodayLayout.setVerticalGroup(
            pnlTransactionsTodayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTransactionsTodayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTransactionsToday)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewTransactionsToday)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        pnlMostReq.setBackground(new java.awt.Color(255, 255, 255));
        pnlMostReq.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblMostReq.setText("Most Requested Service");

        viewMostReq.setFont(new java.awt.Font("Helvetica Neue", 0, 36)); // NOI18N
        viewMostReq.setText("Cash Deposit");

        javax.swing.GroupLayout pnlMostReqLayout = new javax.swing.GroupLayout(pnlMostReq);
        pnlMostReq.setLayout(pnlMostReqLayout);
        pnlMostReqLayout.setHorizontalGroup(
            pnlMostReqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMostReqLayout.createSequentialGroup()
                .addGroup(pnlMostReqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMostReqLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblMostReq))
                    .addGroup(pnlMostReqLayout.createSequentialGroup()
                        .addGap(171, 171, 171)
                        .addComponent(viewMostReq)))
                .addContainerGap(176, Short.MAX_VALUE))
        );
        pnlMostReqLayout.setVerticalGroup(
            pnlMostReqLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMostReqLayout.createSequentialGroup()
                .addComponent(lblMostReq)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(viewMostReq)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlSummaryLayout = new javax.swing.GroupLayout(pnlSummary);
        pnlSummary.setLayout(pnlSummaryLayout);
        pnlSummaryLayout.setHorizontalGroup(
            pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSummaryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlTransactionsToday, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDailyCustomers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlMostReq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAveWait, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlSummaryLayout.setVerticalGroup(
            pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSummaryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlDailyCustomers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAveWait, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(pnlSummaryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlTransactionsToday, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlMostReq, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTellerEfficiency.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Teller Efficiency Report"));
        pnlTellerEfficiency.setLayout(new java.awt.BorderLayout());

        tblTellerEfficiency.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Mariah", "25", "05m 30s", "Excellent"},
                {"Noah", "18", "07m 10s", "Good"},
                {"Rysa", "30", "04m 45s", "Excellent"},
                {"Isabella", "16", "08m 20s", "Fair"},
                {null, null, null, null}
            },
            new String [] {
                "Teller", "Transactions", "Avg. Handling Time", "Efficiency"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        scrllTellerEfficiency.setViewportView(tblTellerEfficiency);

        pnlTellerEfficiency.add(scrllTellerEfficiency, java.awt.BorderLayout.CENTER);

        pnlCustomerVolume.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Customer Volume Report"));
        pnlCustomerVolume.setLayout(new java.awt.BorderLayout());

        tblCustomerVolume.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Today", "125"},
                {"This Week", "645"},
                {"This Month", "2750"},
                {null, null}
            },
            new String [] {
                "Period", "Customer Value"
            }
        ));
        scrllCustomerVolume.setViewportView(tblCustomerVolume);

        pnlCustomerVolume.add(scrllCustomerVolume, java.awt.BorderLayout.CENTER);

        pnlHeatMap.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Teller Efficiency Heat Map"));
        pnlHeatMap.setLayout(new java.awt.BorderLayout());

        tblHeatMap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"12", "15", "10", "8", "9", "11", "13", "16", "14", "10"},
                {"8", "10", "12", "14", "16", "13", "9", "7", "6", "4"},
                {"14", "12", "13", "15", "17", "16", "14", "12", "10", "9"},
                {"7", "6", "8", "10", "11", "12", "14", "13", "12", "11"}
            },
            new String [] {
                "8 AM", "9 AM", "10 AM", "11 AM", "12 PM", "1 PM", "2 PM", "3 PM", "4 PM", "5 PM"
            }
        ));
        scrllHeatMap.setViewportView(tblHeatMap);

        pnlHeatMap.add(scrllHeatMap, java.awt.BorderLayout.CENTER);

        btnExit.setText("Back to Dashboard");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAnalyticsLayout = new javax.swing.GroupLayout(pnlAnalytics);
        pnlAnalytics.setLayout(pnlAnalyticsLayout);
        pnlAnalyticsLayout.setHorizontalGroup(
            pnlAnalyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAnalyticsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAnalyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlTellerEfficiency, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlCustomerVolume, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSummary, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlAnalyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnExit)
                        .addComponent(pnlHeatMap, javax.swing.GroupLayout.PREFERRED_SIZE, 1166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        pnlAnalyticsLayout.setVerticalGroup(
            pnlAnalyticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAnalyticsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlSummary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTellerEfficiency, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCustomerVolume, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlHeatMap, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExit)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addComponent(pnlAnalytics, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addComponent(pnlAnalytics, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        setVisible(false);
        dispose();
        
        AdminDashboard adminDashboard = new AdminDashboard();
        adminDashboard.setVisible(true);
    }//GEN-LAST:event_btnExitActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ReportsPanelUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReportsPanelUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReportsPanelUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReportsPanelUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ReportsPanelUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JLabel lblAveWait;
    private javax.swing.JLabel lblDailyCustomers;
    private javax.swing.JLabel lblMostReq;
    private javax.swing.JLabel lblTransactionsToday;
    private javax.swing.JPanel pnlAnalytics;
    private javax.swing.JPanel pnlAveWait;
    private javax.swing.JPanel pnlCustomerVolume;
    private javax.swing.JPanel pnlDailyCustomers;
    private javax.swing.JPanel pnlHeatMap;
    private javax.swing.JPanel pnlMostReq;
    private javax.swing.JPanel pnlSummary;
    private javax.swing.JPanel pnlTellerEfficiency;
    private javax.swing.JPanel pnlTransactionsToday;
    private javax.swing.JScrollPane scrllCustomerVolume;
    private javax.swing.JScrollPane scrllHeatMap;
    private javax.swing.JScrollPane scrllTellerEfficiency;
    private javax.swing.JTable tblCustomerVolume;
    private javax.swing.JTable tblHeatMap;
    private javax.swing.JTable tblTellerEfficiency;
    private javax.swing.JLabel viewAveWait;
    private javax.swing.JLabel viewDailyCustomers;
    private javax.swing.JLabel viewMostReq;
    private javax.swing.JLabel viewTransactionsToday;
    // End of variables declaration//GEN-END:variables
}
