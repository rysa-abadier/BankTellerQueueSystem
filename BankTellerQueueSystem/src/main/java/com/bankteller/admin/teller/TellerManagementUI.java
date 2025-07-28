package com.bankteller.admin.teller;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class TellerManagementUI extends javax.swing.JFrame {
    private final TellerManager manager = new TellerManager();

   public TellerManagementUI() {
        initComponents();
        this.setLocationRelativeTo(null);
    this.addWindowListener(new WindowAdapter() {
        public void windowOpened(WindowEvent e) {
            loadTellersToTable();
        }
    });
    }
    private void loadTellersToTable() {
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM tellers")) {

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = metaData.getColumnName(i + 1);
        }

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = rs.getObject(i + 1);
            }
            model.addRow(row);
        }

        tellerTable.setModel(model);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
    }
}

@SuppressWarnings("unchecked")

private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {                                       
         if (tellerTable.getRowCount() >= 5) {
        JOptionPane.showMessageDialog(this, "You can only add up to 5 tellers.");
        return;
    }
    String name = JOptionPane.showInputDialog(this, "Enter Teller Name:");
    String service = JOptionPane.showInputDialog(this, "Enter Service Type:");
    if (name != null && service != null) {
        manager.addTeller(name, service);
        loadTellersToTable();
    }
}  
    
private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {                                        
        try {
        int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Teller ID to Edit:"));
        String name = JOptionPane.showInputDialog(this, "Enter New Name:");
        String service = JOptionPane.showInputDialog(this, "Enter New Service Type:");
        if (name != null && service != null) {
            manager.updateTeller(id, name, service);
            loadTellersToTable();
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid ID.");
    }
}   

private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {                                          
        try {
        int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Teller ID to Delete:"));
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.deleteTeller(id);
            loadTellersToTable();
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid ID.");
    }
}  

private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {                                           
        loadTellersToTable();
}

public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            new TellerManagementUI().setVisible(true);
        }
    });
}

private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tellerTable;
    // End of variables declaration
}
