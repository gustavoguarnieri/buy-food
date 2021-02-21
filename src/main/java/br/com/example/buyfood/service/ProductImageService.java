package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ImageRequestDTO;
import br.com.example.buyfood.model.dto.response.ImageResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.ImageEntity;
import br.com.example.buyfood.model.entity.ProductEntity;
import br.com.example.buyfood.model.repository.ProductImageRepository;
import br.com.example.buyfood.model.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductImageService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EstablishmentService establishmentService;

    public List<ImageResponseDTO> getProductImageList(Long establishmentId, Long productId, Integer status) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        if (status == null) {
            return getProductImageListByEstablishmentIdAndProductId(establishment, productId);
        } else {
            switch (status) {
                case 1:
                    return getProductImageListByEstablishmentIdProductIdAndStatus(establishment, productId, RegisterStatus.ENABLED);
                case 0: {
                    return getProductImageListByEstablishmentIdProductIdAndStatus(establishment, productId, RegisterStatus.DISABLED);
                }
                default:
                    log.error("getProductImageList: Status incompatible, status:{}", status);
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public ImageResponseDTO getProductImage(Long establishmentId, Long productId, Long imageId) {
        return convertToDto(getProductImageByEstablishmentAndIdAndProductId(establishmentId, productId, imageId));
    }

    public ImageResponseDTO createProductImage(Long establishmentId, Long productId, MultipartFile file) {
        var productEntity = getProductByEstablishmentAndProductId(establishmentId, productId);

        var uploadFileResponse = fileStorageService.saveFile(file);

        var imageEntity = fileStorageService.createImageEntity(productEntity, uploadFileResponse);
        productImageRepository.save(imageEntity);

        return fileStorageService.createImageResponseDTO(imageEntity.getId(), uploadFileResponse, 1);
    }

    public List<ImageResponseDTO> createProductImageList(Long establishmentId, Long productId, MultipartFile[] files) {
        var productEntity = getProductByEstablishmentAndProductId(establishmentId, productId);

        var uploadFileResponse = fileStorageService.saveFileList(files);

        List<ImageResponseDTO> imageResponseDTOList = new ArrayList<>();

        uploadFileResponse
                .forEach(i -> {
                    var imageEntity = fileStorageService.createImageEntity(productEntity, i);
                    productImageRepository.save(imageEntity);
                    imageResponseDTOList.add(fileStorageService.createImageResponseDTO(imageEntity.getId(), i, 1));
                });

        return imageResponseDTOList;
    }

    public void updateProductImage(Long establishmentId, Long productId, Long imageId, ImageRequestDTO imageRequestDto) {
        getProductImage(establishmentId, productId, imageId);
        var productEntity = getProductById(productId);
        ImageEntity imageEntity = convertToEntity(imageRequestDto);
        imageEntity.setId(imageId);
        imageEntity.setProduct(productEntity);
        productImageRepository.save(imageEntity);
    }

    public void deleteProductImage(Long establishmentId, Long productId, Long imageId) {
        var imageEntity = getProductImageByEstablishmentAndIdAndProductId(establishmentId, productId, imageId);
        imageEntity.setStatus(RegisterStatus.DISABLED.getValue());
        productImageRepository.save(imageEntity);

    }

    private ImageEntity getProductImageByEstablishmentAndIdAndProductId(Long establishmentId, Long productId, Long imageId) {
        return productImageRepository.findByIdAndProductId(imageId, productId)
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishmentId))
                .orElseThrow(() -> new NotFoundException("Product image not found"));
    }

    public ProductEntity getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product image not found"));
    }

    public ProductEntity getProductByEstablishmentAndProductId(Long establishmentId, Long productId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return productRepository.findByEstablishmentAndId(establishment, productId)
                .orElseThrow(() -> new NotFoundException("Product image not found"));
    }

    private List<ImageResponseDTO> getProductImageListByEstablishmentIdAndProductId(EstablishmentEntity establishment,
                                                                                    Long productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private List<ImageResponseDTO> getProductImageListByEstablishmentIdProductIdAndStatus(EstablishmentEntity establishment,
                                                                                          Long productId,
                                                                                          RegisterStatus enabled) {
        return productImageRepository.findAllByProductIdAndStatus(productId, enabled.getValue()).stream()
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ImageResponseDTO convertToDto(ImageEntity imageEntity) {
        return modelMapper.map(imageEntity, ImageResponseDTO.class);
    }

    private ImageEntity convertToEntity(ImageRequestDTO imageRequestDto) {
        return modelMapper.map(imageRequestDto, ImageEntity.class);
    }
}