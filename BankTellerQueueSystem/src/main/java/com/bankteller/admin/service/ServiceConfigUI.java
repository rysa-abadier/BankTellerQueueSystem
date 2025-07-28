package com.bankteller.admin.service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ServiceConfigUI extends JFrame {
    private ServiceManager manager;
    private JTextArea output;

    public ServiceConfigUI() {
        manager = new ServiceManager();
        setupUI();
    }

    private void setupUI() {
        setTitle("Service Config - Noah");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        output = new JTextArea();
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton viewBtn = new JButton("View All");
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton resetBtn = new JButton("Reset Demo Data");
        JButton exitBtn = new JButton("Exit");

        buttons.add(viewBtn);
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(resetBtn);
        buttons.add(exitBtn);
        add(buttons, BorderLayout.SOUTH);

        viewBtn.addActionListener(e -> refreshOutput("All Service Types:"));
        addBtn.addActionListener(e -> handleAdd());
        editBtn.addActionListener(e -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());
        resetBtn.addActionListener(e -> {
            manager = new ServiceManager(); // reload demo values
            refreshOutput("Demo data reset.");
        });
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void handleAdd() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter ID:"));
            String name = JOptionPane.showInputDialog("Enter name:");
            int priority = Integer.parseInt(JOptionPane.showInputDialog("Enter priority (lower = higher priority):"));
            int avgTime = Integer.parseInt(JOptionPane.showInputDialog("Enter average service time (minutes):"));
//            manager.addService(new Service(id, name, priority, avgTime));
            refreshOutput("Service type added.");
        } catch (Exception ex) {
            showError("Invalid input.");
        }
    }

    private void handleEdit() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter ID of the service type to edit:"));
            Service existing = manager.getById(id);
            if (existing == null) {
                showError("Service type not found.");
                return;
            }
            String name = JOptionPane.showInputDialog("Edit name:", existing.getName());
            int priority = Integer.parseInt(JOptionPane.showInputDialog("Edit priority:", existing.getPriority()));
            int avgTime = Integer.parseInt(JOptionPane.showInputDialog("Edit avg time:", existing.getAvgServiceTime()));
//            manager.updateService(new Service(id, name, priority, avgTime));
            refreshOutput("Service type updated.");
        } catch (Exception ex) {
            showError("Invalid input.");
        }
    }

    private void handleDelete() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter ID to delete:"));
            manager.removeService(id);
            refreshOutput("Service type removed.");
        } catch (Exception ex) {
            showError("Invalid input.");
        }
    }

    private void refreshOutput(String msg) {
        StringBuilder sb = new StringBuilder(msg + "\n\n");
        for (Service s : manager.getAllServices()) {
            sb.append("ID: ").append(s.getId())
                    .append(" | ").append(s.toString())
                    .append("\n");
        }
        output.setText(sb.toString());
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServiceConfigUI().setVisible(true));
    }
}

