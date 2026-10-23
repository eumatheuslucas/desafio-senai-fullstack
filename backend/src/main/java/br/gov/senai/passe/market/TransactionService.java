package br.gov.senai.passe.market;

import br.gov.senai.passe.club.ClubRepository;
import br.gov.senai.passe.player.PlayerRepository;
import br.gov.senai.passe.shared.error.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ClubRepository clubRepository;
    private final PlayerRepository playerRepository;

    @Transactional(readOnly = true)
    public HistoryView history(int clubId, TransactionFilter filter) {
        List<MarketTransaction> transactions = filtered(clubId, filter);
        List<TransactionView> data = transactions.stream().map(TransactionView::from).toList();
        return new HistoryView(data, TransactionSummary.of(transactions));
    }

    @Transactional(readOnly = true)
    public String exportCsv(int clubId, TransactionFilter filter) {
        return TransactionCsv.render(filtered(clubId, filter));
    }

    private List<MarketTransaction> filtered(int clubId, TransactionFilter filter) {
        if (!clubRepository.existsById(clubId)) {
            throw new NotFoundException("Clube não encontrado.");
        }
        filter.playerId().ifPresent(playerId -> {
            if (playerRepository.findByIdAndDeletedAtIsNull(playerId).isEmpty()) {
                throw new NotFoundException("Jogador não encontrado.");
            }
        });
        return transactionRepository.findByClubIdOrderByCreatedAtDescIdDesc(clubId).stream()
                .filter(filter::matches)
                .toList();
    }
}
