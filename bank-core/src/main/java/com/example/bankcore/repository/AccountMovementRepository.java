package com.example.bankcore.repository;

import com.example.bankcore.entity.AccountMovement;
import com.example.bankcore.entity.AccountMovementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountMovementRepository
        extends JpaRepository<AccountMovement, AccountMovementId> {

    List<AccountMovement> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}