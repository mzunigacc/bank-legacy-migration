package com.example.banklegacymigration.bff.common.repository;

import com.example.banklegacymigration.bff.common.entity.AccountMovement;
import com.example.banklegacymigration.bff.common.entity.AccountMovementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountMovementRepository
        extends JpaRepository<AccountMovement, AccountMovementId> {

    List<AccountMovement> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}