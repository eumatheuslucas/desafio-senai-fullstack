package br.gov.senai.passe.lineup;

import java.util.List;

public record LineupRequest(Formation formation, List<Integer> playerIds) {
}
