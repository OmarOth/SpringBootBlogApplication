package com.omar.blog.services.impl;

import com.omar.blog.domain.entity.Tag;
import com.omar.blog.repositories.TagRepository;
import com.omar.blog.services.TagService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;

  @Override
  public List<Tag> getAllTags() {
    return tagRepository.findAllWithPostCount();
  }

  @Transactional
  @Override
  public List<Tag> createTags(Set<String> tagNames) {
    List<Tag> existingTags = tagRepository.findByNameIn(tagNames);
    Set<String> existingTagNames =
        existingTags.stream().map(tag -> tag.getName()).collect(Collectors.toSet());

    List<Tag> tags =
        tagNames.stream()
            .filter(s -> !existingTagNames.contains(s))
            .map(s -> Tag.builder().name(s).build())
            .toList();
    List<Tag> savedTags = new ArrayList<>();
    if (!tags.isEmpty()) {
      savedTags = tagRepository.saveAll(tags);
    }
    savedTags.addAll(existingTags);
    return savedTags;
  }

  @Transactional
  @Override
  public void deleteTag(UUID id) {

        tagRepository
            .findById(id)
            .ifPresent(
                tag -> {
                  if (tag.getPosts().size() > 0) {
                    throw new IllegalStateException("Cannot delete tag with posts.");
                  }
                  tagRepository.deleteById(id);
                });
  }
}
