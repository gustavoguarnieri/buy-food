package br.com.example.buyfood.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageResponseDTO extends UploadFileResponseDTO {

    private Long id;
    private int status;

    public ImageResponseDTO(Long id,
                            String fileName,
                            String fileUri,
                            String fileType,
                            long size,
                            int status) {
        super(fileName, fileUri, fileType, size);
        this.id = id;
        this.status = status;
    }
}