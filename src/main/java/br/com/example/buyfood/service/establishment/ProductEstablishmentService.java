package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ProductRequestDTO;
import br.com.example.buyfood.model.dto.response.ProductResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.ProductEntity;
import br.com.example.buyfood.model.repository.ProductRepository;
import br.com.example.buyfood.service.UserService;
import br.com.example.buyfood.util.StatusValidation;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductEstablishmentService {

  private final ModelMapper modelMapper;

  private final ProductRepository productRepository;

  private final EstablishmentService establishmentService;

  private final UserService userService;

  private final StatusValidation statusValidation;

  @Autowired
  public ProductEstablishmentService(
      ModelMapper modelMapper,
      ProductRepository productRepository,
      EstablishmentService establishmentService,
      UserService userService,
      StatusValidation statusValidation) {
    this.modelMapper = modelMapper;
    this.productRepository = productRepository;
    this.establishmentService = establishmentService;
    this.userService = userService;
    this.statusValidation = statusValidation;
  }

  public List<ProductResponseDTO> getProductListByEstablishment(
      Long establishmentId, Integer status) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    if (status == null) {
      return productRepository.findAllByEstablishment(establishment).stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      return productRepository
          .findAllByEstablishmentAndStatus(
              establishment, statusValidation.getStatusIdentification(status))
          .stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
  }

  public ProductResponseDTO getProduct(Long establishmentId, Long productId) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    return productRepository
        .findByEstablishmentAndId(establishment, productId)
        .map(this::convertToDto)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
  }

  public ProductResponseDTO createProduct(
      Long establishmentId, ProductRequestDTO productRequestDto) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    var convertedProductEntity = convertToEntity(productRequestDto);
    convertedProductEntity.setEstablishment(establishment);
    return convertToDto(productRepository.save(convertedProductEntity));
  }

  public void updateProduct(
      Long establishmentId, Long productId, ProductRequestDTO productRequestDto) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    validUserOwnerOfEstablishment(establishment);

    ProductEntity convertedProductEntity = convertToEntity(productRequestDto);
    convertedProductEntity.setId(productId);
    convertedProductEntity.setEstablishment(establishment);
    productRepository.save(convertedProductEntity);
  }

  public void deleteProduct(Long establishmentId, Long productId) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    validUserOwnerOfEstablishment(establishment);

    var productEntity = getProductById(productId);
    productEntity.setStatus(RegisterStatus.DISABLED.getValue());
    productRepository.save(productEntity);
  }

  public ProductEntity getProductById(Long productId) {
    return productRepository
        .findById(productId)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
  }

  private String getUserId() {
    return userService
        .getUserId()
        .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));
  }

  private void validUserOwnerOfEstablishment(EstablishmentEntity establishmentEntity) {
    if (!establishmentEntity.getAudit().getCreatedBy().equals(getUserId())) {
      throw new ForbiddenException(ErrorMessages.USER_IS_NOT_OWNER_OF_ESTABLISHMENT);
    }
  }

  private ProductResponseDTO convertToDto(ProductEntity productEntity) {
    return modelMapper.map(productEntity, ProductResponseDTO.class);
  }

  private ProductEntity convertToEntity(ProductRequestDTO productRequestDto) {
    return modelMapper.map(productRequestDto, ProductEntity.class);
  }
}
