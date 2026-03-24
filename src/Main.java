import java.sql.SQLException;
import java.util.*;

 class Main {
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        UserService userService = new UserService();
        PostService postService = new PostService();
        InteractionService interactionService = new InteractionService();
        User currentUser = null;

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Username: ");
                    String uname = sc.nextLine();
                    System.out.print("Password: ");
                    String pass = sc.nextLine();
                    System.out.print("Full Name: ");
                    String name = sc.nextLine();
                    if (userService.register(uname, pass, name)) {
                        System.out.println("✅ Registration successful.");
                    } else {
                        System.out.println("❌ Registration failed.");
                    }
                    break;

                case 2:
                    System.out.print("Username: ");
                    uname = sc.nextLine();
                    System.out.print("Password: ");
                    pass = sc.nextLine();
                    currentUser = userService.login(uname, pass);

                    if (currentUser != null) {
                        System.out.println("👋 Welcome, " + currentUser.getName());

                        boolean userMenu = true;
                        while (userMenu) {
                            System.out.println("\n--- User Menu ---");
                            System.out.println("1. Update Profile");
                            System.out.println("2. Post Menu");
                            System.out.println("3. Interaction Menu");
                            System.out.println("4. Search & Recommendation");
                            System.out.println("5. Logout");
                            System.out.print("Choice: ");
                            int subChoice = sc.nextInt();
                            sc.nextLine();

                            switch (subChoice) {
                                case 1:
                                    System.out.print("New Bio: ");
                                    String bio = sc.nextLine();
                                    System.out.print("Private Account? (true/false): ");
                                    boolean isPrivate = sc.nextBoolean();
                                    sc.nextLine();
                                    if (userService.updateProfile(currentUser.getId(), bio, isPrivate)) {
                                        System.out.println("✅ Profile updated.");
                                    } else {
                                        System.out.println("❌ Update failed.");
                                    }
                                    break;

                                case 2:
                                    boolean postMenu = true;
                                    while (postMenu) {
                                        System.out.println("\n--- Post Menu ---");
                                        System.out.println("1. Create Post");
                                        System.out.println("2. View All Posts");
                                        System.out.println("3. Edit Post");
                                        System.out.println("4. Delete Post");
                                        System.out.println("5. Back");
                                        System.out.print("Choice: ");
                                        int postChoice = sc.nextInt();
                                        sc.nextLine();

                                        switch (postChoice) {
                                            case 1:
                                                System.out.print("Enter post content: ");
                                                String content = sc.nextLine();
                                                System.out.print("Enter media path/URL (or blank): ");
                                                String media = sc.nextLine();
                                                if (postService.createPost(currentUser.getId(), content, media)) {
                                                    System.out.println("✅ Post created.");
                                                } else {
                                                    System.out.println("❌ Failed to create post.");
                                                }
                                                break;

                                            case 2:
                                                List<Post> posts = postService.getAllPosts(currentUser.getId());
                                                for (Post p : posts) {
                                                    System.out.println("ID: " + p.getId() + ", Content: " + p.getContent());
                                                }
                                                break;

                                            case 3:
                                                System.out.print("Enter Post ID to edit: ");
                                                int editId = sc.nextInt(); sc.nextLine();
                                                System.out.print("New content: ");
                                                String newContent = sc.nextLine();
                                                if (postService.editPost(editId, currentUser.getId(), newContent)) {
                                                    System.out.println("✅ Post updated.");
                                                } else {
                                                    System.out.println("❌ Update failed.");
                                                }
                                                break;

                                            case 4:
                                                System.out.print("Enter Post ID to delete: ");
                                                int delId = sc.nextInt(); sc.nextLine();
                                                if (postService.deletePost(delId, currentUser.getId())) {
                                                    System.out.println("✅ Post deleted.");
                                                } else {
                                                    System.out.println("❌ Delete failed.");
                                                }
                                                break;

                                            case 5:
                                                postMenu = false;
                                                break;

                                            default:
                                                System.out.println("❌ Invalid choice.");
                                        }
                                    }
                                    break;

                                case 3:
                                    boolean interactionMenu = true;
                                    while (interactionMenu) {
                                        System.out.println("\n--- Interaction Menu ---");
                                        System.out.println("1. Like a Post");
                                        System.out.println("2. Comment on a Post");
                                        System.out.println("3. Share a Post");
                                        System.out.println("4. Back");
                                        System.out.print("Choice: ");
                                        int interChoice = sc.nextInt();
                                        sc.nextLine();

                                        switch (interChoice) {
                                            case 1:
                                                System.out.print("Enter Post ID to like: ");
                                                int likeId = sc.nextInt();
                                                sc.nextLine();
                                                interactionService.likePost(currentUser.getId(), likeId);
                                                break;

                                            case 2:
                                                System.out.print("Enter Post ID to comment: ");
                                                int commentId = sc.nextInt();
                                                sc.nextLine();
                                                System.out.print("Comment: ");
                                                String comment = sc.nextLine();
                                                interactionService.commentPost(currentUser.getId(), commentId, comment);
                                                break;

                                            case 3:
                                                System.out.print("Enter Post ID to share: ");
                                                int shareId = sc.nextInt();
                                                sc.nextLine();
                                                interactionService.sharePost(currentUser.getId(), shareId);
                                                break;

                                            case 4:
                                                interactionMenu = false;
                                                break;

                                            default:
                                                System.out.println("❌ Invalid choice.");
                                        }
                                    }
                                    break;

                                case 4:
                                    boolean searchMenu = true;
                                    while (searchMenu) {
                                        System.out.println("\n--- Search & Recommendations ---");
                                        System.out.println("1. Search Users");
                                        System.out.println("2. Search Posts by Keyword");
                                        System.out.println("3. Recommended Posts");
                                        System.out.println("4. Back");
                                        System.out.print("Choice: ");
                                        int searchChoice = sc.nextInt();
                                        sc.nextLine();

                                        switch (searchChoice) {
                                            case 1:
                                                System.out.print("Enter username to search: ");
                                                String searchU = sc.nextLine();
                                                List<User> users = userService.searchUsers(searchU);

                                                if (users.isEmpty()) {
                                                    System.out.println("❌ User not found.");
                                                } else {
                                                    System.out.println("🔎 Search results:");
                                                    for (User u : users) {
                                                        System.out.println("ID: " + u.getId() + ", Username: " + u.getUsername());
                                                    }
                                                }
                                                break;

                                            case 2:
                                                System.out.print("Enter keyword: ");
                                                String keyword = sc.nextLine();
                                                List<Post> kPosts = postService.searchPostsByKeyword(keyword);

                                                if (kPosts.isEmpty()) {
                                                    System.out.println("❌ No posts found with the keyword: " + keyword);
                                                } else {
                                                    System.out.println("🔎 Posts matching keyword '" + keyword + "':");
                                                    for (Post p : kPosts) {
                                                        System.out.println("ID: " + p.getId() + ", Content: " + p.getContent());
                                                    }
                                                }
                                                break;

                                            case 3:
                                                System.out.println("Enter keyword for recommendation : ");
                                                String key= sc.next();
                                                List<Post> recommended = postService.recommendPosts(key);
                                                for (Post p : recommended) {
                                                    System.out.println("ID: " + p.getId() + ", Content: " + p.getContent());
                                                }
                                                break;

                                            case 4:
                                                searchMenu = false;
                                                break;

                                            default:
                                                System.out.println("❌ Invalid choice.");
                                        }
                                    }
                                    break;

                                case 5:
                                    System.out.println("👋 Logged out.");
                                    userMenu = false;
                                    break;

                                default:
                                    System.out.println("❌ Invalid option.");
                            }
                        }

                    } else {
                        System.out.println("❌ Invalid credentials.");
                    }
                    break;

                case 3:
                    System.out.println("👋 Exiting...");
                    return;

                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }
}
