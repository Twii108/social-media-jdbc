public class User {
    private final int id;
    private final String username;
    private final String password;
    private final String name;
    private String bio;
    private boolean isPrivate;

    public User(int id, String username, String password, String name, String bio, boolean isPrivate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.bio = bio;
        this.isPrivate = isPrivate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getName() {
        return name;
    }
    public String getBio() {
        return bio;
    }
    public boolean isPrivate() {
        return isPrivate;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
