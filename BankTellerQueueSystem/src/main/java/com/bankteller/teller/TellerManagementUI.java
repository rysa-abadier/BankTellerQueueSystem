package com.bankteller.teller;

import com.bankteller.teller.*;
import com.bankteller.service.*;
import javax.swing.*;
import java.awt.*;

public class TellerManagementUI extends JFrame {
    private TellerManager manager;
    private ServiceManager serviceManager;
    private JTextArea output;

    public TellerManagementUI() {
        manager = new TellerManager();
        serviceManager = new ServiceManager();
        setupUI();
    }

    private void setupUI() {
        setTitle("Teller Management - Mariah");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        output = new JTextArea();
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton addBtn = new JButton("Add Teller");
        JButton removeBtn = new JButton("Remove Teller");
        JButton editBtn = new JButton("Edit Teller");
        JButton assignBtn = new JButton("Assign Service");
        JButton viewBtn = new JButton("View All");
        JButton exitBtn = new JButton("Exit");

        panel.add(addBtn);
        panel.add(removeBtn);
        panel.add(editBtn);
        panel.add(assignBtn);
        panel.add(viewBtn);
        panel.add(exitBtn);
        add(panel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Teller ID:"));
                String name = JOptionPane.showInputDialog("Enter Teller Name:");
                manager.addTeller(id, name);
                refreshOutput("Teller added.");
            } catch (Exception ex) {
                showError("Invalid input.");
            }
        });

        removeBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Teller ID to remove:"));
                manager.removeTeller(id);
                refreshOutput("Teller removed.");
            } catch (Exception ex) {
                showError("Invalid input.");
            }
        });

        editBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Teller ID to edit:"));
                String newName = JOptionPane.showInputDialog("Enter new name:");
                manager.editTellerName(id, newName);
                refreshOutput("Teller name updated.");
            } catch (Exception ex) {
                showError("Invalid input.");
            }
        });

        assignBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Teller ID:"));

                // Dropdown with all available service types
                Service selected = (Service) JOptionPane.showInputDialog(
                    this,
                    "Select service type:",
                    "Assign Service",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    serviceManager.getAllServices().toArray(),
                    null
                );

                if (selected != null) {
                    manager.assignService(id, selected);
                    refreshOutput("Service assigned.");
                }

            } catch (Exception ex) {
                showError("Invalid input.");
            }
        });


        viewBtn.addActionListener(e -> refreshOutput("All Tellers:"));
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void refreshOutput(String message) {
        StringBuilder sb = new StringBuilder(message + "\n\n");
        for (Teller t : manager.getTellers()) {
            sb.append(t.toString()).append("\n");
        }
        output.setText(sb.toString());
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TellerManagementUI().setVisible(true);
        });
    }
}
