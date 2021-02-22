package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.ImageEntity;
import br.com.example.buyfood.model.entity.IngredientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<IngredientEntity, Long> {
    List<IngredientEntity> findAllByProductIdAndStatus(Long productId, int status);

    List<IngredientEntity> findAllByProductId(Long productId);

    //Optional<IngredientEntity> findByIdAndProductId(Long id, Long productId);
}