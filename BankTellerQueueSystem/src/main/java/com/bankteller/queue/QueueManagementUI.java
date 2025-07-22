package com.bankteller.queue;

import com.bankteller.queue.*;
import com.bankteller.teller.*;
import java.awt.*;
import javax.swing.*;

public class QueueManagementUI extends JFrame{
    private QueueManager manager;
    private Teller teller;
    private Client client;
    private JTextArea output;

    public QueueManagementUI() {
        manager = new QueueManager();
        teller = new Teller();
        client = new Client();
        setupUI();
    }

    private void setupUI() {
        setTitle("Queue Management - Rysa");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        output = new JTextArea();
        output.setEditable(false);
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(3, 3, 10, 10));
        JButton addBtn = new JButton("Add Client");
        JButton nextBtn = new JButton("Next Client");
        JButton completeBtn = new JButton("Complete Service");
        JButton skipBtn = new JButton("Skip Client");
        JButton reassignBtn = new JButton("Reassign Client");
        JButton waitingViewBtn = new JButton("View Waiting");
        JButton activeViewBtn = new JButton("View Active");
        JButton completedViewBtn = new JButton("View Completed");
        JButton exitBtn = new JButton("Exit");

        buttons.add(addBtn);
        buttons.add(nextBtn);
        buttons.add(completeBtn);
        buttons.add(skipBtn);
        buttons.add(reassignBtn);
        buttons.add(waitingViewBtn);
        buttons.add(activeViewBtn);
        buttons.add(completedViewBtn);
        buttons.add(exitBtn);
        add(buttons, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> handleAdd(teller));
        nextBtn.addActionListener(e -> handleNextCustomer());
        completeBtn.addActionListener(e -> handleCompleteService(client));
        skipBtn.addActionListener(e -> handleSkip(client));
        reassignBtn.addActionListener(e -> handleReassign(teller, client));
        waitingViewBtn.addActionListener(e -> viewWaiting());
        activeViewBtn.addActionListener(e -> viewActive());
        waitingViewBtn.addActionListener(e -> viewCompleted());
        exitBtn.addActionListener(e -> System.exit(0));
    }
    
    private void handleAdd(Teller t) {
        try {
            int accountNum = Integer.parseInt(JOptionPane.showInputDialog("Enter Account Number:"));
            String name = JOptionPane.showInputDialog("Enter name:");
            boolean emergency = JOptionPane.showConfirmDialog(this, "Are they an emergency client? [Y / N]:", "Emergency", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            
            Client newClient = new Client(accountNum, name , emergency);
            manager.addClient(t, client);
        } catch (Exception e) {
            showError("Invalid input!");
        }
    }
    
    private void handleNextCustomer() {
        try {
            output.setText(manager.nextClient().printDetails());
        } catch (Exception e) {
            showError("There are no more queued clients.");
        }
    }
    
    private void handleCompleteService(Client c) {
        manager.completeService(c);
    }

    private void handleSkip(Client c) {
        manager.skipClient(c);
    }
    
    private void handleReassign(Teller t, Client c) {
        manager.reassignClient(t, c);
    }
    
    private void viewWaiting() {
        try {
            for (Client c : manager.getWaiting()) {
                output.setText(c.printDetails() + "\n");
            }
        } catch (Exception e) {
            showError("The queue is empty!");
        }
    }
    
    private void viewActive() {
        try {
            for (Client c : manager.getActive()) {
                output.setText(c.printDetails() + "\n");
            }
        } catch (Exception e) {
            showError("The queue is empty!");
        }
    }
    
    private void viewCompleted() {
        try {
            for (Client c : manager.getCompleted()) {
                output.setText(c.printDetails() + "\n");
            }
        } catch (Exception e) {
            showError("The queue is empty!");
        }
    }
    
    private void showError(String msg) {
        output.setText("");
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QueueManagementUI().setVisible(true));
    }
}
