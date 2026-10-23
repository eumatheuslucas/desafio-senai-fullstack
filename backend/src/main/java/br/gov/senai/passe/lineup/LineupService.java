package br.gov.senai.passe.lineup;

import br.gov.senai.passe.club.Club;
import br.gov.senai.passe.club.ClubRepository;
import br.gov.senai.passe.player.Condition;
import br.gov.senai.passe.player.Player;
import br.gov.senai.passe.player.PlayerOrder;
import br.gov.senai.passe.player.PlayerRepository;
import br.gov.senai.passe.player.PlayerView;
import br.gov.senai.passe.player.Position;
import br.gov.senai.passe.shared.error.ConflictException;
import br.gov.senai.passe.shared.error.NotFoundException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LineupService {

    private final LineupRepository lineupRepository;
    private final ClubRepository clubRepository;
    private final PlayerRepository playerRepository;
    private final Clock clock;

    @Transactional
    public LineupView replace(int clubId, LineupRequest request) {
        Club club = clubRepository.findByIdForUpdate(clubId)
                .orElseThrow(() -> new NotFoundException("Clube não encontrado."));
        List<Player> players = new ArrayList<>();
        for (Integer playerId : request.playerIds()) {
            players.add(playerRepository.findByIdAndDeletedAtIsNull(playerId)
                    .orElseThrow(() -> new NotFoundException("Jogador " + playerId + " não encontrado.")));
        }
        checkEligibility(club, players);
        checkFormation(request.formation(), players);

        LocalDateTime now = LocalDateTime.now(clock);
        lineupRepository.deleteAllByClub(clubId);
        List<LineupSlot> slots = players.stream().map(player -> newSlot(clubId, player, now)).toList();
        lineupRepository.saveAll(slots);
        club.setFormation(request.formation().label());
        clubRepository.save(club);
        return view(club, players);
    }

    @Transactional(readOnly = true)
    public LineupView get(int clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new NotFoundException("Clube não encontrado."));
        return view(club, startersOf(clubId));
    }

    @Transactional
    public void removePlayer(int playerId) {
        lineupRepository.deleteByPlayer(playerId);
    }

    @Transactional(readOnly = true)
    public List<Player> completeStarters(Club club) {
        List<Player> starters = startersOf(club.getId());
        if (club.getFormation() == null || starters.size() != LineupView.STARTERS) {
            throw new ConflictException("O clube " + club.getName() + " não tem escalação completa.");
        }
        return starters;
    }

    private List<Player> startersOf(Integer clubId) {
        List<Integer> ids = lineupRepository.findByClubId(clubId).stream().map(LineupSlot::getPlayerId).toList();
        return playerRepository.findAllById(ids).stream().sorted(PlayerOrder.SQUAD).toList();
    }

    private LineupSlot newSlot(int clubId, Player player, LocalDateTime now) {
        LineupSlot slot = new LineupSlot();
        slot.setClubId(clubId);
        slot.setPlayerId(player.getId());
        slot.setCreatedAt(now);
        return slot;
    }

    private void checkEligibility(Club club, List<Player> players) {
        for (Player player : players) {
            if (!club.getId().equals(player.getClubId())) {
                throw new ConflictException("O jogador " + player.getName() + " não é do elenco do clube.");
            }
            if (player.getCondition() == Condition.DOENTE) {
                throw new ConflictException("O jogador " + player.getName() + " está DOENTE e não pode ser escalado.");
            }
        }
    }

    private void checkFormation(Formation formation, List<Player> players) {
        long goalkeepers = count(players, Position.GOL);
        long defenders = count(players, Position.ZAG) + count(players, Position.LAT);
        boolean fits = goalkeepers == 1
                && defenders == formation.defenders()
                && count(players, Position.MEI) == formation.midfielders()
                && count(players, Position.ATA) == formation.attackers();
        if (!fits) {
            throw new ConflictException("As posições escolhidas não fecham a formação " + formation.label() + ".");
        }
    }

    private long count(List<Player> players, Position position) {
        return players.stream().filter(player -> player.getPosition() == position).count();
    }

    private LineupView view(Club club, List<Player> players) {
        List<PlayerView> views = players.stream().sorted(PlayerOrder.SQUAD).map(PlayerView::from).toList();
        boolean complete = club.getFormation() != null && players.size() == LineupView.STARTERS;
        return new LineupView(club.getId(), club.getFormation(), complete, views);
    }
}
