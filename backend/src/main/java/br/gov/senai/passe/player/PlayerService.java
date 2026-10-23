package br.gov.senai.passe.player;

import br.gov.senai.passe.club.ClubRepository;
import br.gov.senai.passe.lineup.LineupService;
import br.gov.senai.passe.shared.PageView;
import br.gov.senai.passe.shared.error.ConflictException;
import br.gov.senai.passe.shared.error.NotFoundException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;
    private final LineupService lineupService;
    private final Clock clock;

    @Transactional(readOnly = true)
    public PageView<PlayerView> list(PlayerFilter filter) {
        filter.clubId().ifPresent(id -> {
            if (!clubRepository.existsById(id)) {
                throw new NotFoundException("Clube não encontrado.");
            }
        });
        List<PlayerView> matching = playerRepository.findByDeletedAtIsNull().stream()
                .map(PlayerView::from)
                .filter(filter::matches)
                .sorted(filter.comparator())
                .toList();
        int total = matching.size();
        int from = (int) Math.min((long) (filter.page() - 1) * filter.limit(), total);
        int to = Math.min(from + filter.limit(), total);
        int totalPages = (total + filter.limit() - 1) / filter.limit();
        return new PageView<>(matching.subList(from, to),
                new PageView.Meta(filter.page(), filter.limit(), total, totalPages));
    }

    @Transactional(readOnly = true)
    public PlayerView get(int id) {
        return PlayerView.from(findActive(id));
    }

    @Transactional
    public PlayerView create(CreatePlayerCommand command) {
        LocalDateTime now = LocalDateTime.now(clock);
        Player player = new Player();
        player.setName(command.name());
        player.setPosition(command.position());
        player.setAgility(command.agility());
        player.setAge(command.age());
        player.setPace(command.pace());
        player.setShooting(command.shooting());
        player.setPassing(command.passing());
        player.setDribbling(command.dribbling());
        player.setDefending(command.defending());
        player.setPhysical(command.physical());
        player.setPotential(command.potential());
        player.setCondition(Condition.APTO);
        player.setBaseValue(PlayerPricing.baseValue(PlayerPricing.overall(player), command.potential(), command.age()));
        player.setCreatedAt(now);
        player.setUpdatedAt(now);
        return PlayerView.from(playerRepository.save(player));
    }

    @Transactional
    public PlayerView update(int id, UpdatePlayerCommand command) {
        Player player = lockActive(id);
        if (player.getClubId() != null && player.getPosition() != command.position()) {
            throw new ConflictException("Jogador com clube não pode mudar de posição.");
        }
        if (command.age() < player.getAge()) {
            throw new ConflictException("A idade não pode ser menor que a idade atual.");
        }
        player.setName(command.name());
        player.setPosition(command.position());
        player.setAgility(command.agility());
        player.setAge(command.age());
        player.setUpdatedAt(LocalDateTime.now(clock));
        return PlayerView.from(playerRepository.save(player));
    }

    @Transactional
    public PlayerView changeCondition(int id, Condition newCondition) {
        Player player = lockActive(id);
        if (player.getCondition() == Condition.DOENTE && newCondition == Condition.APTO) {
            throw new ConflictException("Jogador DOENTE não pode voltar direto para APTO.");
        }
        player.setCondition(newCondition);
        player.setUpdatedAt(LocalDateTime.now(clock));
        if (newCondition == Condition.DOENTE) {
            lineupService.removePlayer(id);
        }
        return PlayerView.from(playerRepository.save(player));
    }

    @Transactional
    public void delete(int id) {
        Player player = lockActive(id);
        if (player.getClubId() != null) {
            throw new ConflictException("Jogador que está num clube não pode ser excluído.");
        }
        LocalDateTime now = LocalDateTime.now(clock);
        player.setDeletedAt(now);
        player.setUpdatedAt(now);
        playerRepository.save(player);
    }

    private Player findActive(int id) {
        return playerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Jogador não encontrado."));
    }

    private Player lockActive(int id) {
        return playerRepository.findActiveForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Jogador não encontrado."));
    }
}
