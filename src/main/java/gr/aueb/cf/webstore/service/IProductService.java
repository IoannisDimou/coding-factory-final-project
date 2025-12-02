package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.core.filters.ProductFilters;
import gr.aueb.cf.webstore.dto.*;

import java.util.List;

public interface IProductService {

    ProductReadOnlyDTO saveProduct(ProductInsertDTO productInsertDTO)
            throws AppObjectAlreadyExists, AppObjectNotFoundException, AppObjectInvalidArgumentException;

    ProductReadOnlyDTO updateProduct(ProductUpdateDTO productUpdateDTO)
            throws AppObjectAlreadyExists, AppObjectNotFoundException, AppObjectInvalidArgumentException;

    ProductReadOnlyDTO getOneProduct(Long id)
            throws AppObjectNotFoundException;

    Paginated<ProductReadOnlyDTO> getPaginatedProducts(int page, int size);

    Paginated<ProductReadOnlyDTO> getProductsFilteredPaginated(ProductFilters productFilters);

    List<ProductSpecReadOnlyDTO> getProductSpecs(Long productId)
            throws AppObjectNotFoundException;

    ProductSpecReadOnlyDTO addProductSpec(Long productId, ProductSpecInsertDTO dto)
            throws AppObjectNotFoundException, AppObjectAlreadyExists;
}
