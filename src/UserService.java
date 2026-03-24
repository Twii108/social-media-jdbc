import java.sql.*;
import java.util.*;

public class UserService {
    DBConnection db = new DBConnection();

    // Register a new user
    public boolean register(String username, String password, String name) {
        try (Connection conn = db.getConnection()) {
            String sql = "INSERT INTO Users(username, password, name, bio, is_private) VALUES (?, ?, ?, '', false)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, name);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("❌ Registration error: " + e.getMessage());
            return false;
        }
    }

    // Login user
    public User login(String username, String password) {
        try (Connection conn = db.getConnection()) {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("bio"),
                        rs.getBoolean("is_private")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Login error: " + e.getMessage());
        }
        return null;
    }

    // Update profile
    public boolean updateProfile(int userId, String bio, boolean isPrivate) {
        try (Connection conn = db.getConnection()) {
            String sql = "UPDATE Users SET bio = ?, is_private = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bio);
            stmt.setBoolean(2, isPrivate);
            stmt.setInt(3, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("❌ Update error: " + e.getMessage());
            return false;
        }
    }

    // Search users by username
    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            String sql = "SELECT * FROM Users WHERE username LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("bio"),
                        rs.getBoolean("is_private")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("❌ Search error: " + e.getMessage());
        }
        return users;
    }
}
