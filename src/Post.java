import java.time.LocalDateTime;

public class Post {
  private   int id;
    private final int userId;
    private String content;
    private final String mediaPath;
    private final LocalDateTime timestamp;
  private int postId;


   public   Post(int id, int userId, String content, String mediaPath) {
        this.id = id;
        this.postId = id;
        this.userId = userId;
        this.content = content;
        this.mediaPath = mediaPath;
        this.timestamp = LocalDateTime.now();
    }


    // Getters
   public int getPostId() {
        return postId;
    }

    public int getId() {
         return id;
     }
    public int getUserId() {
         return userId;
     }
    public String getContent() {
         return content;
     }
    public String getMediaPath() {
         return mediaPath;
     }
    public LocalDateTime getTimestamp() {
         return timestamp;
     }

    // Setters
    public void setContent(String content) {
         this.content = content;
     }
    public void setPostId(int postId) {
        this.postId = postId;
        this.id = postId; // keep both fields consistent
    }

}
