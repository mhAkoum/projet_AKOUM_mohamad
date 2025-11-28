package com.simplecash.projet_akoum_mohamad.repository;

import com.simplecash.projet_akoum_mohamad.domain.Card;
import com.simplecash.projet_akoum_mohamad.domain.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByClientId(Long clientId);
    List<Card> findByClientIdAndStatus(Long clientId, CardStatus status);
}

