package com.omar.blog.services;

import com.omar.blog.domain.entity.User;
import java.util.UUID;

public interface UserService {
  User getUserById(UUID id);
}
