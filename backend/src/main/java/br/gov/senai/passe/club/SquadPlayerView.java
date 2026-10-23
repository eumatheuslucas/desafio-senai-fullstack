package br.gov.senai.passe.club;

import br.gov.senai.passe.player.PlayerView;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record SquadPlayerView(@JsonUnwrapped PlayerView player, ContractView contract) {
}
