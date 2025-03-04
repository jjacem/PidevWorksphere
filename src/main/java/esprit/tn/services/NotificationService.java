package esprit.tn.services;

import esprit.tn.entities.Notification;
import esprit.tn.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private Connection connection;

    public NotificationService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    /**
     * Create notification table if it doesn't exist
     */
    public void createNotificationTableIfNotExists() throws SQLException {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS notification (
                id INT AUTO_INCREMENT PRIMARY KEY,
                user_id INT NOT NULL,
                message VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_read BOOLEAN DEFAULT FALSE,
                notification_type VARCHAR(50),
                FOREIGN KEY (user_id) REFERENCES user(id_user)
            )
            """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableQuery);
        }
    }

    /**
     * Add a new notification
     */
    public void addNotification(Notification notification) throws SQLException {
        createNotificationTableIfNotExists();
        
        String query = "INSERT INTO notification (user_id, message, is_read, notification_type) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getMessage());
            ps.setBoolean(3, notification.isRead());
            ps.setString(4, notification.getType());
            
            ps.executeUpdate();
            
            // Get generated ID
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notification.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Get unread notifications for a user
     */
    public List<Notification> getUnreadNotifications(int userId) throws SQLException {
        createNotificationTableIfNotExists();
        
        String query = "SELECT * FROM notification WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        
        return notifications;
    }

    /**
     * Get all notifications for a user
     */
    public List<Notification> getAllNotifications(int userId) throws SQLException {
        createNotificationTableIfNotExists();
        
        String query = "SELECT * FROM notification WHERE user_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        
        return notifications;
    }

    /**
     * Mark a specific notification as read
     */
    public void markAsRead(int notificationId) throws SQLException {
        String query = "UPDATE notification SET is_read = TRUE WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        }
    }

    /**
     * Mark all notifications for a user as read
     */
    public void markAllAsRead(int userId) throws SQLException {
        String query = "UPDATE notification SET is_read = TRUE WHERE user_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    /**
     * Delete a notification
     */
    public void deleteNotification(int notificationId) throws SQLException {
        String query = "DELETE FROM notification WHERE id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        }
    }

    /**
     * Count unread notifications for a user
     */
    public int countUnreadNotifications(int userId) throws SQLException {
        createNotificationTableIfNotExists();
        
        String query = "SELECT COUNT(*) FROM notification WHERE user_id = ? AND is_read = FALSE";
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }

    /**
     * Map ResultSet to Notification object
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        return new Notification(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("message"),
            rs.getTimestamp("created_at"),
            rs.getBoolean("is_read"),
            rs.getString("notification_type")
        );
    }
}
