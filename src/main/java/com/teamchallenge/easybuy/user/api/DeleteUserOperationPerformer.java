package com.teamchallenge.easybuy.user.api;

import com.teamchallenge.easybuy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for the deletion of a user account.
 * <p>
 * Encapsulates the operation of removing a user entity from the persistence layer,
 * executing the action within a strictly defined transaction boundary to maintain
 * data integrity.
 */
@Service
@RequiredArgsConstructor
public class DeleteUserOperationPerformer {

    private final UserRepository userRepository;

    /**
     * Deletes the user account associated with the provided unique identifier.
     * <p>
     * Depending on the JPA entity mappings and database constraints, this operation
     * may trigger cascading deletions of related entities (e.g., delivery addresses,
     * user granted authorities).
     *
     * @param userId the unique identifier of the user to be permanently deleted
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void deleteUser(final UUID userId) {
        userRepository.deleteById(userId);
    }
}