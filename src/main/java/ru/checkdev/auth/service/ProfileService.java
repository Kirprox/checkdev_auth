package ru.checkdev.auth.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.dto.ProfileDTO;
import ru.checkdev.auth.dto.ProfileTgDTO;
import ru.checkdev.auth.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

/**
 * CheckDev пробное собеседование
 * Класс получения ProfileDTO
 *
 * @author Dmitry Stepanov
 * @version 22.09.2023'T'23:41
 */

@Service
@AllArgsConstructor
@Slf4j
public class ProfileService {
    private final PersonRepository personRepository;
    private final PasswordEncoder encoding;
    private final CircuitBreaker circuitBreaker = new CircuitBreaker(5);

    /**
     * Получить ProfileDTO по ID
     *
     * @param id int
     * @return ProfileDTO
     */
    public Optional<ProfileDTO> findProfileByID(int id) {
        return circuitBreaker.exec(
                () -> Optional.ofNullable(personRepository.findProfileById(id)), null
        );
    }

    /**
     * Получить ProfileTgDTO по ID
     *
     * @param id int
     * @return ProfileDTO
     */
    public Optional<ProfileTgDTO> findProfileTgByID(int id) {
        return circuitBreaker.exec(
                () -> Optional.ofNullable(personRepository.findProfileTgById(id)),
                Optional.empty()
        );
    }

    public Optional<ProfileTgDTO> findProfileTgByEmailAndPassword(String email, String password) {
        Optional<ProfileTgDTO> result = Optional.empty();
        Profile profile = circuitBreaker.exec(
                () -> personRepository.findByEmail(email), null
        );
        if (profile != null && encoding.matches(password, profile.getPassword())) {
            result = Optional.of(new ProfileTgDTO(
                    profile.getId(),
                    profile.getUsername(),
                    profile.getEmail()));
        }
        return result;
    }

    /**
     * Получить список всех PersonDTO
     *
     * @return List<PersonDTO>
     */
    public List<ProfileDTO> findProfilesOrderByCreatedDesc() {
        return circuitBreaker.exec(
                () -> personRepository.findProfileOrderByCreatedDesc(), null
        );
    }
}
