package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.IngredientRequestDTO;
import br.com.example.buyfood.model.dto.response.IngredientResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.IngredientEntity;
import br.com.example.buyfood.model.entity.ProductEntity;
import br.com.example.buyfood.model.repository.IngredientRepository;
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
public class EstablishmentIngredientService {

  private final ModelMapper modelMapper;

  private final ProductRepository productRepository;

  private final IngredientRepository ingredientRepository;

  private final EstablishmentService establishmentService;

  private final UserService userService;

  private final StatusValidation statusValidation;

  @Autowired
  public EstablishmentIngredientService(
      ModelMapper modelMapper,
      ProductRepository productRepository,
      IngredientRepository ingredientRepository,
      EstablishmentService establishmentService,
      UserService userService,
      StatusValidation statusValidation) {
    this.modelMapper = modelMapper;
    this.productRepository = productRepository;
    this.ingredientRepository = ingredientRepository;
    this.establishmentService = establishmentService;
    this.userService = userService;
    this.statusValidation = statusValidation;
  }

  public List<IngredientResponseDTO> getIngredientList(
      Long establishmentId, Long productId, Integer status) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    if (status == null) {
      return ingredientRepository.findAllByProductId(productId).stream()
          .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
          .map(this::convertToDto)
          .collect(Collectors.toList());
    } else {
      return ingredientRepository
          .findAllByProductIdAndStatus(productId, statusValidation.getStatusIdentification(status))
          .stream()
          .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
  }

  public IngredientResponseDTO getIngredient(
      Long establishmentId, Long productId, Long ingredientId) {
    return convertToDto(
        getIngredientByEstablishmentIdAndProductIdAndId(establishmentId, productId, ingredientId));
  }

  public IngredientResponseDTO createIngredient(
      Long establishmentId, Long productId, IngredientRequestDTO ingredientRequestDTO) {
    var productEntity = getProductByEstablishmentIdAndProductId(establishmentId, productId);
    var convertedIngredientEntity = convertToEntity(ingredientRequestDTO);
    convertedIngredientEntity.setProduct(productEntity);
    return convertToDto(ingredientRepository.save(convertedIngredientEntity));
  }

  public void updateIngredient(
      Long establishmentId,
      Long productId,
      Long ingredientId,
      IngredientRequestDTO ingredientRequestDTO) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    validUserOwnerOfEstablishment(establishment);

    var productEntity = getProductByEstablishmentAndProductId(establishment, productId);

    var convertedIngredientEntity = convertToEntity(ingredientRequestDTO);
    convertedIngredientEntity.setId(ingredientId);
    convertedIngredientEntity.setProduct(productEntity);
    ingredientRepository.save(convertedIngredientEntity);
  }

  public void deleteIngredient(Long establishmentId, Long ingredientId) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    validUserOwnerOfEstablishment(establishment);

    var ingredientEntity = getIngredientById(ingredientId);
    ingredientEntity.setStatus(RegisterStatus.DISABLED.getValue());
    ingredientRepository.save(ingredientEntity);
  }

  public IngredientEntity getIngredientById(Long ingredientId) {
    return ingredientRepository
        .findById(ingredientId)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.INGREDIENT_NOT_FOUND));
  }

  public ProductEntity getProductByEstablishmentIdAndProductId(
      Long establishmentId, Long productId) {
    var establishment = establishmentService.getEstablishmentById(establishmentId);
    return productRepository
        .findByEstablishmentAndId(establishment, productId)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
  }

  public ProductEntity getProductByEstablishmentAndProductId(
      EstablishmentEntity establishment, Long productId) {
    return productRepository
        .findByEstablishmentAndId(establishment, productId)
        .orElseThrow(() -> new NotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
  }

  private IngredientEntity getIngredientByEstablishmentIdAndProductIdAndId(
      Long establishmentId, Long productId, Long ingredientId) {
    return ingredientRepository
        .findById(ingredientId)
        .filter(i -> i.getProduct().getId().equals(productId))
        .filter(i -> i.getProduct().getEstablishment().getId().equals(establishmentId))
        .orElseThrow(() -> new NotFoundException(ErrorMessages.INGREDIENT_NOT_FOUND));
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

  private IngredientResponseDTO convertToDto(IngredientEntity ingredientEntity) {
    return modelMapper.map(ingredientEntity, IngredientResponseDTO.class);
  }

  private IngredientEntity convertToEntity(IngredientRequestDTO ingredientRequestDTO) {
    return modelMapper.map(ingredientRequestDTO, IngredientEntity.class);
  }
}
