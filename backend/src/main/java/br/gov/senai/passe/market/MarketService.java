package br.gov.senai.passe.market;

import br.gov.senai.passe.club.Club;
import br.gov.senai.passe.club.ClubRepository;
import br.gov.senai.passe.contract.Contract;
import br.gov.senai.passe.contract.ContractRepository;
import br.gov.senai.passe.contract.ContractStatus;
import br.gov.senai.passe.contract.ContractTerms;
import br.gov.senai.passe.lineup.LineupService;
import br.gov.senai.passe.player.Player;
import br.gov.senai.passe.player.PlayerPricing;
import br.gov.senai.passe.player.PlayerRepository;
import br.gov.senai.passe.shared.error.ConflictException;
import br.gov.senai.passe.shared.error.NotFoundException;
import br.gov.senai.passe.shared.util.BrasiliaTime;
import br.gov.senai.passe.shared.util.Money;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketService {

    private static final int MAX_SQUAD_SIZE = 12;

    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;
    private final ContractRepository contractRepository;
    private final TransactionRepository transactionRepository;
    private final LineupService lineupService;
    private final Clock clock;

    @Transactional
    public PurchaseView buy(int playerId, BuyCommand command) {
        Player player = playerRepository.findActiveForUpdate(playerId)
                .orElseThrow(() -> new NotFoundException("Jogador não encontrado."));
        Club club = clubRepository.findByIdForUpdate(command.clubId())
                .orElseThrow(() -> new NotFoundException("Clube não encontrado."));
        checkCanBuy(player, club);

        BigDecimal value = PlayerPricing.marketValue(player);
        BigDecimal fee = MarketRules.purchaseFee(value);
        BigDecimal totalCost = value.add(fee);
        if (!MarketRules.withinCashLimit(totalCost, club.getCash())) {
            throw new ConflictException("O custo da compra (" + totalCost + ") passa do limite de 50% do caixa ("
                    + club.getCash().divide(BigDecimal.valueOf(2)) + ").");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        club.setCash(club.getCash().subtract(totalCost));
        player.setClubId(club.getId());
        player.setUpdatedAt(now);

        Contract contract = new Contract();
        contract.setPlayerId(playerId);
        contract.setClubId(club.getId());
        contract.setPurchaseValue(value);
        contract.setLoyaltyUntil(command.loyaltyUntil());
        contract.setStatus(ContractStatus.ATIVO);
        contract.setStartedAt(now);

        MarketTransaction transaction = newTransaction(club, player, TransactionType.COMPRA, now);
        transaction.setValue(value);
        transaction.setFee(fee);
        transaction.setPenalty(Money.ZERO);
        transaction.setCashChange(totalCost.negate());

        clubRepository.save(club);
        playerRepository.save(player);
        contractRepository.save(contract);
        transactionRepository.save(transaction);

        PurchaseView.ContractSummary summary = new PurchaseView.ContractSummary(value,
                BrasiliaTime.format(command.loyaltyUntil()), ContractStatus.ATIVO.name());
        return new PurchaseView(transaction.getId(), club.getId(), playerId, player.getName(),
                TransactionType.COMPRA, value, fee, Money.ZERO, null, transaction.getCashChange(),
                BrasiliaTime.format(now), summary, club.getCash());
    }

    @Transactional
    public SaleView sell(int playerId) {
        Player player = playerRepository.findActiveForUpdate(playerId)
                .orElseThrow(() -> new NotFoundException("Jogador não encontrado."));
        if (player.getClubId() == null) {
            throw new ConflictException("Jogador livre não pode ser vendido.");
        }
        Club club = clubRepository.findByIdForUpdate(player.getClubId())
                .orElseThrow(() -> new IllegalStateException("Clube do jogador não existe."));
        Contract contract = contractRepository.findByPlayerIdAndStatus(playerId, ContractStatus.ATIVO)
                .orElseThrow(() -> new IllegalStateException("Jogador com clube sem contrato ativo."));

        LocalDateTime now = LocalDateTime.now(clock);
        BigDecimal value = PlayerPricing.marketValue(player);
        int remainingDays = ContractTerms.remainingDays(contract.getLoyaltyUntil(), now);
        int penaltyRate = ContractTerms.penaltyRate(remainingDays);
        BigDecimal penalty = ContractTerms.penalty(value, penaltyRate);
        BigDecimal profit = value.subtract(contract.getPurchaseValue());
        BigDecimal commission = MarketRules.commission(profit);
        BigDecimal credit = value.subtract(penalty).subtract(commission);

        club.setCash(club.getCash().add(credit));
        player.setClubId(null);
        player.setUpdatedAt(now);
        contract.setStatus(penalty.signum() > 0 ? ContractStatus.RESCINDIDO : ContractStatus.ENCERRADO);
        contract.setEndedAt(now);
        lineupService.removePlayer(playerId);

        MarketTransaction transaction = newTransaction(club, player, TransactionType.VENDA, now);
        transaction.setValue(value);
        transaction.setFee(commission);
        transaction.setPenalty(penalty);
        transaction.setProfit(profit);
        transaction.setCashChange(credit);

        clubRepository.save(club);
        playerRepository.save(player);
        contractRepository.save(contract);
        transactionRepository.save(transaction);

        return new SaleView(transaction.getId(), club.getId(), playerId, player.getName(), TransactionType.VENDA,
                value, commission, penalty, profit, credit, BrasiliaTime.format(now), contract.getPurchaseValue(),
                remainingDays, penaltyRate, contract.getStatus().name(), club.getCash());
    }

    private void checkCanBuy(Player player, Club club) {
        if (player.getClubId() != null) {
            throw new ConflictException("O jogador não está livre.");
        }
        if (playerRepository.findByClubIdAndDeletedAtIsNull(club.getId()).size() >= MAX_SQUAD_SIZE) {
            throw new ConflictException("O elenco já tem " + MAX_SQUAD_SIZE + " jogadores.");
        }
        if (transactionRepository.existsByClubIdAndPlayerIdAndType(club.getId(), player.getId(),
                TransactionType.VENDA)) {
            throw new ConflictException("O clube não pode comprar um jogador que ele mesmo já vendeu.");
        }
    }

    private MarketTransaction newTransaction(Club club, Player player, TransactionType type, LocalDateTime now) {
        MarketTransaction transaction = new MarketTransaction();
        transaction.setClubId(club.getId());
        transaction.setPlayerId(player.getId());
        transaction.setPlayerName(player.getName());
        transaction.setType(type);
        transaction.setCreatedAt(now);
        return transaction;
    }
}
