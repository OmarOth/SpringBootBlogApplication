package com.omar.blog.repositories;

import com.omar.blog.domain.PostStatus;
import com.omar.blog.domain.entity.Category;
import com.omar.blog.domain.entity.Post;
import com.omar.blog.domain.entity.Tag;
import com.omar.blog.domain.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

  List<Post> findAllByStatusAndCategoryAndTagsContaining(
      PostStatus status, Category category, Tag tag);

  List<Post> findAllByStatusAndCategory(PostStatus status, Category category);

  List<Post> findAllByStatusAndTagsContaining(PostStatus status, Tag tag);

  List<Post> findAllByStatus(PostStatus status);

  List<Post> findAllByAuthorAndStatus(User author, PostStatus status);
}
