package br.gov.senai.passe.market;

import br.gov.senai.passe.shared.error.BadRequestException;
import br.gov.senai.passe.shared.util.BrasiliaTime;
import br.gov.senai.passe.shared.validation.JsonBody;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuyValidator {

    private static final long MAX_LOYALTY_DAYS = 1095;

    private final Clock clock;

    public BuyCommand parse(JsonBody body) {
        int clubId = body.requiredInt("club_id", 1, Integer.MAX_VALUE);
        LocalDateTime loyaltyUntil = BrasiliaTime.parse(body.requiredText("loyalty_until"));
        LocalDateTime now = LocalDateTime.now(clock);
        if (!loyaltyUntil.isAfter(now)) {
            throw new BadRequestException("O fim da fidelidade deve ser depois do momento da compra.");
        }
        if (loyaltyUntil.isAfter(now.plusDays(MAX_LOYALTY_DAYS))) {
            throw new BadRequestException("O fim da fidelidade pode ser de até 1.095 dias.");
        }
        return new BuyCommand(clubId, loyaltyUntil);
    }
}
