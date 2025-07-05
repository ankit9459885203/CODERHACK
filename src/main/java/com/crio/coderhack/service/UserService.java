package com.crio.coderhack.service;

import com.crio.coderhack.entity.Badge;
import com.crio.coderhack.entity.User;
import com.crio.coderhack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// @Service marks this class as a Spring service component.
@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection for UserRepository
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user with default score and empty badges.
     *
     * @param userId   The unique ID for the user.
     * @param username The username.
     * @return The newly created User object.
     * @throws ResponseStatusException if a user with the given ID already exists (HTTP 409 Conflict).
     */
    public User registerUser(String userId, String username) {
        // Validation: Check if user already exists
        if (userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with ID " + userId + " already exists.");
        }
        User newUser = new User(userId, username);
        return userRepository.save(newUser);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return An Optional containing the User if found, empty otherwise.
     */
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    /**
     * Updates the score of an existing user and awards badges based on the new score.
     *
     * @param userId   The ID of the user to update.
     * @param newScore The new score for the user (0-100).
     * @return The updated User object.
     * @throws ResponseStatusException if the user is not found (HTTP 404 Not Found)
     * or the score is invalid (HTTP 400 Bad Request).
     */
    public User updateScore(String userId, int newScore) {
        // Validation: Score must be between 0 and 100
        if (newScore < 0 || newScore > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Score must be between 0 and 100.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found."));

        user.setScore(newScore);
        applyBadges(user); // Apply badge logic
        return userRepository.save(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId The ID of the user to delete.
     * @throws ResponseStatusException if the user is not found (HTTP 404 Not Found).
     */
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with ID " + userId + " not found.");
        }
        userRepository.deleteById(userId);
    }

    /**
     * Retrieves all registered users, sorted by score in ascending order.
     *
     * @return A list of all User objects.
     */
    public List<User> getAllUsers() {
        // Using the custom method defined in the repository for sorted retrieval
        return userRepository.findAllByOrderByScoreAsc();
    }

    /**
     * Helper method to apply badges based on the user's score.
     * Badges are added to the user's existing set of badges, ensuring uniqueness.
     *
     * @param user The User object whose badges need to be updated.
     */
    private void applyBadges(User user) {
        Set<Badge> currentBadges = user.getBadges();
        int score = user.getScore();

        // Add badges based on score thresholds
        if (score >= 1) {
            currentBadges.add(Badge.CODE_NINJA);
        }
        if (score >= 30) {
            currentBadges.add(Badge.CODE_CHAMP);
        }
        if (score >= 60) {
            currentBadges.add(Badge.CODE_MASTER);
        }

        // The problem statement says "A user can only have a maximum of three unique badges"
        // Since we are using a Set, uniqueness is already handled.
        // If the requirement meant "only three badges ever, even if score drops",
        // then additional logic would be needed here to remove badges.
        // Assuming it means "max 3 types of badges (Ninja, Champ, Master) are possible"
        // and that once a badge is earned, it's not lost if score drops below threshold.
        user.setBadges(currentBadges);
    }
}
