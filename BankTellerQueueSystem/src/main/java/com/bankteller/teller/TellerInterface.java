package com.bankteller.teller;

import com.bankteller.index.*;
import com.bankteller.admin.dashboard.*;
import java.awt.Font;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class TellerInterface extends javax.swing.JFrame {
    private final DBConnection db = new DBConnection();
    private Connection conn;
    
    private final Font placeholder = new Font("Yu Gothic", Font.ITALIC, 14);
    private final Font data = new Font("Yu Gothic", Font.PLAIN, 14);
    
    private int selectedQueueNum = -1;    
    private int tellerID = Login.getTellerID();
    private Queue<Integer> queue = new LinkedList<>();
    
    /**
     * Creates new form QueueManagementUI
     */
    public TellerInterface() {
        initComponents();
        
        setLocationRelativeTo(null);
        chkEmergency.setEnabled(false);
        
        refreshData();

        setUpSelectionListener(tblQueue);
        setUpSelectionListener(tblActive);
    }
    
    private void refreshData() {
        try {
            conn = db.connect();

            Statement stmt = conn.createStatement();
            ResultSet rs;
            DefaultTableModel model;
            
            // Active Customers
            model = (DefaultTableModel) tblActive.getModel();
            model.setRowCount(0);

            rs = stmt.executeQuery("SELECT * FROM customers WHERE status = 'Active' AND DATE(Transaction_Date) = '2025-07-31' AND teller_id = " + tellerID);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("queue_no")
                });
            }
            
            // Queued Customers
            queue.clear();
            
            model = (DefaultTableModel) tblQueue.getModel();
            model.setRowCount(0);
            
            String[] statuses = {"Queued", "Reassigned", "Skipped"};
            
            String[] emergencies = {"Yes", "NO"};
            
            for (String emergency: emergencies) {
                for (String status: statuses) {
                    rs = stmt.executeQuery("SELECT * FROM customers WHERE emergency = '" + emergency + "' AND teller_id = " + tellerID + " AND DATE(Transaction_Date) = '2025-07-31' AND status = '" + status + "'");

                    while (rs.next()) {
                        queue.add(rs.getInt("queue_no"));
                        
                        model.addRow(new Object[]{
                            rs.getInt("teller_id"),
                            rs.getInt("queue_no"),
                            rs.getString("status")
                        });
                    }
                }
            }
            
            viewQueue.setText("0");
            viewName.setText("None Selected");
            viewService.setText("None Selected");
            viewStatus.setText("None Selected");
            viewTeller.setText("None Selected");
            chkEmergency.setSelected(false);
            viewStart.setText("None Selected");
            viewEnd.setText("None Selected");
            
            viewName.setFont(placeholder);
            viewQueue.setFont(placeholder);
            viewService.setFont(placeholder);
            viewStatus.setFont(placeholder);
            viewTeller.setFont(placeholder);
            viewStart.setFont(placeholder);
            viewEnd.setFont(placeholder);
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setUpSelectionListener(JTable table) {
        table.getSelectionModel().addListSelectionListener(event -> {
            int row = table.getSelectedRow();
            
            if (row >= 0) {
                try {
                    if (table == tblQueue) selectedQueueNum = (int) table.getValueAt(row, 1);
                    else selectedQueueNum = (int) table.getValueAt(row, 0);
                    
                    conn = db.connect();

                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM customers WHERE teller_id = " + tellerID + " AND DATE(Transaction_Date) = '2025-07-31' AND queue_no = " + selectedQueueNum);
                    
                    if (rs.next()) {
                        int tellerId = rs.getInt("teller_id");
                        int serviceId = rs.getInt("service_id");
                        String status = rs.getString("status");
                        
                        viewQueue.setText(Integer.toString(rs.getInt("queue_no")));
                        viewName.setText(rs.getString("name"));
                        viewStatus.setText(status);
                        boolean check = "Yes".equals(rs.getString("emergency"));
                        chkEmergency.setSelected(check);
                        
                        if (rs.getTime("start_time") == null) {
                            viewStart.setText("Still in Queue");
                            viewEnd.setText("Still in Queue");
                        } else {
                            viewStart.setText(rs.getTime("start_time").toString());
                            
                            if (!status.equalsIgnoreCase("Reassigned") || !status.equalsIgnoreCase("Skipped")) {
                                if (rs.getTime("end_time") == null) {
                                    viewEnd.setText("In Progress");
                                } else viewEnd.setText(rs.getTime("end_time").toString());
                            } else viewEnd.setText("Incomplete");
                        }
                        
                        ResultSet tellerSQL = stmt.executeQuery("SELECT * FROM tellers WHERE id = " + tellerId);
                        if (tellerSQL.next()) {
                            viewTeller.setText(tellerSQL.getString("first_name") + " " + tellerSQL.getString("last_name"));
                        }
                        
                        ResultSet serviceSQL = stmt.executeQuery("SELECT * FROM services WHERE id = " + serviceId);
                        if (serviceSQL.next()) {
                            viewService.setText(serviceSQL.getString("name"));
                        }
                        
                        viewName.setFont(data);
                        viewQueue.setFont(data);
                        viewService.setFont(data);
                        viewStatus.setFont(data);
                        viewTeller.setFont(data);
                        viewStart.setFont(data);
                        viewEnd.setFont(data);
                    }
                    
                    conn.close();
                    
                    table.clearSelection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlQueueMgmt = new javax.swing.JPanel();
        pnlQueue = new javax.swing.JPanel();
        scrllQueue = new javax.swing.JScrollPane();
        tblQueue = new javax.swing.JTable();
        scrllActive = new javax.swing.JScrollPane();
        tblActive = new javax.swing.JTable();
        btnNext = new javax.swing.JButton();
        pnlDetails = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblTeller = new javax.swing.JLabel();
        lblService = new javax.swing.JLabel();
        lblQueue = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        chkEmergency = new javax.swing.JCheckBox();
        lblEmergency = new javax.swing.JLabel();
        btnReassign = new javax.swing.JButton();
        btnSkip = new javax.swing.JButton();
        viewQueue = new javax.swing.JLabel();
        viewTeller = new javax.swing.JLabel();
        viewName = new javax.swing.JLabel();
        viewService = new javax.swing.JLabel();
        viewStatus = new javax.swing.JLabel();
        lblStart = new javax.swing.JLabel();
        lblEnd = new javax.swing.JLabel();
        viewStart = new javax.swing.JLabel();
        viewEnd = new javax.swing.JLabel();
        btnComplete = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlQueueMgmt.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Queue Management", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 3, 14))); // NOI18N

        pnlQueue.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Queue", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 2, 12))); // NOI18N

        tblQueue.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        tblQueue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Teller No.", "Queue No.", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllQueue.setViewportView(tblQueue);
        if (tblQueue.getColumnModel().getColumnCount() > 0) {
            tblQueue.getColumnModel().getColumn(0).setResizable(false);
            tblQueue.getColumnModel().getColumn(0).setPreferredWidth(5);
            tblQueue.getColumnModel().getColumn(1).setResizable(false);
            tblQueue.getColumnModel().getColumn(1).setPreferredWidth(10);
            tblQueue.getColumnModel().getColumn(2).setResizable(false);
        }

        tblActive.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        tblActive.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Active"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllActive.setViewportView(tblActive);
        if (tblActive.getColumnModel().getColumnCount() > 0) {
            tblActive.getColumnModel().getColumn(0).setResizable(false);
        }

        btnNext.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        btnNext.setText("NEXT");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlQueueLayout = new javax.swing.GroupLayout(pnlQueue);
        pnlQueue.setLayout(pnlQueueLayout);
        pnlQueueLayout.setHorizontalGroup(
            pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQueueLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrllActive, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnlQueueLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnNext)
                            .addComponent(scrllQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(173, 173, 173))
        );
        pnlQueueLayout.setVerticalGroup(
            pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQueueLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrllActive, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrllQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnNext)
                .addContainerGap())
        );

        pnlDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Customer Details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 2, 12))); // NOI18N

        lblName.setFont(new java.awt.Font("Yu Gothic", 3, 14)); // NOI18N
        lblName.setText("Customer Name:");

        lblTeller.setFont(new java.awt.Font("Yu Gothic", 3, 14)); // NOI18N
        lblTeller.setText("Assigned Teller:");

        lblService.setFont(new java.awt.Font("Yu Gothic", 3, 14)); // NOI18N
        lblService.setText("Requested Service:");

        lblQueue.setFont(new java.awt.Font("Yu Gothic", 3, 14)); // NOI18N
        lblQueue.setText("Queue No.:");

        lblStatus.setFont(new java.awt.Font("Yu Gothic", 3, 14)); // NOI18N
        lblStatus.setText("Status:");

        chkEmergency.setFont(new java.awt.Font("Yu Gothic", 2, 14)); // NOI18N
        chkEmergency.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chkEmergency.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        chkEmergency.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        chkEmergency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkEmergencyActionPerformed(evt);
            }
        });

        lblEmergency.setFont(new java.awt.Font("Yu Gothic", 3, 14)); // NOI18N
        lblEmergency.setText("Emergency?");

        btnReassign.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        btnReassign.setText("REASSIGN");
        btnReassign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReassignActionPerformed(evt);
            }
        });

        btnSkip.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        btnSkip.setText("SKIP");
        btnSkip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSkipActionPerformed(evt);
            }
        });

        viewQueue.setFont(new java.awt.Font("Yu Gothic", 0, 14)); // NOI18N
        viewQueue.setText("0");
        viewQueue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        viewTeller.setFont(new java.awt.Font("Yu Gothic", 0, 14)); // NOI18N
        viewTeller.setText("None Selected");
        viewTeller.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        viewName.setFont(new java.awt.Font("Yu Gothic", 0, 14)); // NOI18N
        viewName.setText("None Selected");
        viewName.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        viewService.setFont(new java.awt.Font("Yu Gothic", 0, 14)); // NOI18N
        viewService.setText("None Selected");
        viewService.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        viewStatus.setFont(new java.awt.Font("Yu Gothic", 0, 14)); // NOI18N
        viewStatus.setText("None Selected");
        viewStatus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblStart.setFont(new java.awt.Font("Yu Gothic", 3, 14)); // NOI18N
        lblStart.setText("Start Time:");

        lblEnd.setFont(new java.awt.Font("Yu Gothic", 3, 14)); // NOI18N
        lblEnd.setText("End Time:");

        viewStart.setFont(new java.awt.Font("Yu Gothic", 0, 14)); // NOI18N
        viewStart.setText("None Selected");
        viewStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        viewEnd.setFont(new java.awt.Font("Yu Gothic", 0, 14)); // NOI18N
        viewEnd.setText("None Selected");
        viewEnd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnComplete.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        btnComplete.setText("COMPLETE");
        btnComplete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDetailsLayout = new javax.swing.GroupLayout(pnlDetails);
        pnlDetails.setLayout(pnlDetailsLayout);
        pnlDetailsLayout.setHorizontalGroup(
            pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDetailsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(viewQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDetailsLayout.createSequentialGroup()
                        .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblEnd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblStart, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEmergency, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblService, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlDetailsLayout.createSequentialGroup()
                                .addComponent(lblTeller, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(24, 24, 24)))
                        .addGap(18, 18, 18)
                        .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDetailsLayout.createSequentialGroup()
                                .addComponent(chkEmergency, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 50, Short.MAX_VALUE))
                            .addComponent(viewName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewTeller, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDetailsLayout.createSequentialGroup()
                        .addComponent(btnComplete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnReassign)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSkip)))
                .addContainerGap())
        );
        pnlDetailsLayout.setVerticalGroup(
            pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetailsLayout.createSequentialGroup()
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblQueue)
                    .addComponent(viewQueue))
                .addGap(18, 18, 18)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTeller)
                    .addComponent(viewTeller))
                .addGap(18, 18, 18)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(viewName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblService)
                    .addComponent(viewService))
                .addGap(18, 18, 18)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(viewStatus))
                .addGap(18, 18, 18)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkEmergency)
                    .addComponent(lblEmergency))
                .addGap(18, 18, 18)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStart)
                    .addComponent(viewStart))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEnd)
                    .addComponent(viewEnd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReassign)
                    .addComponent(btnSkip)
                    .addComponent(btnComplete))
                .addContainerGap())
        );

        btnExit.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        btnExit.setText("LOGOUT");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlQueueMgmtLayout = new javax.swing.GroupLayout(pnlQueueMgmt);
        pnlQueueMgmt.setLayout(pnlQueueMgmtLayout);
        pnlQueueMgmtLayout.setHorizontalGroup(
            pnlQueueMgmtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQueueMgmtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQueueMgmtLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnExit)
                .addGap(17, 17, 17))
        );
        pnlQueueMgmtLayout.setVerticalGroup(
            pnlQueueMgmtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQueueMgmtLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlQueueMgmtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlQueue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExit)
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlQueueMgmt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlQueueMgmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        dispose();

        Login login = new Login();
        login.setVisible(true);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnSkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSkipActionPerformed
        if (selectedQueueNum == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to skip.");
            return;
        }

        try {
            conn = db.connect();

            int confirm = JOptionPane.showConfirmDialog(this, "Confirm to skip customer?", "Skip Customer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM customers WHERE teller_id = " + tellerID + " AND DATE(Transaction_Date) = '2025-07-31' AND queue_no = " + selectedQueueNum);

            int id = 0;
            String emergency = "";

            if (rs.next()) {
                id = rs.getInt("id");
                emergency = rs.getString("emergency");

                if (emergency.equalsIgnoreCase("Yes")) {
                    JOptionPane.showMessageDialog(this, "Customers with the emergency status cannot be skipped!");
                    return;
                }
            }

            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "UPDATE customers SET status = 'Skipped', start_time = '00:00:00' WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);

                pstmt.setInt(1, id);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Customer skipped successfully!");
                }

                refreshData();
            } else JOptionPane.showMessageDialog(this, "Skipping customer uncessfull!");

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSkipActionPerformed

    private void btnReassignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReassignActionPerformed
        if (selectedQueueNum == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to reassign.");
            return;
        }

        try {
            conn = db.connect();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM customers WHERE teller_id = " + tellerID + " AND DATE(Transaction_Date) = '2025-07-31' AND queue_no = " + selectedQueueNum);

            int id = 0;
            int teller = -1;
            int service = 0;
            String name = "";
            String status = "";
            String tellerName = "";
            String serviceName = "";

            String[] options = new String[5];

            if (rs.next()) {
                int tellerId = rs.getInt("teller_id");
                int serviceId = rs.getInt("service_id");

                id = rs.getInt("id");
                name = rs.getString("name");
                status = rs.getString("status");

                rs = stmt.executeQuery("SELECT * FROM tellers WHERE id = " + tellerId);
                if (rs.next()) {
                    tellerName = rs.getString("first_name") + " " + rs.getString("last_name");
                }

                rs = stmt.executeQuery("SELECT * FROM services WHERE id = " + serviceId);
                if (rs.next()) {
                    serviceName = rs.getString("name");
                }

                if (status.equals("Completed")) {
                    JOptionPane.showMessageDialog(this, "Completed customers cannot be reassigned!");
                    return;
                }
            }

            for (int i = 0; i < 5; i++) {
                StringBuilder sb = new StringBuilder("Teller ");
                sb.append((i+1));
                sb.append(" - ");

                rs = stmt.executeQuery("SELECT * FROM tellers WHERE id = " + (i+1));
                if (rs.next()) service = rs.getInt("service_id");

                rs = stmt.executeQuery("SELECT * FROM services WHERE id = " + service);

                if (rs.next()) {
                    sb.append(rs.getString("name"));
                }

                options[i] = sb.toString();
            }

            Object selected = JOptionPane.showInputDialog(this, "Queue No.: " + selectedQueueNum + "\nAssigned Teller: " + tellerName + "\n\nCustomer Name: " + name + "\nService: " + serviceName + "\n", "Reassign Customer", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            if (selected != null) {
                for (int i = 0; i < options.length; i++) {
                    if (options[i].equals(selected.toString())) {
                        teller = (i+1);
                    }
                }

                String sql = "UPDATE customers SET teller_id = ?, status = 'Reassigned', start_time = '00:00:00' WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);

                pstmt.setInt(1, teller);
                pstmt.setInt(2, id);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Customer reassigned successfully!");
                }

                refreshData();
            } else JOptionPane.showMessageDialog(this, "Customer reassignment cancelled!");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnReassignActionPerformed

    private void chkEmergencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEmergencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkEmergencyActionPerformed

    private void btnCompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompleteActionPerformed
        if (selectedQueueNum == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to complete service.");
            return;
        }

        try {
            conn = db.connect();

            int confirm = JOptionPane.showConfirmDialog(this, "Confirm to complete service?", "Complete Service", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM customers WHERE teller_id = " + tellerID + " AND DATE(Transaction_Date) = '2025-07-31' AND queue_no = " + selectedQueueNum);

            int id = 0;

            if (rs.next()) {
                id = rs.getInt("id");
            }

            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "UPDATE customers SET status = 'Completed', end_time = CURRENT_TIME() WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);

                pstmt.setInt(1, id);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Service completed successfully!");
                }

                refreshData();
            } else JOptionPane.showMessageDialog(this, "Complete service uncessfull!");

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnCompleteActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        if (queue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no customers in queue.");
            return;
        }

        try {
            conn = db.connect();

            int confirm = JOptionPane.showConfirmDialog(this, "Confirm to call customer?", "Next Customer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);

            Statement stmt = conn.createStatement();
            ResultSet rs;
            
            rs = stmt.executeQuery("SELECT * FROM customers WHERE status = 'Active' AND DATE(Transaction_Date) = '2025-07-31' AND teller_id = " + tellerID);

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "You have an active customer! Cannot call on next customer.");
                return;
            }

            if (confirm == JOptionPane.YES_OPTION) {
                int customer = queue.poll();
                
                System.out.println(customer);
                
                String sql = "UPDATE customers SET status = 'Active', start_time = CURRENT_TIME() WHERE DATE(Transaction_Date) = '2025-07-31' AND queue_no = " + customer;
                PreparedStatement pstmt = conn.prepareStatement(sql);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Next customer called successfully!");
                }

                refreshData();
            } else JOptionPane.showMessageDialog(this, "Next customer call uncessfull!");

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_btnNextActionPerformed

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
            java.util.logging.Logger.getLogger(TellerInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TellerInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TellerInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TellerInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TellerInterface().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnComplete;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnReassign;
    private javax.swing.JButton btnSkip;
    private javax.swing.JCheckBox chkEmergency;
    private javax.swing.JLabel lblEmergency;
    private javax.swing.JLabel lblEnd;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblQueue;
    private javax.swing.JLabel lblService;
    private javax.swing.JLabel lblStart;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTeller;
    private javax.swing.JPanel pnlDetails;
    private javax.swing.JPanel pnlQueue;
    private javax.swing.JPanel pnlQueueMgmt;
    private javax.swing.JScrollPane scrllActive;
    private javax.swing.JScrollPane scrllQueue;
    private javax.swing.JTable tblActive;
    private javax.swing.JTable tblQueue;
    private javax.swing.JLabel viewEnd;
    private javax.swing.JLabel viewName;
    private javax.swing.JLabel viewQueue;
    private javax.swing.JLabel viewService;
    private javax.swing.JLabel viewStart;
    private javax.swing.JLabel viewStatus;
    private javax.swing.JLabel viewTeller;
    // End of variables declaration//GEN-END:variables
}
