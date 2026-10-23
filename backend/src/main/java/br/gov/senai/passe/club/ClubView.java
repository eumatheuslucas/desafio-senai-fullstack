package br.gov.senai.passe.club;

import java.math.BigDecimal;

public record ClubView(
        Integer id,
        String name,
        String acronym,
        BigDecimal cash,
        BigDecimal squadValue,
        BigDecimal netWorth) {

    public static ClubView of(Club club, BigDecimal squadValue) {
        return new ClubView(club.getId(), club.getName(), club.getAcronym(), club.getCash(), squadValue,
                club.getCash().add(squadValue));
    }
}
