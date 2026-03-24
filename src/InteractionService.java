import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

 class InteractionService {
    DBConnection db = new DBConnection();

     void likePost(int userId, int postId) {
        try (Connection conn = db.getConnection()) {
            String sql = "INSERT INTO Likes (user_id, post_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            stmt.executeUpdate();
            System.out.println("❤️ Post liked.");
        } catch (SQLException e) {
            System.out.println("❌ Like failed: " + e.getMessage());
        }
    }

     void commentPost(int userId, int postId, String commentText) {
        try (Connection conn = db.getConnection()) {
            String sql = "INSERT INTO Comments (user_id, post_id, comment_text) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            stmt.setString(3, commentText);
            stmt.executeUpdate();
            System.out.println("💬 Comment added.");
        } catch (SQLException e) {
            System.out.println("❌ Failed to comment: " + e.getMessage());
        }
    }

     void sharePost(int userId, int postId) {
        try (Connection conn = db.getConnection()) {
            String sql = "INSERT INTO Shares (user_id, post_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, postId);
            stmt.executeUpdate();
            System.out.println("🔁 Post shared.");
        } catch (SQLException e) {
            System.out.println("❌ Share failed: " + e.getMessage());
        }
    }
}
