package br.com.example.buyfood.service;

import br.com.example.buyfood.model.dto.response.product.EstablishmentProductResponseDTO;
import br.com.example.buyfood.model.entity.ProductEntity;
import br.com.example.buyfood.model.repository.ProductRepository;
import br.com.example.buyfood.util.StatusValidation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductUserService {

  private final ModelMapper modelMapper;

  private final ProductRepository productRepository;

  private final StatusValidation statusValidation;

  @Autowired
  public ProductUserService(
      ModelMapper modelMapper,
      ProductRepository productRepository,
      StatusValidation statusValidation) {
    this.modelMapper = modelMapper;
    this.productRepository = productRepository;
    this.statusValidation = statusValidation;
  }

  public List<EstablishmentProductResponseDTO> getProductList(Integer status) {
    if (status == null) {
      return productRepository.findAll().stream()
          .map(this::convertToEstablishmentProductResponseDto)
          .collect(Collectors.toList());
    } else {
      return productRepository
          .findAllByStatus(statusValidation.getStatusIdentification(status))
          .stream()
          .map(this::convertToEstablishmentProductResponseDto)
          .collect(Collectors.toList());
    }
  }

  private EstablishmentProductResponseDTO convertToEstablishmentProductResponseDto(
      ProductEntity productEntity) {
    return modelMapper.map(productEntity, EstablishmentProductResponseDTO.class);
  }
}
