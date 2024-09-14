package com.money_plan.api.domain.user.repository;

import com.money_plan.api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByAccount(String account);

    Optional<User> findByAccount(String username);
}
