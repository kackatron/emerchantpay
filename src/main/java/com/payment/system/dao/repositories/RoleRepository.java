package com.payment.system.dao.repositories;

import java.util.Optional;

import com.payment.system.dao.models.ERole;
import com.payment.system.dao.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}