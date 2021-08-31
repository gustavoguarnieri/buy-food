package br.com.example.buyfood.service.establishment;

import br.com.example.buyfood.constants.ErrorMessages;
import br.com.example.buyfood.enums.FileStorageFolder;
import br.com.example.buyfood.enums.RegisterStatus;
import br.com.example.buyfood.exception.NotFoundException;
import br.com.example.buyfood.model.dto.request.ImageRequestDTO;
import br.com.example.buyfood.model.dto.response.ImageResponseDTO;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.ImageEntity;
import br.com.example.buyfood.model.entity.ProductEntity;
import br.com.example.buyfood.model.repository.ProductImageRepository;
import br.com.example.buyfood.model.repository.ProductRepository;
import br.com.example.buyfood.service.FileStorageService;
import br.com.example.buyfood.service.UserService;
import br.com.example.buyfood.util.StatusValidation;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
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
public class EstablishmentProductImageService {

    private final ModelMapper modelMapper;

    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    private final FileStorageService fileStorageService;

    private final EstablishmentService establishmentService;

    private final UserService userService;

    private final StatusValidation statusValidation;

    private final String RETRY_DELAY_TIME = "200";
    private final int RETRY_MAX_ATTEMPTS = 6;

    @Autowired
    public EstablishmentProductImageService(ModelMapper modelMapper, ProductRepository productRepository, ProductImageRepository productImageRepository, FileStorageService fileStorageService, EstablishmentService establishmentService, UserService userService, StatusValidation statusValidation) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.fileStorageService = fileStorageService;
        this.establishmentService = establishmentService;
        this.userService = userService;
        this.statusValidation = statusValidation;
    }

    public List<ImageResponseDTO> getProductImageList(Long establishmentId, Long productId, Integer status) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        if (status == null) {
            return getProductImageListByEstablishmentIdAndProductId(establishment, productId);
        } else {
            return productImageRepository
                    .findAllByProductIdAndStatus(productId, statusValidation.getStatusIdentification(status))
                    .stream()
                    .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
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

        return fileStorageService.createImageResponseDTO(
                imageEntity.getId(), uploadFileResponse, RegisterStatus.ENABLED.getValue()
        );
    }

    public List<ImageResponseDTO> createProductImageList(Long establishmentId, Long productId, MultipartFile[] files) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        var product = getProductByEstablishmentAndProductId(establishmentId, productId);

        var downloadPath = getDownloadProductPath(establishment, product);

        var uploadFileResponse =
                fileStorageService.saveFileList(files, FileStorageFolder.PRODUCTS, productId, downloadPath);

        List<ImageResponseDTO> imageResponseDTOList = new ArrayList<>();

        uploadFileResponse.forEach(
                file -> {
                    var imageEntity = fileStorageService.createImageEntity(product, file);
                    productImageRepository.save(imageEntity);
                    imageResponseDTOList.add(
                            fileStorageService.createImageResponseDTO(
                                    imageEntity.getId(), file, RegisterStatus.ENABLED.getValue())
                    );
                });

        return imageResponseDTOList;
    }

    public void updateProductImage(
            Long establishmentId, Long productId, Long imageId, ImageRequestDTO imageRequestDto) {
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

        var imageEntity =
                getProductImageByEstablishmentIdAndProductIdAndId(establishmentId, productId, imageId);
        imageEntity.setStatus(RegisterStatus.DISABLED.getValue());
        productImageRepository.save(imageEntity);
    }

    @Retryable(
            value = Exception.class,
            maxAttempts = RETRY_MAX_ATTEMPTS,
            backoff = @Backoff(delayExpression = RETRY_DELAY_TIME)
    )
    public ResponseEntity<Resource> getDownloadProductImage(
            Long productId, String fileName, HttpServletRequest request) {
        return fileStorageService.downloadFile(FileStorageFolder.PRODUCTS, productId, fileName, request);
    }

    private ImageEntity getProductImageByEstablishmentIdAndProductIdAndId(
            Long establishmentId, Long productId, Long imageId) {
        return productImageRepository.findById(imageId)
                .filter(i -> i.getProduct().getId().equals(productId))
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishmentId))
                .orElseThrow(() -> new NotFoundException(ErrorMessages.PRODUCT_IMAGE_NOT_FOUND));
    }

    public ProductEntity getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
    }

    public ProductEntity getProductByEstablishmentAndProductId(Long establishmentId, Long productId) {
        var establishment = establishmentService.getEstablishmentById(establishmentId);
        return productRepository.findByEstablishmentAndId(establishment, productId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
    }

    private List<ImageResponseDTO> getProductImageListByEstablishmentIdAndProductId(
            EstablishmentEntity establishment, Long productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .filter(i -> i.getProduct().getEstablishment().getId().equals(establishment.getId()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private String getDownloadProductPath(EstablishmentEntity establishment, ProductEntity product) {
        return "/api/v1/establishments/"
                + establishment.getId()
                + "/products/"
                + product.getId()
                + "/images/download-file/";
    }

    private String getUserId() {
        return userService.getUserId().orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND));
    }

    private void validUserOwnerOfEstablishment(EstablishmentEntity establishmentEntity) {
        if (!establishmentEntity.getAudit().getCreatedBy().equals(getUserId())) {
            throw new ForbiddenException(ErrorMessages.USER_IS_NOT_OWNER_OF_ESTABLISHMENT);
        }
    }

    private ImageResponseDTO convertToDto(ImageEntity imageEntity) {
        return modelMapper.map(imageEntity, ImageResponseDTO.class);
    }

    private ImageEntity convertToEntity(ImageRequestDTO imageRequestDto) {
        return modelMapper.map(imageRequestDto, ImageEntity.class);
    }
}
