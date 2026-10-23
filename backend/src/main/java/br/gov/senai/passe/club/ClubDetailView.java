package br.gov.senai.passe.club;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;

public record ClubDetailView(@JsonUnwrapped ClubView club, List<SquadPlayerView> players) {
}
