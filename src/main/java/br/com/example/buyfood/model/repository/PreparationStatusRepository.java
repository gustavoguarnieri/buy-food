package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.PreparationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreparationStatusRepository extends JpaRepository<PreparationStatusEntity, Long> {
    List<PreparationStatusEntity> findAllByStatus(int status);
    Optional<PreparationStatusEntity> findByDescriptionIgnoreCase(String description);
}