package com.bankteller.admin.queue;

import com.bankteller.index.DBConnection;
import com.bankteller.admin.dashboard.*;
import java.awt.Font;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class QueueManagementUI extends javax.swing.JFrame {
    private final DBConnection db = new DBConnection();
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private DefaultTableModel model;
                    
    private QueueManager manager = new QueueManager();
    private int selectedQueueNum = -1;
    
    private final Font placeholder = new Font("Yu Gothic", Font.ITALIC, 14);
    private final Font data = new Font("Yu Gothic", Font.PLAIN, 14);
    
    /**
     * Creates new form QueueManagementUI
     */
    public QueueManagementUI() {
        initComponents();
        
        setLocationRelativeTo(null);
        
        refreshData();
        
        setUpSelectionListener(tblQueue);
        setUpSelectionListener(tblActive1);
        setUpSelectionListener(tblActive2);
        setUpSelectionListener(tblActive3);
        setUpSelectionListener(tblActive4);
        setUpSelectionListener(tblActive5);
        setUpSelectionListener(tblCompleted1);
        setUpSelectionListener(tblCompleted2);
        setUpSelectionListener(tblCompleted3);
        setUpSelectionListener(tblCompleted4);
        setUpSelectionListener(tblCompleted5);
    }
    
    private void getActive() {
        for (int i = 0; i < 5; i++) {
            model = activeTellerTable(i);
            model.setRowCount(0);
            
            int active = manager.getActiveCustomers(i+1);
            
            model.addRow(new Object[]{
                active == 0 ? null : active
            });
        }
    }
    
    private void getCompleted() {
        for (int i = 0; i < 5; i++) {
            model = completedTellerTable(i);
            model.setRowCount(0);
            
            List<Integer> complete = manager.getCompletedCustomers(i+1);
            
            for (int c : complete) {
                model.addRow(new Object[]{
                    c == 0 ? null : c
                });
            }
        }
    }
    
    private void getQueued() {
        model = (DefaultTableModel) tblQueue.getModel();
        model.setRowCount(0);

        Queue<Transaction> queue = manager.getAllQueue();
        
        for (Transaction q : queue) {
            model.addRow(new Object[]{
                q.getTeller_id(),
                q.getQueue_no(),
                q.getStatus()
            });
        }
    }
    
    private void refreshData() {
        getActive();
        getCompleted();
        getQueued();

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
    }
    
    private DefaultTableModel activeTellerTable(int num)  {
        DefaultTableModel[] active = new DefaultTableModel[] {
            (DefaultTableModel) tblActive1.getModel(),
            (DefaultTableModel) tblActive2.getModel(),
            (DefaultTableModel) tblActive3.getModel(),
            (DefaultTableModel) tblActive4.getModel(),
            (DefaultTableModel) tblActive5.getModel()
        };
        
        return active[num];
    }
    
    private DefaultTableModel completedTellerTable(int num)  {
        DefaultTableModel[] completed = new DefaultTableModel[] {
            (DefaultTableModel) tblCompleted1.getModel(),
            (DefaultTableModel) tblCompleted2.getModel(),
            (DefaultTableModel) tblCompleted3.getModel(),
            (DefaultTableModel) tblCompleted4.getModel(),
            (DefaultTableModel) tblCompleted5.getModel()
        };
        
        return completed[num];
    }
    
    private void setUpSelectionListener(JTable table) {
        table.getSelectionModel().addListSelectionListener(event -> {
            int row = table.getSelectedRow();
            
            if (row >= 0) {
                try {
                    if (table == tblQueue) selectedQueueNum = (int) table.getValueAt(row, 1);
                    else selectedQueueNum = (int) table.getValueAt(row, 0);
                    
                    conn = db.connect();

                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(manager.selectByQueueNum(selectedQueueNum));
                    
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
                            viewTeller.setText(tellerSQL.getString("first_name") != null && tellerSQL.getString("last_name") != null ? tellerSQL.getString("first_name") + " " + tellerSQL.getString("last_name") : "REMOVED TELLER");
                        }
                        
                        ResultSet serviceSQL = stmt.executeQuery("SELECT * FROM services WHERE id = " + serviceId);
                        if (serviceSQL.next()) {
                            viewService.setText(serviceSQL.getString("name") != null ? serviceSQL.getString("name") : "DELETED SERVICE");
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
        tabCustomers = new javax.swing.JTabbedPane();
        pnlActive = new javax.swing.JPanel();
        scrllActive1 = new javax.swing.JScrollPane();
        tblActive1 = new javax.swing.JTable();
        scrllActive4 = new javax.swing.JScrollPane();
        tblActive4 = new javax.swing.JTable();
        scrllActive2 = new javax.swing.JScrollPane();
        tblActive2 = new javax.swing.JTable();
        scrllActive3 = new javax.swing.JScrollPane();
        tblActive3 = new javax.swing.JTable();
        scrllActive5 = new javax.swing.JScrollPane();
        tblActive5 = new javax.swing.JTable();
        pnlCompleted = new javax.swing.JPanel();
        scrllCompleted1 = new javax.swing.JScrollPane();
        tblCompleted1 = new javax.swing.JTable();
        scrllCompleted2 = new javax.swing.JScrollPane();
        tblCompleted2 = new javax.swing.JTable();
        scrllCompleted3 = new javax.swing.JScrollPane();
        tblCompleted3 = new javax.swing.JTable();
        scrllCompleted4 = new javax.swing.JScrollPane();
        tblCompleted4 = new javax.swing.JTable();
        scrllCompleted5 = new javax.swing.JScrollPane();
        tblCompleted5 = new javax.swing.JTable();
        pnlQueue = new javax.swing.JPanel();
        scrllQueue = new javax.swing.JScrollPane();
        tblQueue = new javax.swing.JTable();
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
        btnExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlQueueMgmt.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Queue Management", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Yu Gothic", 3, 14))); // NOI18N

        tabCustomers.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N

        tblActive1.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        tblActive1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Teller 1"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllActive1.setViewportView(tblActive1);
        if (tblActive1.getColumnModel().getColumnCount() > 0) {
            tblActive1.getColumnModel().getColumn(0).setResizable(false);
        }

        tblActive4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Teller 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllActive4.setViewportView(tblActive4);
        if (tblActive4.getColumnModel().getColumnCount() > 0) {
            tblActive4.getColumnModel().getColumn(0).setResizable(false);
        }

        tblActive2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Teller 2"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllActive2.setViewportView(tblActive2);
        if (tblActive2.getColumnModel().getColumnCount() > 0) {
            tblActive2.getColumnModel().getColumn(0).setResizable(false);
        }

        tblActive3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Teller 3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllActive3.setViewportView(tblActive3);
        if (tblActive3.getColumnModel().getColumnCount() > 0) {
            tblActive3.getColumnModel().getColumn(0).setResizable(false);
        }

        tblActive5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Teller 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllActive5.setViewportView(tblActive5);
        if (tblActive5.getColumnModel().getColumnCount() > 0) {
            tblActive5.getColumnModel().getColumn(0).setResizable(false);
        }

        javax.swing.GroupLayout pnlActiveLayout = new javax.swing.GroupLayout(pnlActive);
        pnlActive.setLayout(pnlActiveLayout);
        pnlActiveLayout.setHorizontalGroup(
            pnlActiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlActiveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrllActive1, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrllActive2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(scrllActive3, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(pnlActiveLayout.createSequentialGroup()
                .addGap(114, 114, 114)
                .addComponent(scrllActive4, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scrllActive5, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlActiveLayout.setVerticalGroup(
            pnlActiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlActiveLayout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(pnlActiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrllActive1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrllActive2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrllActive3, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlActiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrllActive4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scrllActive5, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );

        tabCustomers.addTab("ACTIVE", pnlActive);

        tblCompleted1.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        tblCompleted1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Teller 1"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllCompleted1.setViewportView(tblCompleted1);
        if (tblCompleted1.getColumnModel().getColumnCount() > 0) {
            tblCompleted1.getColumnModel().getColumn(0).setResizable(false);
        }

        tblCompleted2.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        tblCompleted2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Teller 2"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllCompleted2.setViewportView(tblCompleted2);
        if (tblCompleted2.getColumnModel().getColumnCount() > 0) {
            tblCompleted2.getColumnModel().getColumn(0).setResizable(false);
        }

        tblCompleted3.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        tblCompleted3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Teller 3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllCompleted3.setViewportView(tblCompleted3);
        if (tblCompleted3.getColumnModel().getColumnCount() > 0) {
            tblCompleted3.getColumnModel().getColumn(0).setResizable(false);
        }

        tblCompleted4.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        tblCompleted4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Teller 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllCompleted4.setViewportView(tblCompleted4);
        if (tblCompleted4.getColumnModel().getColumnCount() > 0) {
            tblCompleted4.getColumnModel().getColumn(0).setResizable(false);
        }

        tblCompleted5.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        tblCompleted5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Teller 5"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrllCompleted5.setViewportView(tblCompleted5);
        if (tblCompleted5.getColumnModel().getColumnCount() > 0) {
            tblCompleted5.getColumnModel().getColumn(0).setResizable(false);
        }

        javax.swing.GroupLayout pnlCompletedLayout = new javax.swing.GroupLayout(pnlCompleted);
        pnlCompleted.setLayout(pnlCompletedLayout);
        pnlCompletedLayout.setHorizontalGroup(
            pnlCompletedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCompletedLayout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addComponent(scrllCompleted1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrllCompleted2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrllCompleted3, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrllCompleted4, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrllCompleted5, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53))
        );
        pnlCompletedLayout.setVerticalGroup(
            pnlCompletedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCompletedLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCompletedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrllCompleted2, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addComponent(scrllCompleted1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(scrllCompleted3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addComponent(scrllCompleted4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addComponent(scrllCompleted5, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabCustomers.addTab("COMPLETED", pnlCompleted);

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

        javax.swing.GroupLayout pnlQueueLayout = new javax.swing.GroupLayout(pnlQueue);
        pnlQueue.setLayout(pnlQueueLayout);
        pnlQueueLayout.setHorizontalGroup(
            pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQueueLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scrllQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(173, 173, 173))
        );
        pnlQueueLayout.setVerticalGroup(
            pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQueueLayout.createSequentialGroup()
                .addComponent(scrllQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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

        javax.swing.GroupLayout pnlDetailsLayout = new javax.swing.GroupLayout(pnlDetails);
        pnlDetails.setLayout(pnlDetailsLayout);
        pnlDetailsLayout.setHorizontalGroup(
            pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDetailsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDetailsLayout.createSequentialGroup()
                                .addComponent(lblQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(viewQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDetailsLayout.createSequentialGroup()
                                .addComponent(btnReassign)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSkip))))
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
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(viewName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewTeller, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(viewEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(pnlDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReassign)
                    .addComponent(btnSkip))
                .addContainerGap())
        );

        btnExit.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        btnExit.setText("Back to Dashboard");
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
                .addGroup(pnlQueueMgmtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabCustomers)
                    .addGroup(pnlQueueMgmtLayout.createSequentialGroup()
                        .addComponent(pnlQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQueueMgmtLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnExit)))
                .addContainerGap())
        );
        pnlQueueMgmtLayout.setVerticalGroup(
            pnlQueueMgmtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQueueMgmtLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlQueueMgmtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlQueue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExit)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlQueueMgmt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlQueueMgmt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chkEmergencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkEmergencyActionPerformed
        try {
            conn = db.connect();
            
            int confirm = JOptionPane.showConfirmDialog(this, "Change customer emergency status?", "Emergency Customer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(manager.selectByQueueNum(selectedQueueNum));
            
            int id = 0;
            String emergency = "";
            
            if (rs.next()) {
                id = rs.getInt("id");
                emergency = rs.getString("emergency").equalsIgnoreCase("Yes") ? "No" : "Yes";
                
                if (rs.getString("status").equalsIgnoreCase("Completed")) {
                    JOptionPane.showMessageDialog(this, "Cannot change emergency status of completed customers!");
                    refreshData();
                    return;
                } 
            }
            
            if (confirm == JOptionPane.YES_OPTION) {
                PreparedStatement pstmt = conn.prepareStatement(manager.updateQuery("`emergency` = ?"));

                pstmt.setString(1, emergency);
                pstmt.setInt(2, id);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Customer updated successfully!");
                }
                
                refreshData();
            } else JOptionPane.showMessageDialog(this, "Customer update cancelled!");
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }//GEN-LAST:event_chkEmergencyActionPerformed

    private void btnReassignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReassignActionPerformed
        if (selectedQueueNum == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to reassign.");
            return;
        }
        
        try {            
            conn = db.connect();

            stmt = conn.createStatement();
            rs = stmt.executeQuery(manager.selectByQueueNum(selectedQueueNum));
            
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
                    tellerName = rs.getString("first_name") != null && rs.getString("last_name") != null ? rs.getString("first_name") + " " + rs.getString("last_name") : "REMOVED TELLER";
                }

                rs = stmt.executeQuery("SELECT * FROM services WHERE id = " + serviceId);
                if (rs.next()) {
                    serviceName = rs.getString("name") != null ? rs.getString("name") : "DELETED SERVICE";
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
                    sb.append(rs.getString("name") != null ? rs.getString("name") : "DELETED SERVICE");
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

                int rows = manager.reassign(id, teller);
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

    private void btnSkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSkipActionPerformed
        if (selectedQueueNum == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to skip.");
            return;
        }
        
        try {
            conn = db.connect();
            
            int confirm = JOptionPane.showConfirmDialog(this, "Confirm to skip customer?", "Skip Customer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(manager.selectByQueueNum(selectedQueueNum));
            
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
                int rows = manager.skip(id);
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

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
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
            java.util.logging.Logger.getLogger(QueueManagementUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QueueManagementUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QueueManagementUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QueueManagementUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QueueManagementUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
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
    private javax.swing.JPanel pnlActive;
    private javax.swing.JPanel pnlCompleted;
    private javax.swing.JPanel pnlDetails;
    private javax.swing.JPanel pnlQueue;
    private javax.swing.JPanel pnlQueueMgmt;
    private javax.swing.JScrollPane scrllActive1;
    private javax.swing.JScrollPane scrllActive2;
    private javax.swing.JScrollPane scrllActive3;
    private javax.swing.JScrollPane scrllActive4;
    private javax.swing.JScrollPane scrllActive5;
    private javax.swing.JScrollPane scrllCompleted1;
    private javax.swing.JScrollPane scrllCompleted2;
    private javax.swing.JScrollPane scrllCompleted3;
    private javax.swing.JScrollPane scrllCompleted4;
    private javax.swing.JScrollPane scrllCompleted5;
    private javax.swing.JScrollPane scrllQueue;
    private javax.swing.JTabbedPane tabCustomers;
    private javax.swing.JTable tblActive1;
    private javax.swing.JTable tblActive2;
    private javax.swing.JTable tblActive3;
    private javax.swing.JTable tblActive4;
    private javax.swing.JTable tblActive5;
    private javax.swing.JTable tblCompleted1;
    private javax.swing.JTable tblCompleted2;
    private javax.swing.JTable tblCompleted3;
    private javax.swing.JTable tblCompleted4;
    private javax.swing.JTable tblCompleted5;
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
