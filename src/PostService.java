import java.io.*;
import java.sql.*;
import java.util.*;

public class PostService {
    private final LinkedList<Post> postList = new LinkedList<>();
    private final Map<Integer, Post> postMap = new HashMap<>();
    private final Hashtable<Integer, Integer> postUserMap = new Hashtable<>();
    private int postIdCounter = 1;
    private final String FILE_PATH = "posts.txt";

    // Create post (with DB and file I/O)
    public synchronized boolean createPost(int userId, String content, String mediaPath) {
        String sql = "INSERT INTO posts (author, content, image_data) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/social_media", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, content);
            pstmt.setString(3, mediaPath);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);

                        // Create Post object with DB-generated ID
                        Post post = new Post(generatedId, userId, content, mediaPath);

                        // Store in memory
                        postList.addFirst(post);
                        postMap.put(generatedId, post);
                        postUserMap.put(generatedId, userId);

                        savePostToFile(post);

                        System.out.println("✅ Post created with ID: " + generatedId);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ DB error while creating post: " + e.getMessage());
        }

        return false;
    }


    // Save post to file
    private void savePostToFile(Post post) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(post.toString() + "\n");
        } catch (IOException e) {
            System.err.println("File write error: " + e.getMessage());
        }
    }

    // Save post to DB (fixed InputStream handling)
    private void savePostToDB(Post post) {
        String sql = "INSERT INTO posts(id, content, image_data, author, created_at) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/social_media", "root", "");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, post.getPostId());
            ps.setString(2, post.getContent());

            File image = new File(post.getMediaPath());
            if (image.exists()) {
                FileInputStream fis = new FileInputStream(image);
                ps.setBinaryStream(3, fis, (int) image.length());
                ps.setString(4, String.valueOf(post.getUserId()));
                ps.executeUpdate();
                fis.close();
            } else {
                ps.setNull(3, Types.BLOB);
                ps.setString(4, String.valueOf(post.getUserId()));
                ps.executeUpdate();
            }

        } catch (Exception e) {
            System.err.println("DB error: " + e.getMessage());
        }
    }

    // Get all posts


    public List<Post> getAllPosts(int userId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT id, author, content, image_data, created_at FROM posts WHERE author = ? ORDER BY created_at DESC";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/social_media", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getInt("id"),
                            rs.getInt("author"),
                            rs.getString("content"),
                            rs.getString("image_data")
                    );
                    // If your Post class supports timestamp, add:
                    // post.setCreatedAt(rs.getTimestamp("created_at"));

                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ DB error while fetching posts: " + e.getMessage());
        }

        return posts;
    }


    // Edit post
    public synchronized boolean editPost(int postId, int userId, String newContent) {
        String sql = "UPDATE posts SET content = ? WHERE id = ? AND author = ?";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/social_media", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newContent);
            pstmt.setInt(2, postId);
            pstmt.setInt(3, userId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // ✅ If post is also in memory, update it
                Post post = postMap.get(postId);
                if (post != null) {
                    post.setContent(newContent);
                }
                System.out.println("✅ Post updated successfully.");
                return true;
            } else {
                System.out.println("⚠️ Update failed: No matching post found.");
            }
        } catch (SQLException e) {
            System.err.println("DB error during post update: " + e.getMessage());
        }

        return false;
    }






    // Delete post
    public synchronized boolean deletePost(int postId, int userId) {
        String sql = "DELETE FROM posts WHERE id = ? AND author = ?";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/social_media", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // ✅ Remove from in-memory structures if exists
                postMap.remove(postId);
                postUserMap.remove(postId);
                postList.removeIf(post -> post.getId() == postId);

                System.out.println("🗑️ Post deleted successfully.");
                return true;
            } else {
                System.out.println("⚠️ Delete failed: No matching post found.");
            }
        } catch (SQLException e) {
            System.err.println("DB error during post delete: " + e.getMessage());
        }

        return false;
    }


    // Search by keyword
    public List<Post> searchPostsByKeyword(String keyword) {
        List<Post> result = new ArrayList<>();
        for (Post post : postList) {
            if (post.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(post);
            }
        }
        return result;
    }

    // Search by hashtag
    public List<Post> searchPostsByHashtag(String hashtag) {
        return searchPostsByKeyword("#" + hashtag);
    }

    // Recommend recent 5 posts
    public List<Post> recommendPosts(String keyword) {
        List<Post> recommended = new ArrayList<>();
        String sql = "SELECT id, author, content, image_data FROM posts WHERE content LIKE ?";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/social_media", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(
                            rs.getInt("id"),
                            rs.getInt("author"),
                            rs.getString("content"),
                            rs.getString("image_data")
                    );
                    recommended.add(post);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB error while recommending posts: " + e.getMessage());
        }

        return recommended;
    }


    // Run trending post thread
    public void runTrendingThread() {
        new Thread(() -> {
            Map<String, Integer> wordMap = new HashMap<>();
            for (Post p : postList) {
                for (String w : p.getContent().split(" ")) {
                    w = w.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
                    if (!w.isEmpty()) wordMap.put(w, wordMap.getOrDefault(w, 0) + 1);
                }
            }
            System.out.println("🔥 Trending Words:");
            wordMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(5)
                    .forEach(e -> System.out.println("#" + e.getKey() + " (" + e.getValue() + ")"));
        }).start();
    }

    // ✅ Safe method to add a post with image
    public void addPostWithImage(String content, String imagePath, int userId) {
        String sql = "INSERT INTO posts (content, image_data, author, created_at) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/social_media", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, content);

            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new FileNotFoundException("Image file not found: " + imagePath);
            }

            FileInputStream fis = new FileInputStream(imageFile);
            pstmt.setBlob(2, fis);
            pstmt.setString(3, String.valueOf(userId));
            pstmt.executeUpdate();
            fis.close();

            System.out.println("✅ Post added successfully with image!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}