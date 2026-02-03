package com.omar.blog.services.impl;

import com.omar.blog.domain.CreatePostRequest;
import com.omar.blog.domain.PostStatus;
import com.omar.blog.domain.UpdatePostRequest;
import com.omar.blog.domain.entity.Category;
import com.omar.blog.domain.entity.Post;
import com.omar.blog.domain.entity.Tag;
import com.omar.blog.domain.entity.User;
import com.omar.blog.repositories.PostRepository;
import com.omar.blog.services.CategoryService;
import com.omar.blog.services.PostService;
import com.omar.blog.services.TagService;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final CategoryService categoryService;
  private final TagService tagService;
  private static final int WORDS_PER_MINUTE = 100;

  @Override
  public Post getPost(UUID id) {
    return postRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Post does not exist with id " + id));
  }

  @Override
  public void deletePost(UUID id) {
    Post post = getPost(id);
    postRepository.delete(post);
  }

  @Transactional(readOnly = true)
  @Override
  public List<Post> getAllPosts(UUID categoryId, UUID tagId) {
    if (categoryId != null && tagId != null) {
      Category categoryById = categoryService.getCategoryById(categoryId);
      Tag tagById = tagService.getTagById(tagId);
      List<Post> posts =
          postRepository.findAllByStatusAndCategoryAndTagsContaining(
              PostStatus.PUBLISHED, categoryById, tagById);
      return posts;
    }

    if (categoryId != null) {
      Category categoryById = categoryService.getCategoryById(categoryId);
      List<Post> posts =
          postRepository.findAllByStatusAndCategory(PostStatus.PUBLISHED, categoryById);
      return posts;
    }

    if (tagId != null) {
      Tag tagById = tagService.getTagById(tagId);
      List<Post> posts =
          postRepository.findAllByStatusAndTagsContaining(PostStatus.PUBLISHED, tagById);
      return posts;
    }

    return postRepository.findAllByStatus(PostStatus.PUBLISHED);
  }

  @Override
  public List<Post> getDraftPosts(User user) {
    return postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT);
  }

  @Override
  @Transactional
  public Post createPost(User user, CreatePostRequest createPostRequest) {
    Post post = new Post();
    post.setTitle(createPostRequest.getTitle());
    post.setContent(createPostRequest.getContent());
    post.setStatus(createPostRequest.getStatus());
    post.setAuthor(user);
    post.setReadingTime(calculateReadingTime(createPostRequest.getContent()));

    Category categoryById = categoryService.getCategoryById(createPostRequest.getCategoryId());
    post.setCategory(categoryById);

    Set<UUID> tagIds = createPostRequest.getTagIds();
    List<Tag> tagsByIds = tagService.getTagsByIds(tagIds);
    post.setTags(new HashSet<>(tagsByIds));

    return postRepository.save(post);
  }

  @Override
  @Transactional
  public Post updatePost(UUID id, UpdatePostRequest updatePostRequest) {
    Post post =
        postRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Post does not exist with id " + id));

    post.setTitle(updatePostRequest.getTitle());
    post.setContent(updatePostRequest.getContent());
    post.setStatus(updatePostRequest.getStatus());
    post.setReadingTime(calculateReadingTime(updatePostRequest.getContent()));

    UUID categoryId = updatePostRequest.getCategoryId();
    if (!post.getCategory().getId().equals(categoryId)) {
      Category categoryById = categoryService.getCategoryById(categoryId);
      post.setCategory(categoryById);
    }

    Set<UUID> uuids = post.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
    Set<UUID> updatePostRequestTagIds = updatePostRequest.getTagIds();

    if (!uuids.equals(updatePostRequestTagIds)) {
      List<Tag> tagsByIds = tagService.getTagsByIds(updatePostRequestTagIds);
      post.setTags(new HashSet<>(tagsByIds));
    }

    return postRepository.save(post);
  }

  private Integer calculateReadingTime(String content) {
    if (content == null || content.isBlank()) {
      return 0;
    }
    int wordCount = content.trim().split("\\s+").length;
    int time = (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    return time;
  }
}
