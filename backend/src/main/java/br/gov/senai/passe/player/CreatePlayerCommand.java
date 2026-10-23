package br.gov.senai.passe.player;

public record CreatePlayerCommand(
        String name,
        Position position,
        int agility,
        int age,
        int pace,
        int shooting,
        int passing,
        int dribbling,
        int defending,
        int physical,
        int potential) {
}
