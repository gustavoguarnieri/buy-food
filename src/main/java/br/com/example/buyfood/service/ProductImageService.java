package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ImageRequestDto;
import br.com.example.buyfood.model.dto.response.ImageResponseDto;
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

    public List<ImageResponseDto> getProductImageList(Long establishmentId, Long productId, Integer status) {
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

    public ImageResponseDto getProductImage(Long establishmentId, Long productId, Long imageId) {
        return convertToDto(getProductImageByEstablishmentAndIdAndProductId(establishmentId, productId, imageId));
    }

    public ImageResponseDto createProductImage(Long establishmentId, Long productId, MultipartFile file) {
        var productEntity = getProductByEstablishmentAndProductId(establishmentId, productId);

        var uploadFileResponse = fileStorageService.saveFile(file);

        var imageEntity = new ImageEntity(
                productEntity,
                uploadFileResponse.getFileName(),
                uploadFileResponse.getFileUri(),
                uploadFileResponse.getFileType(),
                uploadFileResponse.getSize());

        productImageRepository.save(imageEntity);

        return new ImageResponseDto(
                imageEntity.getId(),
                uploadFileResponse.getFileName(),
                uploadFileResponse.getFileUri(),
                uploadFileResponse.getFileType(),
                uploadFileResponse.getSize(),
                1);
    }

    public List<ImageResponseDto> createProductImageList(Long establishmentId, Long productId, MultipartFile[] files) {
        var productEntity = getProductByEstablishmentAndProductId(establishmentId, productId);

        var uploadFileResponse = fileStorageService.saveFileList(files);

        List<ImageResponseDto> imageResponseDtoList = new ArrayList<ImageResponseDto>();

        uploadFileResponse
                .forEach(f -> {
                    var imageEntity = new ImageEntity(productEntity, f.getFileName(), f.getFileUri(),
                            f.getFileType(), f.getSize());

                    productImageRepository.save(imageEntity);

                    imageResponseDtoList.add(new ImageResponseDto(imageEntity.getId(), imageEntity.getFileName(),
                            imageEntity.getFileUri(), f.getFileType(), imageEntity.getSize(), 1));
                });

        return imageResponseDtoList;
    }

    public void updateProductImage(Long establishmentId, Long productId, Long imageId, ImageRequestDto imageRequestDto) {
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
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return productImageRepository.findByIdAndProductId(imageId, productId)
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
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

    private List<ImageResponseDto> getProductImageListByEstablishmentIdAndProductId(EstablishmentEntity establishment,
                                                                                    Long productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private List<ImageResponseDto> getProductImageListByEstablishmentIdProductIdAndStatus(EstablishmentEntity establishment,
                                                                                          Long productId,
                                                                                          RegisterStatus enabled) {
        return productImageRepository.findAllByProductIdAndStatus(productId, enabled.getValue()).stream()
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ImageResponseDto convertToDto(ImageEntity imageEntity) {
        return modelMapper.map(imageEntity, ImageResponseDto.class);
    }

    private ImageEntity convertToEntity(ImageRequestDto imageRequestDto) {
        return modelMapper.map(imageRequestDto, ImageEntity.class);
    }
}