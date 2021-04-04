package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.PaymentWayRequestDTO;
import br.com.example.buyfood.model.dto.response.PaymentWayResponseDTO;
import br.com.example.buyfood.model.entity.PaymentWayEntity;
import br.com.example.buyfood.model.repository.PaymentWayRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentWayService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PaymentWayRepository paymentWayRepository;

    public List<PaymentWayResponseDTO> getPaymentWayList(Integer status) {
        if (status == null) {
            return paymentWayRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getPaymentWayListByStatus(RegisterStatus.ENABLED);
                case 0: {
                    return getPaymentWayListByStatus(RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public PaymentWayResponseDTO getPaymentWay(Long id) {
        return paymentWayRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Payment way not found"));
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
        return paymentWayRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment way not found"));
    }

    private List<PaymentWayResponseDTO> getPaymentWayListByStatus(RegisterStatus enabled) {
        return paymentWayRepository.findAllByStatus(enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PaymentWayResponseDTO convertToDto(PaymentWayEntity paymentWayEntity) {
        return modelMapper.map(paymentWayEntity, PaymentWayResponseDTO.class);
    }

    private PaymentWayEntity convertToEntity(PaymentWayRequestDTO paymentWayRequestDTO) {
        return modelMapper.map(paymentWayRequestDTO, PaymentWayEntity.class);
    }
}