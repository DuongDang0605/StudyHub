package org.example.studyhub.mapper;

import org.example.studyhub.dto.PostDTO;
import org.example.studyhub.model.Post;
import org.example.studyhub.model.Setting;
import org.example.studyhub.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostMapper {

    public PostDTO toDTO(Post post) {
        if (post == null) return null;

        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setThumbnail(post.getThumbnail());
        dto.setStatus(post.getStatus());
        dto.setIsFeatured(post.getIsFeatured());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        if (post.getAuthor() != null) {
            dto.setAuthorId(post.getAuthor().getId());
            dto.setAuthorName(post.getAuthor().getFullName());
        }

        if (post.getCategory() != null) {
            dto.setCategoryId(post.getCategory().getId());
            dto.setCategoryName(post.getCategory().getName());
        }

        return dto;
    }

    public Post toEntity(PostDTO dto) {
        if (dto == null) return null;

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setThumbnail(dto.getThumbnail());
        post.setStatus(dto.getStatus() != null ? dto.getStatus() : "DRAFT");
        post.setIsFeatured(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);

        if (dto.getAuthorId() != null) {
            User author = new User();
            author.setId(dto.getAuthorId());
            post.setAuthor(author);
        }

        if (dto.getCategoryId() != null) {
            Setting category = new Setting();
            category.setId(dto.getCategoryId());
            post.setCategory(category);
        }

        return post;
    }

    public List<PostDTO> toDTOList(List<Post> posts) {
        if (posts == null) return null;
        return posts.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Post updateEntity(Post existing, PostDTO dto) {
        if (existing == null || dto == null) return existing;

        if (dto.getTitle() != null) existing.setTitle(dto.getTitle());
        if (dto.getContent() != null) existing.setContent(dto.getContent());
        if (dto.getThumbnail() != null) existing.setThumbnail(dto.getThumbnail());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getIsFeatured() != null) existing.setIsFeatured(dto.getIsFeatured());

        if (dto.getCategoryId() != null) {
            Setting category = new Setting();
            category.setId(dto.getCategoryId());
            existing.setCategory(category);
        }

        return existing;
    }
}