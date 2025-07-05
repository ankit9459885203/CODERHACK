package com.crio.coderhack.repository;



import com.crio.coderhack.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// @Repository marks this interface as a Spring Data repository.
// It extends MongoRepository, providing CRUD operations for User entities with String ID.
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Spring Data automatically generates the implementation for this method.
    // It will find all users and sort them by score in ascending order.
    List<User> findAllByOrderByScoreAsc();
}
