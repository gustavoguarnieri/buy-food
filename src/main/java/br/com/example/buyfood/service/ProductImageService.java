package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.BadRequestException;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ImageRequestDto;
import br.com.example.buyfood.model.dto.response.ImageResponseDto;
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

    public List<ImageResponseDto> getProductImageList(Long productId, Integer status) {
        if (status == null) {
            return getProductImageListByProductId(productId);
        } else {
            switch (status) {
                case 1:
                    return getProductImageListByProductIdAndStatus(productId, RegisterStatus.ENABLED);
                case 0: {
                    return getProductImageListByProductIdAndStatus(productId, RegisterStatus.DISABLED);
                }
                default:
                    log.error("getProductImageList: Status incompatible, status:{}", status);
                    throw new BadRequestException("Status incompatible");
            }
        }
    }

    public ImageResponseDto getProductImage(Long productId, Long imageId) {
        return productImageRepository.findByIdAndProductId(imageId, productId)
                .map(this::convertToDto)
                .orElseThrow(() -> new NotFoundException("Product image not found"));
    }

    public ImageResponseDto createProductImage(Long id, MultipartFile file) {
        var productEntity = getProductById(id);

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

    public List<ImageResponseDto> createProductImageList(Long productId, MultipartFile[] files) {
        var productEntity = getProductById(productId);
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

    public void updateProductImage(Long productId, Long imageId, ImageRequestDto imageRequestDto) {
        getProductImage(productId, imageId);
        var productEntity = getProductById(productId);
        ImageEntity imageEntity = convertToEntity(imageRequestDto);
        imageEntity.setId(imageId);
        imageEntity.setProduct(productEntity);
        productImageRepository.save(imageEntity);
    }

    public void deleteProductImage(Long productId, Long imageId) {
        var imageEntity = getProductImageByIdAndProductId(productId, imageId);
        imageEntity.setStatus(RegisterStatus.DISABLED.getValue());
        productImageRepository.save(imageEntity);

    }

    private ImageEntity getProductImageByIdAndProductId(Long productId, Long imageId) {
        return productImageRepository.findByIdAndProductId(imageId, productId)
                .orElseThrow(() -> new NotFoundException("Product image not found"));
    }

    public ProductEntity getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product image not found"));
    }

    private List<ImageResponseDto> getProductImageListByProductId(Long productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private List<ImageResponseDto> getProductImageListByProductIdAndStatus(Long productId, RegisterStatus enabled) {
        return productImageRepository.findAllByProductIdAndStatus(productId, enabled.getValue()).stream()
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