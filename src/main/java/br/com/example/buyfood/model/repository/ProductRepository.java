package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.EstablishmentEntity;
import br.com.example.buyfood.model.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByEstablishment(EstablishmentEntity establishment);

    List<ProductEntity> findAllByEstablishmentAndStatus(EstablishmentEntity establishment, int status);

    List<ProductEntity> findAllByStatus(int status);

    Optional<ProductEntity> findByEstablishmentAndId(EstablishmentEntity establishment, Long productId);
}