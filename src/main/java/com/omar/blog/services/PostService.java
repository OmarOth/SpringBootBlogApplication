package com.omar.blog.services;

import com.omar.blog.domain.CreatePostRequest;
import com.omar.blog.domain.UpdatePostRequest;
import com.omar.blog.domain.entity.Post;
import com.omar.blog.domain.entity.User;
import java.util.List;
import java.util.UUID;

public interface PostService {
  Post getPost(UUID id);

  void deletePost(UUID id);

  List<Post> getAllPosts(UUID categoryId, UUID tagId);

  List<Post> getDraftPosts(User user);

  Post createPost(User user, CreatePostRequest createPostRequest);

  Post updatePost(UUID id, UpdatePostRequest updatePostRequest);
}
