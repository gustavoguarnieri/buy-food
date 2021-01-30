package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.EstablishmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentRepository extends JpaRepository<EstablishmentEntity, Long> {}