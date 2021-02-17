package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.BusinessHoursEntity;
import br.com.example.buyfood.model.entity.EstablishmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHoursEntity, Long> {
    List<BusinessHoursEntity> findAllByEstablishment(EstablishmentEntity establishment);

    List<BusinessHoursEntity> findAllByEstablishmentAndStatus(EstablishmentEntity establishment, int status);

    Optional<BusinessHoursEntity> findByEstablishmentAndId(EstablishmentEntity establishment, Long businessHoursId);

    Optional<BusinessHoursEntity> findByEstablishment(EstablishmentEntity establishment);
}