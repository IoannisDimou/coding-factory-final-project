package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.OrderFilters;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.dto.OrderInsertDTO;
import gr.aueb.cf.webstore.dto.OrderReadOnlyDTO;
import gr.aueb.cf.webstore.dto.OrderUpdateDTO;

public interface IOrderService {

    OrderReadOnlyDTO createOrder(OrderInsertDTO orderInsertDTO) throws AppObjectNotFoundException, AppObjectInvalidArgumentException;

    OrderReadOnlyDTO getOneOrder(Long id) throws AppObjectNotFoundException, AppObjectNotAuthorizedException;

    Paginated<OrderReadOnlyDTO> getPaginatedOrders(int page, int size);

    Paginated<OrderReadOnlyDTO> getOrdersFilteredPaginated(OrderFilters orderFilters);

    OrderReadOnlyDTO updateOrderStatus(OrderUpdateDTO orderUpdateDTO) throws AppObjectNotFoundException, AppObjectInvalidArgumentException;

    OrderReadOnlyDTO getOneOrderByCode(String orderCode) throws AppObjectNotFoundException, AppObjectNotAuthorizedException;

}
