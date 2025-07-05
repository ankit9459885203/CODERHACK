package com.crio.coderhack.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

// @Document annotation marks this class as a MongoDB document.
// The collection name in MongoDB will be "users" by default (lowercase class name).
@Document(collection = "users")
// @Data from Lombok generates getters, setters, toString, equals, and hashCode methods.
@Data
// @NoArgsConstructor from Lombok generates a no-argument constructor.
@NoArgsConstructor
// @AllArgsConstructor from Lombok generates a constructor with all fields as arguments.
@AllArgsConstructor
public class User {

    // @Id marks this field as the primary key in MongoDB.
    private @Id String userId;
    private String username;
    private int score;
    // Using a Set to store badges ensures uniqueness and no specific order.
    private Set<Badge> badges = new HashSet<>();

    // Constructor for initial user registration
    public User(String userId, String username) {
        this.userId = userId;
        this.username = username;
        this.score = 0; // Initial score is 0
        this.badges = new HashSet<>(); // Initial badges are empty
    }
}
