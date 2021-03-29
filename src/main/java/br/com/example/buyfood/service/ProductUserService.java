package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.model.dto.response.Product.EstablishmentProductResponseDTO;
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
public class ProductUserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    public List<EstablishmentProductResponseDTO> getProductList(Integer status) {
        if (status == null) {
            return productRepository.findAll().stream()
                    .map(this::convertToEstablishmentProductResponseDto)
                    .collect(Collectors.toList());
        } else {
            return productRepository.findAllByStatus(status == 1 ? RegisterStatus.ENABLED.getValue() : RegisterStatus.DISABLED.getValue()).stream()
                    .map(this::convertToEstablishmentProductResponseDto)
                    .collect(Collectors.toList());
        }
    }

    private EstablishmentProductResponseDTO convertToEstablishmentProductResponseDto(ProductEntity productEntity) {
        return modelMapper.map(productEntity, EstablishmentProductResponseDTO.class);
    }
}