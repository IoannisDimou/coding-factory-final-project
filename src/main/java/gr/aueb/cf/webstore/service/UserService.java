package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.dto.UserInsertDTO;
import gr.aueb.cf.webstore.dto.UserReadOnlyDTO;
import gr.aueb.cf.webstore.dto.UserUpdateDTO;
import gr.aueb.cf.webstore.mapper.Mapper;
import gr.aueb.cf.webstore.model.User;
import gr.aueb.cf.webstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final Mapper mapper;

    @Autowired
    public UserService(UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO saveUser(UserInsertDTO userInsertDTO) throws AppObjectAlreadyExists {

        if (userRepository.findByEmail(userInsertDTO.email()).isPresent()) {
            throw new AppObjectAlreadyExists("Email", "User with email " + userInsertDTO.email() + " already exists");
        }

        if (userRepository.findByPhoneNumber(userInsertDTO.phoneNumber()).isPresent()) {
            throw new AppObjectAlreadyExists("PhoneNumber", "User with phone " + userInsertDTO.phoneNumber() + " already exists");
        }

        User user = mapper.mapToUserEntity(userInsertDTO);

        User savedUser = userRepository.save(user);

        log.info("User created successfully. email={}, phone={}", user.getEmail(), user.getPhoneNumber());

        return mapper.mapToUserReadOnlyDTO(savedUser);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO updateUser(UserUpdateDTO userUpdateDTO) throws AppObjectAlreadyExists, AppObjectNotFoundException {

        User existingUser = userRepository.findById(userUpdateDTO.id()).orElse(null);
        if (existingUser == null) throw new AppObjectNotFoundException("User", "User with id=" + userUpdateDTO.id() + " not found");


        if (userRepository.findById(userUpdateDTO.id()).isEmpty()) {
            throw new AppObjectNotFoundException("User", "User with id " + userUpdateDTO.id() + " not found");
        }

        if (existingUser == null) throw new AppObjectNotFoundException("User", "User with id=" + userUpdateDTO.id() + " not found");

        if(!existingUser.getEmail().equals(userUpdateDTO.email()) && userRepository.findByEmail(userUpdateDTO.email()).isPresent()) {
            throw new AppObjectAlreadyExists("Email", "User with email " + userUpdateDTO.email() + " already exists");
        }

        if (!userUpdateDTO.phoneNumber().equals(existingUser.getPhoneNumber()) && userRepository.findByPhoneNumber(userUpdateDTO.phoneNumber()).isPresent()) {
            throw new AppObjectAlreadyExists("PhoneNumber", "User with phone " + userUpdateDTO.phoneNumber() + " already exists");
        }

        return mapper.mapToUserReadOnlyDTO(existingUser);
    }
}
