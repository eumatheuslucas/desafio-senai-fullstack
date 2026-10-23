package br.gov.senai.passe.match;

import br.gov.senai.passe.shared.error.BadRequestException;
import br.gov.senai.passe.shared.validation.JsonBody;

public final class MatchValidator {

    private MatchValidator() {
    }

    public static MatchRequest parse(JsonBody body) {
        int home = body.requiredInt("home_club_id", 1, Integer.MAX_VALUE);
        int away = body.requiredInt("away_club_id", 1, Integer.MAX_VALUE);
        if (home == away) {
            throw new BadRequestException("O mandante e o visitante devem ser clubes diferentes.");
        }
        return new MatchRequest(home, away);
    }
}
