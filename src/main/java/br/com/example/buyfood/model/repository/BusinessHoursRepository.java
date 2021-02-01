package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.BusinessHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessHoursRepository extends JpaRepository<BusinessHoursEntity, Long> {}