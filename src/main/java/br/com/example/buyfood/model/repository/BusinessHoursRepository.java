package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.BusinessHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHoursEntity, Long> {
    List<BusinessHoursEntity> findAllByStatus(int status);

    Optional<BusinessHoursEntity> findByIdAndStatus(Long id, int status);
}