package gr.aueb.cf.webstore.mapper;

import gr.aueb.cf.webstore.dto.UserInsertDTO;
import gr.aueb.cf.webstore.dto.UserReadOnlyDTO;
import gr.aueb.cf.webstore.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final PasswordEncoder passwordEncoder;

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(
                user.getId(),
                user.getUuid(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getIsActive()
        );
    }

    public User mapToUserEntity(UserInsertDTO dto) {

        User user = new User();

        user.setIsActive(dto.isActive());
        user.setFirstname(dto.firstname());
        user.setLastname(dto.lastname());
        user.setEmail(dto.email());
        user.


    }

}










