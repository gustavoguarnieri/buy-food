package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.EstablishmentCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstablishmentCategoryRepository extends JpaRepository<EstablishmentCategoryEntity, Long> {
    List<EstablishmentCategoryEntity> findAllByStatus(int status);
}