package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.ImageEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ImageEntity, Long> {
  List<ImageEntity> findAllByProductIdAndStatus(Long productId, int status);

  List<ImageEntity> findAllByProductId(Long productId);

  Optional<ImageEntity> findByIdAndProductId(Long id, Long productId);
}
