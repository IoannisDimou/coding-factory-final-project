package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.core.filters.UserFilters;
import gr.aueb.cf.webstore.dto.UserInsertDTO;
import gr.aueb.cf.webstore.dto.UserReadOnlyDTO;
import gr.aueb.cf.webstore.dto.UserUpdateDTO;

public interface IUserService {

    UserReadOnlyDTO saveUser(UserInsertDTO userInsertDTO) throws AppObjectAlreadyExists, AppObjectInvalidArgumentException;

    UserReadOnlyDTO updateUser(UserUpdateDTO userUpdateDTO) throws AppObjectAlreadyExists, AppObjectNotFoundException, AppObjectInvalidArgumentException;

    UserReadOnlyDTO getUser(String uuid) throws AppObjectNotFoundException;

    Paginated<UserReadOnlyDTO> getPaginatedUsers(int page, int size);

    Paginated<UserReadOnlyDTO> getUsersFilteredPaginated(UserFilters userFilters);
}
