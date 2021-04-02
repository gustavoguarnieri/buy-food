package br.com.example.buyfood.service;

import br.com.example.buyfood.enums.FileStorageFolder;
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
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
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

    @Autowired
    private UserService userService;

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
        return convertToDto(getProductImageByEstablishmentIdAndProductIdAndId(establishmentId, productId, imageId));
    }

    public ImageResponseDTO createProductImage(Long establishmentId, Long productId, MultipartFile file) {
        var productEntity = getProductByEstablishmentAndProductId(establishmentId, productId);
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return saveImage(file, establishment, productEntity);
    }

    private ImageResponseDTO saveImage(MultipartFile file, EstablishmentEntity establishment, ProductEntity product) {
        var downloadPath = getDownloadProductPath(establishment, product);

        var uploadFileResponse =
                fileStorageService.saveFile(file, FileStorageFolder.PRODUCTS, product.getId(), downloadPath);

        var imageEntity = fileStorageService.createImageEntity(product, uploadFileResponse);
        productImageRepository.save(imageEntity);

        return fileStorageService.createImageResponseDTO(imageEntity.getId(), uploadFileResponse, 1);
    }

    public List<ImageResponseDTO> createProductImageList(Long establishmentId, Long productId, MultipartFile[] files) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        var product = getProductByEstablishmentAndProductId(establishmentId, productId);

        var downloadPath = getDownloadProductPath(establishment, product);

        var uploadFileResponse =
                fileStorageService.saveFileList(files, FileStorageFolder.PRODUCTS, productId, downloadPath);

        List<ImageResponseDTO> imageResponseDTOList = new ArrayList<>();

        uploadFileResponse
                .forEach(i -> {
                    var imageEntity = fileStorageService.createImageEntity(product, i);
                    productImageRepository.save(imageEntity);
                    imageResponseDTOList.add(fileStorageService.createImageResponseDTO(imageEntity.getId(), i, 1));
                });

        return imageResponseDTOList;
    }

    public void updateProductImage(Long establishmentId, Long productId, Long imageId, ImageRequestDTO imageRequestDto) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        validUserOwnerOfEstablishment(establishment);

        getProductImage(establishmentId, productId, imageId);

        var productEntity = getProductById(productId);
        var imageEntity = convertToEntity(imageRequestDto);
        imageEntity.setId(imageId);
        imageEntity.setProduct(productEntity);
        productImageRepository.save(imageEntity);
    }

    public void deleteProductImage(Long establishmentId, Long productId, Long imageId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        validUserOwnerOfEstablishment(establishment);

        var imageEntity = getProductImageByEstablishmentIdAndProductIdAndId(establishmentId, productId, imageId);
        imageEntity.setStatus(RegisterStatus.DISABLED.getValue());
        productImageRepository.save(imageEntity);
    }

    @Retryable(
            value = Exception.class,
            maxAttempts = 6,
            backoff = @Backoff(delayExpression = "200")
    )
    public ResponseEntity<Resource> getDownloadProductImage(Long productId, String fileName,
                                                            HttpServletRequest request) {
        return fileStorageService.downloadFile(FileStorageFolder.PRODUCTS, productId, fileName, request);
    }

    private ImageEntity getProductImageByEstablishmentIdAndProductIdAndId(Long establishmentId, Long productId, Long imageId) {
        return productImageRepository.findById(imageId)
                .filter(i -> i.getProduct().getId().equals(productId))
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishmentId))
                .orElseThrow(() -> new NotFoundException("Product image not found"));
    }

    public ProductEntity getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public ProductEntity getProductByEstablishmentAndProductId(Long establishmentId, Long productId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return productRepository.findByEstablishmentAndId(establishment, productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
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

    private String getDownloadProductPath(EstablishmentEntity establishment, ProductEntity product) {
        return "/api/v1/establishments/" + establishment.getId() + "/products/" + product.getId() + "/images/download-file/";
    }

    private String getUserId() {
        return userService.getUserId().orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void validUserOwnerOfEstablishment(EstablishmentEntity establishmentEntity) {
        if (!establishmentEntity.getAudit().getCreatedBy().equals(getUserId())) {
            throw new ForbiddenException("User is not owner of establishment");
        }
    }

    private ImageResponseDTO convertToDto(ImageEntity imageEntity) {
        return modelMapper.map(imageEntity, ImageResponseDTO.class);
    }

    private ImageEntity convertToEntity(ImageRequestDTO imageRequestDto) {
        return modelMapper.map(imageRequestDto, ImageEntity.class);
    }
}