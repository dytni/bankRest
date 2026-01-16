package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import jakarta.persistence.LockModeType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    @NotNull
    @Override
    @EntityGraph(attributePaths = {"owner"})
    Page<Card> findAll(Specification<Card> specification, @NotNull Pageable pageable);

    boolean existsByNumber(String number);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Card c WHERE c.number = :number")
    Optional<Card> findByNumberForUpdate(@Param("number") String number);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"owner"})
    Optional<Card> findByNumberAndOwner(String number, User owner);

    List<Card> findAllByStatusAndExpiryDateBefore(String active, LocalDate today);

    Optional<Card> findByStatusAndNumber(String status, String cardNumber);

    Optional<Card> findByNumber(String number);
}
