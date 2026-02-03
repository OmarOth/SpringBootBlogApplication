package com.omar.blog.controllers;

import com.omar.blog.domain.CreatePostRequest;
import com.omar.blog.domain.UpdatePostRequest;
import com.omar.blog.domain.dto.CreatePostRequestDto;
import com.omar.blog.domain.dto.PostDto;
import com.omar.blog.domain.dto.UpdatePostRequestDto;
import com.omar.blog.domain.entity.Post;
import com.omar.blog.domain.entity.User;
import com.omar.blog.mappers.PostMapper;
import com.omar.blog.services.PostService;
import com.omar.blog.services.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;
  private final PostMapper postMapper;
  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<PostDto>> getAllPosts(
      @RequestParam(required = false) UUID categoryId, @RequestParam(required = false) UUID tagId) {
    List<Post> posts = postService.getAllPosts(categoryId, tagId);
    List<PostDto> postDtos = posts.stream().map(postMapper::toDto).toList();

    return ResponseEntity.ok(postDtos);
  }

  @GetMapping(path = "/drafts")
  public ResponseEntity<List<PostDto>> getDrafts(@RequestAttribute UUID userId) {
    User user = userService.getUserById(userId);
    List<Post> draftPosts = postService.getDraftPosts(user);
    List<PostDto> postDtoStream = draftPosts.stream().map(postMapper::toDto).toList();

    return ResponseEntity.ok(postDtoStream);
  }

  @PostMapping
  public ResponseEntity<PostDto> createPost(
      @RequestBody @Valid CreatePostRequestDto createPostRequestDto,
      @RequestAttribute UUID userId) {
    User user = userService.getUserById(userId);
    CreatePostRequest createPostRequest = postMapper.toCreatePostRequest(createPostRequestDto);
    Post post = postService.createPost(user, createPostRequest);
    PostDto postDto = postMapper.toDto(post);
    return new ResponseEntity<>(postDto, HttpStatus.CREATED);
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<PostDto> updatePost(
      @PathVariable UUID id, @RequestBody @Valid UpdatePostRequestDto updatePostRequestDto) {
    UpdatePostRequest updatePostRequest = postMapper.toUpdatePostRequest(updatePostRequestDto);
    Post updatedPost = postService.updatePost(id, updatePostRequest);
    PostDto updatedPostDto = postMapper.toDto(updatedPost);
    return ResponseEntity.ok(updatedPostDto);
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<PostDto> getPost(@PathVariable UUID id) {
    Post post = postService.getPost(id);
    PostDto postDto = postMapper.toDto(post);
    return ResponseEntity.ok(postDto);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
    postService.deletePost(id);
    return ResponseEntity.noContent().build();
  }
}
