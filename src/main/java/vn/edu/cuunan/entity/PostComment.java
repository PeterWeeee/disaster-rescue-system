package vn.edu.cuunan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "author_name", nullable = false, length = 150)
    private String authorName;

    @Column(name = "author_avatar", length = 255)
    private String authorAvatar;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PostComment() {
    }

    public PostComment(Long postId, Long userId, String authorName, String authorAvatar, String content) {
        this.postId = postId;
        this.userId = userId;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public PostComment(Long postId, Long userId, String authorName, String authorAvatar, String content, LocalDateTime createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
