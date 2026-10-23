package br.gov.senai.passe.market;

import java.time.LocalDateTime;

public record BuyCommand(int clubId, LocalDateTime loyaltyUntil) {
}
