package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ProductRequestDTO;
import br.com.example.buyfood.model.dto.response.ProductResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.ProductEntity;
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
public class ProductService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private UserService userService;

    public List<ProductResponseDTO> getProductList(Long establishmentId, Integer status) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        if (status == null) {
            return productRepository.findAllByEstablishment(establishment).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getProductListByEstablishmentAndStatus(establishment, RegisterStatus.ENABLED);
                case 0: {
                    return getProductListByEstablishmentAndStatus(establishment, RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public ProductResponseDTO getProduct(Long establishmentId, Long productId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return productRepository.findByEstablishmentAndId(establishment, productId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public ProductResponseDTO createProduct(Long establishmentId, ProductRequestDTO productRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        var convertedProductEntity = convertToEntity(productRequestDto);
        convertedProductEntity.setEstablishment(establishment);
        return convertToDto(productRepository.save(convertedProductEntity));
    }

    public void updateProduct(Long establishmentId, Long productId, ProductRequestDTO productRequestDto) {
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
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private List<ProductResponseDTO> getProductListByEstablishmentAndStatus(EstablishmentEntity establishment,
                                                                            RegisterStatus enabled) {
        return productRepository.findAllByEstablishmentAndStatus(establishment, enabled.getValue()).stream()
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

    private ProductResponseDTO convertToDto(ProductEntity productEntity) {
        return modelMapper.map(productEntity, ProductResponseDTO.class);
    }

    private ProductEntity convertToEntity(ProductRequestDTO productRequestDto) {
        return modelMapper.map(productRequestDto, ProductEntity.class);
    }
}