package com.omar.blog.controllers;

import com.omar.blog.domain.dto.CreateTagsRequest;
import com.omar.blog.domain.dto.TagDto;
import com.omar.blog.domain.entity.Tag;
import com.omar.blog.mappers.TagMapper;
import com.omar.blog.services.TagService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;
  private final TagMapper tagMapper;

  @GetMapping
  public ResponseEntity<List<TagDto>> getAllTags() {
    List<Tag> tags = tagService.getAllTags();
    List<TagDto> tagRespons = tags.stream().map(tag -> tagMapper.toTagResponse(tag)).toList();
    return ResponseEntity.ok(tagRespons);
  }

  @PostMapping
  public ResponseEntity<List<TagDto>> createTags(
      @Valid @RequestBody CreateTagsRequest createTagsRequest) {
    List<Tag> savedTags = tagService.createTags(createTagsRequest.getNames());
    List<TagDto> tagRespons = savedTags.stream().map(tagMapper::toTagResponse).toList();
    return new ResponseEntity<>(tagRespons, HttpStatus.CREATED);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {
    tagService.deleteTag(id);
    return ResponseEntity.noContent().build();
  }
}
