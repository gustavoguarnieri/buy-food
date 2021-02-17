package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ProductRequestDto;
import br.com.example.buyfood.model.dto.response.ProductResponseDto;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.ProductEntity;
import br.com.example.buyfood.model.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<ProductResponseDto> getProductList(Long establishmentId, Integer status) {
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

    public ProductResponseDto getProduct(Long establishmentId, Long productId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return productRepository.findByEstablishmentAndId(establishment, productId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public ProductResponseDto createProduct(Long establishmentId, ProductRequestDto productRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);

        if (productRepository.findByEstablishment(establishment).isPresent()) {
            log.warn("createProduct: establishment already exist establishmentId={}", establishmentId);
            throw new BadRequestException("Establishment already exist");
        }

        ProductEntity convertedProductEntity = convertToEntity(productRequestDto);
        convertedProductEntity.setEstablishment(establishment);
        return convertToDto(productRepository.save(convertedProductEntity));
    }

    public void updateProduct(Long establishmentId, Long productId, ProductRequestDto productRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        ProductEntity convertedProductEntity = convertToEntity(productRequestDto);
        convertedProductEntity.setId(productId);
        convertedProductEntity.setEstablishment(establishment);
        productRepository.save(convertedProductEntity);
    }

    public void deleteProduct(Long establishmentId, Long productId) {
        establishmentService.getEstablishmentById(establishmentId);
        ProductEntity productEntity = getProductById(productId);
        productEntity.setStatus(RegisterStatus.DISABLED.getValue());
        productRepository.save(productEntity);
    }

    public ProductEntity getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private List<ProductResponseDto> getProductListByEstablishmentAndStatus(EstablishmentEntity establishment,
                                                                            RegisterStatus enabled) {
        return productRepository.findAllByEstablishmentAndStatus(establishment, enabled.getValue()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ProductResponseDto convertToDto(ProductEntity productEntity) {
        return modelMapper.map(productEntity, ProductResponseDto.class);
    }

    private ProductEntity convertToEntity(ProductRequestDto productRequestDto) {
        return modelMapper.map(productRequestDto, ProductEntity.class);
    }
}