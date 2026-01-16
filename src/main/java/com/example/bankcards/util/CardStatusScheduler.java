package com.example.bankcards.util;

import com.example.bankcards.entity.Card;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardStatusScheduler {

    private final CardRepository cardRepository;

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void updateExpiredCards() {
        log.info("Запуск задачи по обновлению просроченных карт...");

        LocalDate today = LocalDate.now();

        List<Card> expiredCards = cardRepository.findAllByStatusAndExpiryDateBefore("ACTIVE", today);

        expiredCards.forEach(card -> card.setStatus("EXPIRED"));

        cardRepository.saveAll(expiredCards);
        log.info("Обновлено карт: {}", expiredCards.size());
    }
}