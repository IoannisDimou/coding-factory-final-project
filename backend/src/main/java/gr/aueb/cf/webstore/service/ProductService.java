package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.core.filters.ProductFilters;
import gr.aueb.cf.webstore.core.specifications.ProductSpecification;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

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
    public ProductReadOnlyDTO saveProduct(ProductInsertDTO productInsertDTO, MultipartFile imageFile) throws AppObjectAlreadyExists, AppObjectNotFoundException, IOException, AppObjectInvalidArgumentException {

        if (productRepository.findBySku(productInsertDTO.sku()).isPresent()) {
            throw new AppObjectAlreadyExists("Sku", "Product with SKU " + productInsertDTO.sku() + " already exists");
        }

        Category category = categoryRepository.findById(productInsertDTO.categoryId()).orElseThrow(() -> new AppObjectNotFoundException("Category", "Category with id " + productInsertDTO.categoryId() + " not found"));

        Product product = mapper.mapToProductEntity(productInsertDTO, category);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveProductImage(imageFile);
            product.setImage(imagePath);
        }

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully. id={}, sku={}", savedProduct.getId(), savedProduct.getSku());

        return mapper.mapToProductReadOnlyDTO(savedProduct);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductReadOnlyDTO updateProduct(ProductUpdateDTO productUpdateDTO, MultipartFile imageFile)
            throws AppObjectAlreadyExists, AppObjectNotFoundException, AppObjectInvalidArgumentException, IOException {

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

        if (productUpdateDTO.sku() != null) existingProduct.setSku(productUpdateDTO.sku());

        if (imageFile != null && !imageFile.isEmpty()) {

            if (existingProduct.getImage() != null) {
                deleteImageFile(existingProduct.getImage());
            }
            String imagePath = saveProductImage(imageFile);
            existingProduct.setImage(imagePath);
        }

        Product updatedProduct = productRepository.save(existingProduct);

        log.info("Product with id = {} updated successfully.", updatedProduct.getId());

        return mapper.mapToProductReadOnlyDTO(updatedProduct);
    }

    @Override
    @Transactional
    public ProductReadOnlyDTO getOneProduct(Long id) throws AppObjectNotFoundException {

        return productRepository.findById(id)
                .map(mapper::mapToProductReadOnlyDTO)
                .orElseThrow(() -> new AppObjectNotFoundException("Product", "Product with id " + id + " not found"));
    }

    @Override
    @Transactional
    public Paginated<ProductReadOnlyDTO> getPaginatedProducts(int page, int size) {

        String defaultSort = "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());

        Page<Product> pagedProducts = productRepository.findAll(pageable);
        log.debug("Paginated products returned successfully with page={} and size={}", page, size);

        return Paginated.fromPage(pagedProducts.map(mapper::mapToProductReadOnlyDTO));
    }

    @Override
    @Transactional
    public Paginated<ProductReadOnlyDTO> getProductsFilteredPaginated(ProductFilters productFilters) {

        var filtered = productRepository.findAll(getSpecsFromFilters(productFilters), productFilters.getPageable());

        log.debug("Filtered and paginated products returned successfully with page={} and size={}", productFilters.getPage(), productFilters.getPageSize());

        return Paginated.fromPage(filtered.map(mapper::mapToProductReadOnlyDTO));
    }

    private Specification<Product> getSpecsFromFilters(ProductFilters filters) {

        return ProductSpecification.stringFieldLike("name", filters.getName())
                .and(ProductSpecification.categoryNameLike(filters.getCategory()))
                .and(ProductSpecification.priceBetween(filters.getMinPrice(), filters.getMaxPrice()))
                .and(ProductSpecification.brandLike(filters.getBrand()))
                .and(ProductSpecification.specLike(filters.getSpecName(), filters.getSpecValue()))
                .and(ProductSpecification.isActive(filters.getIsActive()));
    }

    @Override
    @Transactional
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

    private String saveProductImage(MultipartFile imageFile) throws IOException {

        if (imageFile == null || imageFile.isEmpty()) return null;

        String originalFileName = imageFile.getOriginalFilename();
        String savedName = UUID.randomUUID().toString() + getFileExtension(originalFileName);

        Path uploadDir = Paths.get("uploads");
        Files.createDirectories(uploadDir);

        Path filePath = uploadDir.resolve(savedName);

        try (InputStream inputStream = imageFile.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("Image saved to {}", filePath.toAbsolutePath());

        return "/uploads/" + savedName;
    }

    private String getFileExtension(String filename) {

        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }

        return "";
    }

    private void deleteImageFile(String imagePath) {
        try {
            String fileName = Paths.get(imagePath).getFileName().toString();
            Path uploadDir = Paths.get("uploads");
            Path filePath = uploadDir.resolve(fileName);

            Files.deleteIfExists(filePath);

            log.info("Deleted old image file {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            log.warn("Failed to delete old image {}", imagePath, e);
        }
    }
}
