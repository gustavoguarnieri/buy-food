package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.EstablishmentCategoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentCategoryRepository
    extends JpaRepository<EstablishmentCategoryEntity, Long> {
  List<EstablishmentCategoryEntity> findAllByStatus(int status);
}
