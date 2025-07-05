package com.crio.coderhack;

import com.crio.coderhack.entity.Badge;
import com.crio.coderhack.entity.User;
import com.crio.coderhack.repository.UserRepository;
import com.crio.coderhack.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class) integrates Mockito with JUnit 5.
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    // @Mock creates a mock instance of UserRepository.
    @Mock
    private UserRepository userRepository;

    // @InjectMocks injects the mock UserRepository into UserService.
    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Initialize a common test user before each test.
        testUser = new User("user1", "testuser");
        testUser.setScore(0);
        testUser.setBadges(new HashSet<>());
    }

    @Test
    void registerUser_Success() {
        // Mock repository behavior: when existsById is called, return false (user does not exist).
        when(userRepository.existsById("user1")).thenReturn(false);
        // Mock repository behavior: when save is called, return the user object that was passed to it.
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User registeredUser = userService.registerUser("user1", "testuser");

        // Assertions to verify the outcome.
        assertNotNull(registeredUser);
        assertEquals("user1", registeredUser.getUserId());
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals(0, registeredUser.getScore());
        assertTrue(registeredUser.getBadges().isEmpty());
        // Verify that existsById was called once and save method was called exactly once.
        verify(userRepository, times(1)).existsById("user1");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_UserAlreadyExists() {
        // Mock repository behavior: user exists.
        when(userRepository.existsById("user1")).thenReturn(true);

        // Assert that calling registerUser throws a ResponseStatusException with CONFLICT status.
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.registerUser("user1", "testuser");
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("User with ID user1 already exists.", exception.getReason());
        // Verify that save method was never called.
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Found() {
        // Mock repository behavior: findById returns the test user wrapped in Optional.
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.getUserById("user1");

        // Assertions.
        assertTrue(foundUser.isPresent());
        assertEquals(testUser, foundUser.get());
    }

    @Test
    void getUserById_NotFound() {
        // Mock repository behavior: findById returns empty Optional.
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById("nonexistent");

        // Assertions.
        assertFalse(foundUser.isPresent());
    }

    @Test
    void updateScore_ValidScoreAndBadgesAwarded() {
        // Set initial score to ensure it's updated
        testUser.setScore(0);
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateScore("user1", 75); // Score for Code Master

        assertNotNull(updatedUser);
        assertEquals(75, updatedUser.getScore());
        // Check if all three badges are awarded correctly based on score 75
        Set<Badge> expectedBadges = new HashSet<>(Arrays.asList(Badge.CODE_NINJA, Badge.CODE_CHAMP, Badge.CODE_MASTER));
        assertEquals(expectedBadges, updatedUser.getBadges());
        verify(userRepository, times(1)).save(testUser); // Verify save was called with the updated user
    }

    @Test
    void updateScore_ValidScore_CodeNinjaBadge() {
        testUser.setScore(0);
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateScore("user1", 15); // Score for Code Ninja

        assertNotNull(updatedUser);
        assertEquals(15, updatedUser.getScore());
        Set<Badge> expectedBadges = new HashSet<>(Collections.singletonList(Badge.CODE_NINJA));
        assertEquals(expectedBadges, updatedUser.getBadges());
    }

    @Test
    void updateScore_ValidScore_CodeChampBadge() {
        testUser.setScore(0);
        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateScore("user1", 45); // Score for Code Champ

        assertNotNull(updatedUser);
        assertEquals(45, updatedUser.getScore());
        Set<Badge> expectedBadges = new HashSet<>(Arrays.asList(Badge.CODE_NINJA, Badge.CODE_CHAMP));
        assertEquals(expectedBadges, updatedUser.getBadges());
    }

    @Test
    void updateScore_ScoreDrops_BadgesRetained() {
        // Scenario: User had Code Master, score drops, but badges should be retained.
        testUser.setScore(90);
        Set<Badge> initialBadges = new HashSet<>(Arrays.asList(Badge.CODE_NINJA, Badge.CODE_CHAMP, Badge.CODE_MASTER));
        testUser.setBadges(initialBadges);

        when(userRepository.findById("user1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateScore("user1", 20); // Score drops to 20

        assertNotNull(updatedUser);
        assertEquals(20, updatedUser.getScore());
        // Badges should still include all previously earned ones, as per problem interpretation.
        assertEquals(initialBadges, updatedUser.getBadges());
    }


    @Test
    void updateScore_InvalidScore_TooLow() {
        // Assert that calling updateScore with invalid score throws exception.
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.updateScore("user1", -5);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Score must be between 0 and 100.", exception.getReason());
        verify(userRepository, never()).findById(anyString()); // Verify no repository interaction
    }

    @Test
    void updateScore_InvalidScore_TooHigh() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.updateScore("user1", 105);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Score must be between 0 and 100.", exception.getReason());
        verify(userRepository, never()).findById(anyString());
    }

    @Test
    void updateScore_UserNotFound() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.updateScore("nonexistent", 50);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User with ID nonexistent not found.", exception.getReason());
        verify(userRepository, never()).save(any(User.class)); // Verify save was not called
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById("user1")).thenReturn(true);
        // Mock void method: doNothing when deleteById is called.
        doNothing().when(userRepository).deleteById("user1");

        userService.deleteUser("user1");

        // Verify that existsById was called once and deleteById was called exactly once.
        verify(userRepository, times(1)).existsById("user1");
        verify(userRepository, times(1)).deleteById("user1");
    }

    @Test
    void deleteUser_UserNotFound() {
        when(userRepository.existsById("nonexistent")).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.deleteUser("nonexistent");
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User with ID nonexistent not found.", exception.getReason());
        verify(userRepository, never()).deleteById(anyString()); // Verify deleteById was not called
    }

    @Test
    void getAllUsers_SortedByScore() {
        User user1 = new User("user1", "Alice");
        user1.setScore(50);
        User user2 = new User("user2", "Bob");
        user2.setScore(10);
        User user3 = new User("user3", "Charlie");
        user3.setScore(90);

        // Mock repository to return users already sorted by score.
        // The repository method is expected to handle the sorting.
        when(userRepository.findAllByOrderByScoreAsc()).thenReturn(Arrays.asList(user2, user1, user3));

        List<User> retrievedUsers = userService.getAllUsers();

        assertNotNull(retrievedUsers);
        assertEquals(3, retrievedUsers.size());
        // Verify the order of users is by ascending score.
        assertEquals("user2", retrievedUsers.get(0).getUserId()); // Score 10
        assertEquals("user1", retrievedUsers.get(1).getUserId()); // Score 50
        assertEquals("user3", retrievedUsers.get(2).getUserId()); // Score 90
    }

    @Test
    void getAllUsers_NoUsers() {
        when(userRepository.findAllByOrderByScoreAsc()).thenReturn(Collections.emptyList());

        List<User> retrievedUsers = userService.getAllUsers();

        assertNotNull(retrievedUsers);
        assertTrue(retrievedUsers.isEmpty());
    }
}
