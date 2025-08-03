package com.bankteller.admin.queue;

import com.bankteller.index.DBConnection;
import java.sql.*;
import java.time.*;
import java.util.*;

public class QueueManager {
    public DBConnection db = new DBConnection();
    public Connection conn;
    public Statement stmt;
    public ResultSet rs;
    
    public Queue<Transaction> queue;
    public List<Transaction> transactions;
    
    public QueueManager() {
        queue = new LinkedList<>();
        transactions = new LinkedList<>();
    }
    
    public String selectQuery(String columns, boolean date) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT ").append(columns).append(" FROM transactions");
        
        if (date) sb.append(" WHERE DATE(transaction_date) = '2025-07-31'");
        
        return sb.toString();
    }
    
    public String selectQuery(String columns, String clause, boolean date) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT ").append(columns).append(" FROM transactions WHERE ").append(clause);
        if (date) sb.append(" AND DATE(transaction_date) = '2025-07-31'");
                
        return sb.toString();
    }
    
    public String selectWithTeller(String columns, int id, boolean date) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT ").append(columns).append(" FROM transactions WHERE `teller_id` = ").append(id);
        if (date) sb.append(" AND DATE(transaction_date) = '2025-07-31'");
        
        return sb.toString();
    }
    
    public String selectWithTeller(String columns, int id, String clause, boolean date) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT ").append(columns).append(" FROM transactions WHERE `teller_id` = ").append(id);
        sb.append(" AND ").append(clause);
        if (date) sb.append(" AND DATE(transaction_date) = '2025-07-31'");
                
        return sb.toString();
    }
    
    public String selectByQueueNum(int num) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT * FROM transactions WHERE `queue_no` = ").append(num);
        sb.append(" AND DATE(transaction_date) = '2025-07-31'");
                
        return sb.toString();
    }
    
    public String updateQuery(String columns) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("UPDATE `transactions` SET ").append(columns).append(" WHERE `id` = ?");
                
        return sb.toString();
    }
    
    public String updateQuery(String columns, String clause) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("UPDATE `transactions` SET ").append(columns).append(" WHERE `id` = ?");
                
        return sb.toString();
    }

    public Queue<Transaction> getAllQueue() {
        queue.clear();
        
        String[] statuses = {"Queued", "Reassigned", "Skipped"};
        String[] emergencies = {"Yes", "NO"};
        
        try {
            conn = db.connect();
            stmt = conn.createStatement();
            
            for (String emergency: emergencies) {
                for (String status: statuses) {
                    StringBuilder sb = new StringBuilder();
                    
                    sb.append("`emergency` = '").append(emergency).append("'");
                    sb.append(" AND ").append("`status` = '").append(status).append("'");
                    String clause = sb.toString();
                    
                    rs = stmt.executeQuery(selectQuery("*", clause, true));

                    while (rs.next()) {
                        Transaction transaction = new Transaction(
                            rs.getInt("id"),
                            rs.getInt("customer_id"),
                            rs.getInt("teller_id"),
                            rs.getInt("service_id"),
                            rs.getInt("queue_no"),
                            rs.getString("name"),
                            rs.getString("status"),
                            (rs.getString("emergency").equals("Yes")),
                            rs.getObject("transaction_date", java.time.LocalDateTime.class),
                            rs.getString("start_time"),
                            rs.getString("end_time")
                        );
                        queue.add(transaction);
                    }
                }
            }
            conn.close();
            
            return queue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queue;
    }

    public Queue<Transaction> getTellerQueue(int id) {
        queue.clear();
        
        String[] statuses = {"Queued", "Reassigned", "Skipped"};
        String[] emergencies = {"Yes", "NO"};
        
        try {
            conn = db.connect();
            stmt = conn.createStatement();
            
            for (String emergency: emergencies) {
                for (String status: statuses) {
                    StringBuilder sb = new StringBuilder();
                    
                    sb.append("`emergency` = '").append(emergency).append("'");
                    sb.append(" AND ").append("`status` = '").append(status).append("'");
                    String clause = sb.toString();
                    
                    rs = stmt.executeQuery(selectWithTeller("*", id, clause, true));

                    while (rs.next()) {
                        Transaction transaction = new Transaction(
                            rs.getInt("id"),
                            rs.getInt("customer_id"),
                            rs.getInt("teller_id"),
                            rs.getInt("service_id"),
                            rs.getInt("queue_no"),
                            rs.getString("name"),
                            rs.getString("status"),
                            (rs.getString("emergency").equals("Yes")),
                            rs.getObject("transaction_date", java.time.LocalDateTime.class),
                            rs.getString("start_time"),
                            rs.getString("end_time")
                        );
                        queue.add(transaction);
                    }
                }
            }
            conn.close();
            
            return queue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queue;
    }
    
    public List<Transaction> getAllTransactions() {
        transactions.clear();
        
        try {
            conn = db.connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectQuery("*", false));
            
            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getInt("id"),
                        rs.getInt("customer_id"),
                        rs.getInt("teller_id"),
                        rs.getInt("service_id"),
                        rs.getInt("queue_no"),
                        rs.getString("name"),
                        rs.getString("status"),
                        (rs.getString("emergency").equals("Yes")),
                        rs.getObject("transaction_date", java.time.LocalDateTime.class),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                );
                transactions.add(transaction);
            }
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;
    }
    
    public Transaction getByQueueNum(int num) {
        for (Transaction t : transactions) {
            if (t.getQueue_no() == num) return t;
        }
        return null;
    }
    
    public int getActiveCustomers(int id) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("`status` = 'Active'");
        sb.append(" AND `teller_id` = ").append(id);
        
        String clause = sb.toString();
        
        try {
            conn = db.connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectQuery("queue_no", clause, true));
            
            while (rs.next()) {
                return rs.getInt("queue_no");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<Integer> getCompletedCustomers(int id) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("`teller_id` = ").append(id);
        sb.append(" AND `status` = 'Completed'");
        
        String clause = sb.toString();
        
        List<Integer> complete = new LinkedList<>();
        
        try {
            conn = db.connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectQuery("*", clause, true));
            
            while (rs.next()) complete.add(rs.getInt("queue_no"));
            
            conn.close();
            
            return complete;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return complete;
    }
    
    public int reassign(int id, int teller) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("`teller_id` = ?");
        sb.append(", `status` = 'Reassigned'");
        sb.append(", `start_time` = '00:00:00'");
        
        String columns = sb.toString();
        
        try {
            conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(updateQuery(columns));

            stmt.setInt(1, teller);
            stmt.setInt(2, id);
            int row = stmt.executeUpdate();

            for (int i = 0; i < transactions.size(); i++) {
                if (transactions.get(i).getId() == id) {
                    Transaction t = transactions.get(i);
                    t.setTeller_id(teller);
                    t.setStatus("Reassigned");
                    t.setStart_time("00:00:00");
                    break;
                }
            }
            conn.close();
            
            return row;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int skip(int id) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("`status` = 'Skipped'");
        sb.append(", `start_time` = '00:00:00'");
        
        String columns = sb.toString();
        
        try {
            conn = db.connect();
            PreparedStatement stmt = conn.prepareStatement(updateQuery(columns));
            
            stmt.setInt(1, id);
            
            int row = stmt.executeUpdate();

            for (int i = 0; i < transactions.size(); i++) {
                if (transactions.get(i).getId() == id) {
                    Transaction t = transactions.get(i);
                    t.setStatus("Skipped");
                    t.setStart_time("00:00:00");
                    break;
                }
            }
            conn.close();
            
            return row;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public String selectAccount(int id, int acc) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT t.customer_id AS AccNum, c.acc_name AS AccName FROM `transactions` t LEFT JOIN customers c ON c.id = t.customer_id");
        sb.append(" WHERE DATE(t.transaction_date) = '2025-07-31' AND t.teller_id = ").append(id);
        sb.append(" AND t.customer_id = ").append(acc);
                
        return sb.toString();
    }
    
    public int getNextQueueNum() {
        try {
            conn = db.connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectQuery("MAX(queue_no) AS QueueNum", true));
            
            while (rs.next()) {
		return rs.getInt("QueueNum") + 1;
            }
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int enqueue(int accNum, int teller, int service, String name, boolean emergency) {
        String sql = "INSERT INTO transactions (customer_id, teller_id, service_id, queue_no, name, emergency, transaction_date) VALUES (?, ?, ?, ?, ?, ?, CONCAT('2025-07-31 ', CURTIME()))";

        int tokenNum = getNextQueueNum();
        
        try {
            conn = db.connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM customers WHERE id = " + accNum);

            boolean customerExists = false;

            while (rs.next()) {
                if (rs.getInt("id") == accNum) {
                    customerExists = true;

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, accNum);
                    pstmt.setInt(2, teller);
                    pstmt.setInt(3, service);
                    pstmt.setInt(4, tokenNum);
                    pstmt.setString(5, name);
                    pstmt.setString(6, emergency ? "Yes" : "No");

                    int rows = pstmt.executeUpdate();

                    pstmt.close();
                    if (rows > 0) return tokenNum;
                    else return -1;
                }
            }

            if (!customerExists) {
                System.out.println("Customer ID " + accNum + " not found.");
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ignore) {}
        }

        return -1;
    }
}
