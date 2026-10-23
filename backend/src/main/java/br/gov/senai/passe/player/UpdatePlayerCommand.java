package br.gov.senai.passe.player;

public record UpdatePlayerCommand(String name, Position position, int agility, int age) {
}
