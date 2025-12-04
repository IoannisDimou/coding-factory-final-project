package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.dto.PaymentConfirmationDTO;
import gr.aueb.cf.webstore.dto.PaymentReadOnlyDTO;
import gr.aueb.cf.webstore.dto.PaymentRequestDTO;

import java.util.List;

public interface IPaymentService {

    PaymentReadOnlyDTO createPayment(PaymentRequestDTO paymentRequestDTO) throws AppObjectNotFoundException, AppObjectInvalidArgumentException;

    PaymentReadOnlyDTO confirmPayment(PaymentConfirmationDTO paymentConfirmationDTO) throws AppObjectNotFoundException, AppObjectInvalidArgumentException;

    PaymentReadOnlyDTO getPayment(Long id) throws AppObjectNotFoundException;

    List<PaymentReadOnlyDTO> getPaymentsForOrder(Long orderId) throws AppObjectNotFoundException;

    Paginated<PaymentReadOnlyDTO> getPaginatedPayments(int page, int size);
}
