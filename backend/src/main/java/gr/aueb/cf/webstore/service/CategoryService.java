package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.CategoryFilters;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.dto.CategoryInsertDTO;
import gr.aueb.cf.webstore.dto.CategoryReadOnlyDTO;
import gr.aueb.cf.webstore.dto.CategoryUpdateDTO;
import gr.aueb.cf.webstore.mapper.Mapper;
import gr.aueb.cf.webstore.model.Category;
import gr.aueb.cf.webstore.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final Mapper mapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, Mapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CategoryReadOnlyDTO saveCategory(CategoryInsertDTO categoryInsertDTO) throws AppObjectAlreadyExists {

        if (categoryRepository.findByName(categoryInsertDTO.name()).isPresent()) {
            throw new AppObjectAlreadyExists("Category", "Category with name " + categoryInsertDTO.name() + " already exists");
        }

        Category category = new Category();
        category.setName(categoryInsertDTO.name());
        category.setIsActive(categoryInsertDTO.isActive() != null ? categoryInsertDTO.isActive() : Boolean.TRUE);

        Category savedCategory = categoryRepository.save(category);

        log.info("Category created successfully. id={}, name={}", savedCategory.getId(), savedCategory.getName());

        return mapper.mapToCategoryReadOnlyDTO(savedCategory);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CategoryReadOnlyDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO) throws AppObjectAlreadyExists, AppObjectNotFoundException {

        Category existingCategory = categoryRepository.findById(categoryUpdateDTO.id()).orElseThrow(() -> new AppObjectNotFoundException(
                        "Category", "Category with id " + categoryUpdateDTO.id() + " not found"));

        if (categoryUpdateDTO.name() != null && !categoryUpdateDTO.name().equals(existingCategory.getName()) &&
                categoryRepository.findByName(categoryUpdateDTO.name()).isPresent()) {throw new AppObjectAlreadyExists(
                    "Category", "Category with name " + categoryUpdateDTO.name() + " already exists");
        }

        if (categoryUpdateDTO.name() != null) existingCategory.setName(categoryUpdateDTO.name());

        if (categoryUpdateDTO.isActive() != null) existingCategory.setIsActive(categoryUpdateDTO.isActive());


        Category updatedCategory = categoryRepository.save(existingCategory);

        log.info("Category with id={} updated successfully.", updatedCategory.getId());

        return mapper.mapToCategoryReadOnlyDTO(updatedCategory);
    }

    @Override
    public CategoryReadOnlyDTO getOneCategory(Long id) throws AppObjectNotFoundException {

        return categoryRepository.findById(id)
                .map(mapper::mapToCategoryReadOnlyDTO)
                .orElseThrow(() -> new AppObjectNotFoundException(
                        "Category",
                        "Category with id " + id + " not found"
                ));
    }

    @Override
    public Paginated<CategoryReadOnlyDTO> getPaginatedCategories(int page, int size) {
        String defaultSort = "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());

        Page<Category> pagedCategories = categoryRepository.findAll(pageable);
        log.debug("Paginated categories returned successfully with page={} and size={}", page, size);

        return Paginated.fromPage(pagedCategories.map(mapper::mapToCategoryReadOnlyDTO));
    }

    @Override
    public Paginated<CategoryReadOnlyDTO> getCategoriesFilteredPaginated(CategoryFilters categoryFilters) {

        var page = categoryRepository.findAll(categoryFilters.getPageable());

        log.debug("Filtered and paginated categories returned successfully with page={} and size={}",
                categoryFilters.getPage(), categoryFilters.getPageSize());

        return Paginated.fromPage(page.map(mapper::mapToCategoryReadOnlyDTO));
    }
}

