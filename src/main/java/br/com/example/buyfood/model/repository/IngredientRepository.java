package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.IngredientEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {
  List<IngredientEntity> findAllByProductIdAndStatus(Long productId, int status);

  List<IngredientEntity> findAllByProductId(Long productId);
}
