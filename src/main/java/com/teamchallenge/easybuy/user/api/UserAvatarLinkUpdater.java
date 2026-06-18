package com.teamchallenge.easybuy.user.api;

import com.teamchallenge.easybuy.openapi.dto.UserDto;
import com.teamchallenge.easybuy.user.api.avatar.UserAvatarLinkProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for enriching {@link UserDto} objects with a dynamically generated avatar URL.
 * <p>
 * This component acts as a transformer in the service pipeline, ensuring that every
 * user read operation returns an up-to-date link to their profile image.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarLinkUpdater {

    private final UserAvatarLinkProvider userAvatarLinkProvider;

    /**
     * Enriches the provided {@link UserDto} with a public avatar access link.
     * <p>
     * Fetches the current link from the {@link UserAvatarLinkProvider} using
     * the user's ID and updates the DTO's avatar field.
     *
     * @param userDto the user DTO to be enriched
     * @return the enriched {@link UserDto} containing the populated avatar link
     */
    public UserDto update(final UserDto userDto) {
        userDto.setAvatarLink(userAvatarLinkProvider.getLink(userDto.getId()));
        return userDto;
    }
}