package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.IngredientRequestDTO;
import br.com.example.buyfood.model.dto.response.IngredientResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.IngredientEntity;
import br.com.example.buyfood.model.entity.ProductEntity;
import br.com.example.buyfood.model.repository.IngredientRepository;
import br.com.example.buyfood.model.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.ForbiddenException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IngredientService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private UserService userService;

    public List<IngredientResponseDTO> getIngredientList(Long establishmentId, Long productId, Integer status) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        if (status == null) {
            return getIngredientListByEstablishmentIdAndProductId(establishment, productId);
        } else {
            switch (status) {
                case 1:
                    return getIngredientListByEstablishmentIdProductIdAndStatus(establishment, productId, RegisterStatus.ENABLED);
                case 0: {
                    return getIngredientListByEstablishmentIdProductIdAndStatus(establishment, productId, RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public IngredientResponseDTO getIngredient(Long establishmentId, Long productId, Long ingredientId) {
        return convertToDto(getIngredientByEstablishmentIdAndProductIdAndId(establishmentId, productId, ingredientId));
    }

    public IngredientResponseDTO createIngredient(Long establishmentId, Long productId, IngredientRequestDTO ingredientRequestDTO) {
        var productEntity = getProductByEstablishmentIdAndProductId(establishmentId, productId);
        var convertedIngredientEntity = convertToEntity(ingredientRequestDTO);
        convertedIngredientEntity.setProduct(productEntity);
        return convertToDto(ingredientRepository.save(convertedIngredientEntity));
    }

    public void updateIngredient(Long establishmentId, Long productId, Long ingredientId,
                                 IngredientRequestDTO ingredientRequestDTO) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        validUserOwnerOfEstablishment(establishment);

        var productEntity = getProductByEstablishmentAndProductId(establishment, productId);

        var convertedIngredientEntity = convertToEntity(ingredientRequestDTO);
        convertedIngredientEntity.setId(ingredientId);
        convertedIngredientEntity.setProduct(productEntity);
        ingredientRepository.save(convertedIngredientEntity);
    }

    public void deleteIngredient(Long establishmentId, Long productId, Long ingredientId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        validUserOwnerOfEstablishment(establishment);

        var ingredientEntity = getIngredientById(ingredientId);
        ingredientEntity.setStatus(RegisterStatus.DISABLED.getValue());
        ingredientRepository.save(ingredientEntity);
    }

    public IngredientEntity getIngredientById(Long ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
    }

    public ProductEntity getProductByEstablishmentIdAndProductId(Long establishmentId, Long productId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return productRepository.findByEstablishmentAndId(establishment, productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public ProductEntity getProductByEstablishmentAndProductId(EstablishmentEntity establishment, Long productId) {
        return productRepository.findByEstablishmentAndId(establishment, productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private IngredientEntity getIngredientByEstablishmentIdAndProductIdAndId(Long establishmentId, Long productId,
                                                                             Long ingredientId) {
        return ingredientRepository.findById(ingredientId)
                .filter(i -> i.getProduct().getId().equals(productId))
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishmentId))
                .orElseThrow(() -> new NotFoundException("Ingredient not found"));
    }

    private List<IngredientResponseDTO> getIngredientListByEstablishmentIdAndProductId(EstablishmentEntity establishment,
                                                                                       Long productId) {
        return ingredientRepository.findAllByProductId(productId).stream()
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private List<IngredientResponseDTO> getIngredientListByEstablishmentIdProductIdAndStatus(EstablishmentEntity establishment,
                                                                                             Long productId,
                                                                                             RegisterStatus enabled) {
        return ingredientRepository.findAllByProductIdAndStatus(productId, enabled.getValue()).stream()
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private String getUserId() {
        return userService.getUserId().orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void validUserOwnerOfEstablishment(EstablishmentEntity establishmentEntity) {
        if (!establishmentEntity.getAudit().getCreatedBy().equals(getUserId())) {
            throw new ForbiddenException("User is not owner of establishment");
        }
    }

    private IngredientResponseDTO convertToDto(IngredientEntity ingredientEntity) {
        return modelMapper.map(ingredientEntity, IngredientResponseDTO.class);
    }

    private IngredientEntity convertToEntity(IngredientRequestDTO ingredientRequestDTO) {
        return modelMapper.map(ingredientRequestDTO, IngredientEntity.class);
    }
}