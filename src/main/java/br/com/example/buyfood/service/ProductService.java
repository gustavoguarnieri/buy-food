package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ProductRequestDto;
import br.com.example.buyfood.model.dto.response.ProductResponseDto;
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

    public List<ProductResponseDto> getProductList(Integer status) {
        if (status == null) {
            return productRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            switch (status) {
                case 1:
                    return getProductListByStatus(RegisterStatus.ENABLED);
                case 0: {
                    return getProductListByStatus(RegisterStatus.DISABLED);
                }
                default:
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public ProductResponseDto getProduct(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public ProductResponseDto createProduct(ProductRequestDto productRequestDto) {
        ProductEntity convertedProductEntity = convertToEntity(productRequestDto);
        return convertToDto(productRepository.save(convertedProductEntity));
    }

    public void updateProduct(Long id, ProductRequestDto productRequestDto) {
        getProductById(id);
        ProductEntity convertedProductEntity = convertToEntity(productRequestDto);
        convertedProductEntity.setId(id);
        productRepository.save(convertedProductEntity);
    }

    public void deleteProduct(Long id) {
        ProductEntity productEntity = getProductById(id);
        productEntity.setStatus(RegisterStatus.DISABLED.getValue());
        productRepository.save(productEntity);
    }

    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    private List<ProductResponseDto> getProductListByStatus(RegisterStatus enabled) {
        return productRepository.findAllByStatus(enabled.getValue()).stream()
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