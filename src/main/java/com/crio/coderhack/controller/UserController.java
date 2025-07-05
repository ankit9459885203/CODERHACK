package com.crio.coderhack.controller;

import com.crio.coderhack.entity.User;
import com.crio.coderhack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// @RestController combines @Controller and @ResponseBody, making it easy to build RESTful services.
@RestController
// @RequestMapping sets the base path for all endpoints in this controller.
@RequestMapping("/coderhack/api/v1/users")
public class UserController {

    private final UserService userService;

    // Constructor injection for UserService
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to register a new user.
     * POST /users
     * Request Body: { "userId": "string", "username": "string" }
     *
     * @param user The User object containing userId and username.
     * @return ResponseEntity with the created User and HTTP status 201 (Created).
     * @throws ResponseStatusException if userId or username are missing (HTTP 400 Bad Request),
     * or if a user with the given ID already exists (handled by service, HTTP 409 Conflict).
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Basic validation for request body fields
        if (user.getUserId() == null || user.getUserId().isEmpty() ||
            user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID and Username are required.");
        }
        // Score and badges are initialized by the service, so we only need ID and username from request.
        User newUser = userService.registerUser(user.getUserId(), user.getUsername());
        return new ResponseEntity<>(newUser, HttpStatus.OK); // Use 201 Created for resource creation
    }

    /**
     * Endpoint to retrieve a user by ID.
     * GET /users/{userId}
     *
     * @param userId The ID of the user to retrieve.
     * @return ResponseEntity with the User details and HTTP status 200 (OK).
     * @throws ResponseStatusException if the user is not found (HTTP 404 Not Found).
     */
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found."));
    }

    /**
     * Endpoint to update the score of a specific user.
     * PUT /users/{userId}
     * Request Body: { "score": 10 }
     *
     * @param userId The ID of the user to update.
     * @param updates A map containing the update fields (only "score" is allowed).
     * @return ResponseEntity with the updated User and HTTP status 200 (OK).
     * @throws ResponseStatusException if the user is not found (HTTP 404 Not Found),
     * if score is not a valid number (HTTP 400 Bad Request),
     * or if other fields are attempted to be updated (HTTP 400 Bad Request).
     */
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUserScore(@PathVariable String userId, @RequestBody Map<String, Object> updates) {
        // Validation: Only 'score' field is allowed for update
        if (updates.size() != 1 || !updates.containsKey("score")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only 'score' field is allowed for update.");
        }

        Object scoreObj = updates.get("score");
        int newScore;
        try {
            // Attempt to convert score to Integer. Handles if it's already Integer or a String number.
            newScore = Integer.parseInt(String.valueOf(scoreObj));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Score must be a valid number.");
        }

        User updatedUser = userService.updateScore(userId, newScore);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Endpoint to delete a specific user.
     * DELETE /users/{userId}
     *
     * @param userId The ID of the user to delete.
     * @return ResponseEntity with HTTP status 204 (No Content).
     * @throws ResponseStatusException if the user is not found (HTTP 404 Not Found).
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
    }

    /**
     * Endpoint to retrieve a list of all registered users, sorted by score.
     * GET /users
     *
     * @return ResponseEntity with a list of User objects and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}