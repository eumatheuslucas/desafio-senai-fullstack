package br.gov.senai.passe.club;

import br.gov.senai.passe.contract.Contract;
import br.gov.senai.passe.contract.ContractTerms;
import br.gov.senai.passe.shared.util.BrasiliaTime;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContractView(BigDecimal purchaseValue, String loyaltyUntil, int remainingDays, int penaltyRate) {

    public static ContractView of(Contract contract, LocalDateTime now) {
        int remainingDays = ContractTerms.remainingDays(contract.getLoyaltyUntil(), now);
        return new ContractView(contract.getPurchaseValue(), BrasiliaTime.format(contract.getLoyaltyUntil()),
                remainingDays, ContractTerms.penaltyRate(remainingDays));
    }
}
