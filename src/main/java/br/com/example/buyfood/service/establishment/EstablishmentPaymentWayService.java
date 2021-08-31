package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.PaymentWayRequestDTO;
import br.com.example.buyfood.model.dto.response.PaymentWayResponseDTO;
import br.com.example.buyfood.model.entity.PaymentWayEntity;
import br.com.example.buyfood.model.repository.PaymentWayRepository;
import br.com.example.buyfood.util.StatusValidation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EstablishmentPaymentWayService {

  private final ModelMapper modelMapper;

  private final PaymentWayRepository paymentWayRepository;

  private final StatusValidation statusValidation;

  @Autowired
  public EstablishmentPaymentWayService(
      ModelMapper modelMapper,
      PaymentWayRepository paymentWayRepository,
      StatusValidation statusValidation) {
    this.modelMapper = modelMapper;
    this.paymentWayRepository = paymentWayRepository;
    this.statusValidation = statusValidation;
  }

  public List<PaymentWayResponseDTO> getPaymentWayList(Integer status) {
    if (status == null) {
      return paymentWayRepository.findAll().stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      return paymentWayRepository
          .findAllByStatus(statusValidation.getStatusIdentification(status))
          .stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
  }

  public PaymentWayResponseDTO getPaymentWay(Long id) {
    return paymentWayRepository
        .findById(id)
        .map(this::convertToDto)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.PAYMENT_WAY_NOT_FOUND));
  }

  public PaymentWayResponseDTO createPaymentWay(PaymentWayRequestDTO paymentWayRequestDTO) {
    var convertedPaymentWayEntity = convertToEntity(paymentWayRequestDTO);
    return convertToDto(paymentWayRepository.save(convertedPaymentWayEntity));
  }

  public void updatePaymentWay(Long id, PaymentWayRequestDTO paymentWayRequestDTO) {
    var convertedPaymentWayEntity = convertToEntity(paymentWayRequestDTO);
    convertedPaymentWayEntity.setId(id);
    paymentWayRepository.save(convertedPaymentWayEntity);
  }

  public void deletePaymentWay(Long id) {
    var convertedPaymentWayEntity = getPaymentWayById(id);
    convertedPaymentWayEntity.setStatus(RegisterStatus.DISABLED.getValue());
    paymentWayRepository.save(convertedPaymentWayEntity);
  }

  public PaymentWayEntity getPaymentWayById(Long id) {
    return paymentWayRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.PAYMENT_WAY_NOT_FOUND));
  }

  private PaymentWayResponseDTO convertToDto(PaymentWayEntity paymentWayEntity) {
    return modelMapper.map(paymentWayEntity, PaymentWayResponseDTO.class);
  }

  private PaymentWayEntity convertToEntity(PaymentWayRequestDTO paymentWayRequestDTO) {
    return modelMapper.map(paymentWayRequestDTO, PaymentWayEntity.class);
  }
}
