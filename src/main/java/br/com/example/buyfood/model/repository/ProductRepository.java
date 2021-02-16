package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByStatus(int status);

    Optional<ProductEntity> findByIdAndStatus(Long id, int status);
}