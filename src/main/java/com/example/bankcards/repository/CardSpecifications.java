package com.example.bankcards.repository;

import com.example.bankcards.dto.card.request.CardsFilter;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class CardSpecifications {

    public static Specification<Card> build(User user, CardsFilter filter) {
        if (filter == null) {
            filter = new CardsFilter();
        }
        return CardSpecifications.byUserId(user)
                .and(byStatus(filter.getStatus()))
                .and(byExpiryDate(filter.getDate()));
    }
    public static Specification<Card> build(CardsFilter filter) {
        return CardSpecifications.byExpiryDate(filter.getDate())
                .and(byStatus(filter.getStatus()));
    }



    public static Specification<Card> byExpiryDate(LocalDate expiryDate) {
        if (expiryDate == null) return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("expiryDate"), LocalDate.now()));
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("expiryDate"), expiryDate));
    }

    public static Specification<Card> byStatus(String status) {
        if (status == null || status.isEmpty()) return null;

        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status));
    }

    public static Specification<Card> byUserId(User user) {
        if (user == null) return null;
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("owner"), user));
    }

}
