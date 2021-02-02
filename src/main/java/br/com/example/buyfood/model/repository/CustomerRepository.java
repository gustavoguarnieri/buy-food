package br.com.example.buyfood.model.repository;

import br.com.example.buyfood.model.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    List<CustomerEntity> findAllByStatus(int status);
    Optional<CustomerEntity> findByIdAndStatus(Long id, int status);
}
