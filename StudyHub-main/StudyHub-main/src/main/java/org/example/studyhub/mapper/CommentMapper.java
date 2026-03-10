package org.example.studyhub.mapper;

import org.example.studyhub.dto.CommentDTO;
import org.example.studyhub.model.Comment;
import org.example.studyhub.model.Post;
import org.example.studyhub.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public CommentDTO toDTO(Comment comment) {
        if (comment == null) return null;

        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setStatus(comment.getStatus());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());

        if (comment.getPost() != null) {
            dto.setPostId(comment.getPost().getId());
        }

        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getId());
            dto.setUserFullName(comment.getUser().getFullName());
            dto.setUserAvatar(comment.getUser().getAvatar());
        }

        if (comment.getParent() != null) {
            dto.setParentId(comment.getParent().getId());
        }

        return dto;
    }

    public Comment toEntity(CommentDTO dto) {
        if (dto == null) return null;

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");

        if (dto.getPostId() != null) {
            Post post = new Post();
            post.setId(dto.getPostId());
            comment.setPost(post);
        }

        if (dto.getUserId() != null) {
            User user = new User();
            user.setId(dto.getUserId());
            comment.setUser(user);
        }

        if (dto.getParentId() != null) {
            Comment parent = new Comment();
            parent.setId(dto.getParentId());
            comment.setParent(parent);
        }

        return comment;
    }

    public List<CommentDTO> toDTOList(List<Comment> comments) {
        if (comments == null) return null;
        return comments.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Comment updateEntity(Comment existing, CommentDTO dto) {
        if (existing == null || dto == null) return existing;

        if (dto.getContent() != null) existing.setContent(dto.getContent());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());

        return existing;
    }
}