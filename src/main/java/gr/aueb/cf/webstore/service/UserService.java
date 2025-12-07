package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.core.filters.UserFilters;
import gr.aueb.cf.webstore.core.specifications.UserSpecification;
import gr.aueb.cf.webstore.dto.UserInsertDTO;
import gr.aueb.cf.webstore.dto.UserReadOnlyDTO;
import gr.aueb.cf.webstore.dto.UserUpdateDTO;
import gr.aueb.cf.webstore.mapper.Mapper;
import gr.aueb.cf.webstore.model.User;
import gr.aueb.cf.webstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final Mapper mapper;
    private final IEmailVerificationService emailVerificationService;


    @Autowired
    public UserService(UserRepository userRepository, Mapper mapper, IEmailVerificationService emailVerificationService) {

        this.userRepository = userRepository;
        this.mapper = mapper;
        this.emailVerificationService = emailVerificationService;
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO saveUser(UserInsertDTO userInsertDTO) throws AppObjectAlreadyExists {

        var existingByEmail = userRepository.findByEmail(userInsertDTO.email());

        if (existingByEmail.isPresent()) {

            User existing = existingByEmail.get();

            if (Boolean.TRUE.equals(existing.getEmailVerified())) {
                throw new AppObjectAlreadyExists("Email","User with email " + userInsertDTO.email() + " already exists");
            }

            if (userInsertDTO.phoneNumber() != null && userRepository.findByPhoneNumber(userInsertDTO.phoneNumber())

                            .filter(u -> !u.getId().equals(existing.getId()))
                            .isPresent()) {
                throw new AppObjectAlreadyExists("PhoneNumber","User with phone " + userInsertDTO.phoneNumber() + " already exists");
            }

            mapper.mapToUserEntity(userInsertDTO, existing);
            existing.setIsActive(false);
            existing.setEmailVerified(false);

            User savedUser = userRepository.save(existing);

            log.info("Unverified user updated and verification re-sent. email={}, phone={}", savedUser.getEmail(), savedUser.getPhoneNumber());

            emailVerificationService.createAndSendToken(savedUser);

            return mapper.mapToUserReadOnlyDTO(savedUser);
        }

        if (userInsertDTO.phoneNumber() != null && userRepository.findByPhoneNumber(userInsertDTO.phoneNumber()).isPresent()) {
            throw new AppObjectAlreadyExists("PhoneNumber","User with phone " + userInsertDTO.phoneNumber() + " already exists");
        }

        User user = mapper.mapToUserEntity(userInsertDTO);
        user.setIsActive(false);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        log.info("New user created successfully. email={}, phone={}",savedUser.getEmail(), savedUser.getPhoneNumber());

        emailVerificationService.createAndSendToken(savedUser);

        return mapper.mapToUserReadOnlyDTO(savedUser);
    }


    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO updateUser(UserUpdateDTO userUpdateDTO) throws AppObjectAlreadyExists, AppObjectNotFoundException {

        User existingUser = userRepository.findByUuid(userUpdateDTO.uuid()).orElseThrow(
                () -> new AppObjectNotFoundException("User", "User with uuid " + userUpdateDTO.uuid() + " not found"));

        if(!userUpdateDTO.email().equals(existingUser.getEmail()) && userRepository.findByEmail(userUpdateDTO.email()).isPresent()) {
            throw new AppObjectAlreadyExists("Email", "User with email " + userUpdateDTO.email() + " already exists");
        }

        if (userUpdateDTO.phoneNumber() != null && !userUpdateDTO.phoneNumber().equals(existingUser.getPhoneNumber()) &&
                userRepository.findByPhoneNumber(userUpdateDTO.phoneNumber()).isPresent()) { throw new AppObjectAlreadyExists(
                        "PhoneNumber", "User with phone " + userUpdateDTO.phoneNumber() + " already exists");
        }

        mapper.mapToUserEntity(userUpdateDTO, existingUser);
        User updatedUser = userRepository.save(existingUser);
        log.info("User with id={} updated successfully.", updatedUser.getId());

        return mapper.mapToUserReadOnlyDTO(updatedUser);

    }

    @Override
    public UserReadOnlyDTO getUser(String uuid) throws AppObjectNotFoundException {
        return userRepository.findByUuid(uuid)
                .map(mapper::mapToUserReadOnlyDTO)
                .orElseThrow(() -> new AppObjectNotFoundException("User", "User with uuid " + uuid + " not found"));
    }

    @Override
    public Paginated<UserReadOnlyDTO> getPaginatedUsers(int page, int size) {
        String defaultSort = "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());

        Page<User> pagedUsers = userRepository.findAll(pageable);
        log.debug("Paginated users returned successfully with page={} and size={}", page, size);

        return Paginated.fromPage(pagedUsers.map(mapper::mapToUserReadOnlyDTO));
    }

    @Override
    public Paginated<UserReadOnlyDTO> getUsersFilteredPaginated(UserFilters userFilters) {
        var filtered = userRepository.findAll(getSpecsFromFilters(userFilters), userFilters.getPageable());

        log.debug("Filtered and paginated users returned successfully with page={} and size={}", userFilters.getPage(),
                userFilters.getPageSize());

        return Paginated.fromPage(filtered.map(mapper::mapToUserReadOnlyDTO));
    }

    private Specification<User> getSpecsFromFilters(UserFilters filters) {
        return UserSpecification.stringFieldLike("uuid", filters.getUuid())
                .and(UserSpecification.stringFieldLike("firstname", filters.getFirstname()))
                .and(UserSpecification.stringFieldLike("lastname", filters.getLastname()))
                .and(UserSpecification.stringFieldLike("email", filters.getEmail()))
                .and(UserSpecification.userIsActive(filters.getIsActive()))
                .and(UserSpecification.userRoleIs(filters.getRole()));
    }

}
