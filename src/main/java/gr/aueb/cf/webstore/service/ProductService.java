package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.core.filters.ProductFilters;
import gr.aueb.cf.webstore.dto.*;
import gr.aueb.cf.webstore.mapper.Mapper;
import gr.aueb.cf.webstore.model.Category;
import gr.aueb.cf.webstore.model.Product;
import gr.aueb.cf.webstore.model.ProductSpec;
import gr.aueb.cf.webstore.repository.CategoryRepository;
import gr.aueb.cf.webstore.repository.ProductRepository;
import gr.aueb.cf.webstore.repository.ProductSpecRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ProductSpecRepository productSpecRepository;
    private final CategoryRepository categoryRepository;
    private final Mapper mapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductSpecRepository productSpecRepository, CategoryRepository categoryRepository, Mapper mapper) {
        this.productRepository = productRepository;
        this.productSpecRepository = productSpecRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductReadOnlyDTO saveProduct(ProductInsertDTO productInsertDTO) throws AppObjectAlreadyExists, AppObjectNotFoundException, AppObjectInvalidArgumentException {

        if (productRepository.findBySku(productInsertDTO.sku()).isPresent()) {
            throw new AppObjectAlreadyExists("Sku", "Product with SKU " + productInsertDTO.sku() + " already exists");
        }

        Category category = categoryRepository.findById(productInsertDTO.categoryId()).orElseThrow(() -> new AppObjectNotFoundException("Category", "Category with id " + productInsertDTO.categoryId() + " not found"));

        Product product = mapper.mapToProductEntity(productInsertDTO, category);
        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully. id={}, sku={}", savedProduct.getId(), savedProduct.getSku());

        return mapper.mapToProductReadOnlyDTO(savedProduct);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductReadOnlyDTO updateProduct(ProductUpdateDTO productUpdateDTO)
            throws AppObjectAlreadyExists, AppObjectNotFoundException, AppObjectInvalidArgumentException {

        Product existingProduct = productRepository.findById(productUpdateDTO.id())
                .orElseThrow(() -> new AppObjectNotFoundException(
                        "Product", "Product with id " + productUpdateDTO.id() + " not found"));


        if (productUpdateDTO.sku() != null && !productUpdateDTO.sku().equals(existingProduct.getSku()) && productRepository.findBySku(productUpdateDTO.sku()).isPresent()) {
            throw new AppObjectAlreadyExists("Sku", "Product with SKU " + productUpdateDTO.sku() + " already exists");
        }

        if (productUpdateDTO.name() != null) existingProduct.setName(productUpdateDTO.name());

        if (productUpdateDTO.description() != null) existingProduct.setDescription(productUpdateDTO.description());

        if (productUpdateDTO.price() != null) existingProduct.setPrice(productUpdateDTO.price());

        if (productUpdateDTO.stock() != null) existingProduct.setStock(productUpdateDTO.stock());

        if (productUpdateDTO.isActive() != null) existingProduct.setIsActive(productUpdateDTO.isActive());

        if (productUpdateDTO.brand() != null) existingProduct.setBrand(productUpdateDTO.brand());

        if (productUpdateDTO.image() != null) existingProduct.setImage(productUpdateDTO.image());

        if (productUpdateDTO.sku() != null) existingProduct.setSku(productUpdateDTO.sku());

        Product updatedProduct = productRepository.save(existingProduct);

        log.info("Product with id = {} updated successfully.", updatedProduct.getId());

        return mapper.mapToProductReadOnlyDTO(updatedProduct);
    }

    @Override
    public ProductReadOnlyDTO getOneProduct(Long id) throws AppObjectNotFoundException {

        return productRepository.findById(id)
                .map(mapper::mapToProductReadOnlyDTO)
                .orElseThrow(() -> new AppObjectNotFoundException("Product", "Product with id " + id + " not found"));
    }

    @Override
    public Paginated<ProductReadOnlyDTO> getPaginatedProducts(int page, int size) {

        String defaultSort = "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());

        Page<Product> pagedProducts = productRepository.findAll(pageable);
        log.debug("Paginated products returned successfully with page={} and size={}", page, size);

        return Paginated.fromPage(pagedProducts.map(mapper::mapToProductReadOnlyDTO));
    }

    @Override
    public Paginated<ProductReadOnlyDTO> getProductsFilteredPaginated(ProductFilters productFilters) {

        var filtered = productRepository.findAll(productFilters.getPageable());

        log.debug("Filtered and paginated products returned successfully with page={} and size={}", productFilters.getPage(), productFilters.getPageSize());

        return Paginated.fromPage(filtered.map(mapper::mapToProductReadOnlyDTO));
    }

    @Override
    public List<ProductSpecReadOnlyDTO> getProductSpecs(Long productId)
            throws AppObjectNotFoundException {


        productRepository.findById(productId).orElseThrow(() -> new AppObjectNotFoundException("Product", "Product with id " + productId + " not found"));

        List<ProductSpec> specs = productSpecRepository.findByProductId(productId);

        return specs.stream()
                .map(spec -> new ProductSpecReadOnlyDTO(
                        spec.getId(),
                        spec.getName(),
                        spec.getValue(),
                        spec.getProduct().getId())).toList();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductSpecReadOnlyDTO addProductSpec(Long productId, ProductSpecInsertDTO dto) throws AppObjectNotFoundException, AppObjectAlreadyExists {

        Product product = productRepository.findById(productId).orElseThrow(() -> new AppObjectNotFoundException(
                "Product", "Product with id " + productId + " not found")
        );


        boolean duplicateName = product.getProductSpecs().stream().anyMatch(s -> s.getName().equalsIgnoreCase(dto.name()));

        if (duplicateName) throw new AppObjectAlreadyExists("ProductSpec", "Spec with name '" + dto.name() + "' already exists for this product");

        ProductSpec spec = new ProductSpec();
        spec.setProduct(product);
        spec.setName(dto.name());
        spec.setValue(dto.value());

        ProductSpec savedSpec = productSpecRepository.save(spec);

        log.info("ProductSpec created successfully. id={}, productId={}", savedSpec.getId(), productId);

        return new ProductSpecReadOnlyDTO(
                savedSpec.getId(),
                savedSpec.getName(),
                savedSpec.getValue(),
                savedSpec.getProduct().getId()
        );
    }
}
