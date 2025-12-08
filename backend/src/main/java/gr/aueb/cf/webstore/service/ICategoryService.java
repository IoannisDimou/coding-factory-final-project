package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.CategoryFilters;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.dto.CategoryInsertDTO;
import gr.aueb.cf.webstore.dto.CategoryReadOnlyDTO;
import gr.aueb.cf.webstore.dto.CategoryUpdateDTO;

public interface ICategoryService {

    CategoryReadOnlyDTO saveCategory(CategoryInsertDTO categoryInsertDTO)
            throws AppObjectAlreadyExists;

    CategoryReadOnlyDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO)
            throws AppObjectAlreadyExists, AppObjectNotFoundException;

    CategoryReadOnlyDTO getOneCategory(Long id)
            throws AppObjectNotFoundException;

    Paginated<CategoryReadOnlyDTO> getPaginatedCategories(int page, int size);

    Paginated<CategoryReadOnlyDTO> getCategoriesFilteredPaginated(CategoryFilters categoryFilters);
}
