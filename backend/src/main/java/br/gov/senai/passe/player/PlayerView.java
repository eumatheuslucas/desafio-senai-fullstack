package br.gov.senai.passe.player;

import java.math.BigDecimal;

public record PlayerView(
        Integer id,
        String name,
        Position position,
        int agility,
        int age,
        Condition condition,
        BigDecimal baseValue,
        BigDecimal marketValue,
        int pace,
        int shooting,
        int passing,
        int dribbling,
        int defending,
        int physical,
        int potential,
        int overall,
        Integer clubId) {

    public static PlayerView from(Player player) {
        return new PlayerView(
                player.getId(),
                player.getName(),
                player.getPosition(),
                player.getAgility(),
                player.getAge(),
                player.getCondition(),
                player.getBaseValue(),
                PlayerPricing.marketValue(player),
                player.getPace(),
                player.getShooting(),
                player.getPassing(),
                player.getDribbling(),
                player.getDefending(),
                player.getPhysical(),
                player.getPotential(),
                PlayerPricing.overall(player),
                player.getClubId());
    }
}
