package br.gov.senai.passe.match;

import br.gov.senai.passe.club.Club;
import br.gov.senai.passe.club.ClubRepository;
import br.gov.senai.passe.lineup.LineupService;
import br.gov.senai.passe.player.Player;
import br.gov.senai.passe.shared.error.NotFoundException;
import br.gov.senai.passe.shared.util.BrasiliaTime;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchDuelRepository duelRepository;
    private final ClubRepository clubRepository;
    private final LineupService lineupService;
    private final Clock clock;

    @Transactional
    public MatchView play(MatchRequest request) {
        Club home = findClub(request.homeClubId());
        Club away = findClub(request.awayClubId());
        List<Player> homeStarters = lineupService.completeStarters(home);
        List<Player> awayStarters = lineupService.completeStarters(away);

        List<MatchEngine.Duel> duels = MatchEngine.play(homeStarters, awayStarters);

        Match match = new Match();
        match.setHomeClubId(home.getId());
        match.setAwayClubId(away.getId());
        match.setHomeGoals((int) duels.stream().filter(duel -> duel.homeAttacking() && duel.goal()).count());
        match.setAwayGoals((int) duels.stream().filter(duel -> !duel.homeAttacking() && duel.goal()).count());
        match.setHomeFormation(home.getFormation());
        match.setAwayFormation(away.getFormation());
        match.setPlayedAt(LocalDateTime.now(clock));
        Match saved = matchRepository.save(match);

        List<MatchDuel> savedDuels = duelRepository.saveAll(
                duels.stream().map(duel -> toEntity(saved, duel, home, away)).toList());
        return toView(saved, Map.of(home.getId(), home.getName(), away.getId(), away.getName()), savedDuels);
    }

    @Transactional(readOnly = true)
    public MatchView get(int id) {
        Match match = matchRepository.findById(id).orElseThrow(() -> new NotFoundException("Partida não encontrada."));
        return toView(match, clubNames(), duelRepository.findByMatchIdOrderBySequence(id));
    }

    @Transactional(readOnly = true)
    public List<MatchView> list(Optional<Integer> clubId) {
        clubId.ifPresent(this::findClub);
        List<Match> matches = clubId
                .map(id -> matchRepository.findByHomeClubIdOrAwayClubIdOrderByPlayedAtDescIdDesc(id, id))
                .orElseGet(matchRepository::findAllByOrderByPlayedAtDescIdDesc);
        Map<Integer, String> names = clubNames();
        return matches.stream().map(match -> toView(match, names, null)).toList();
    }

    private Club findClub(int id) {
        return clubRepository.findById(id).orElseThrow(() -> new NotFoundException("Clube não encontrado."));
    }

    private Map<Integer, String> clubNames() {
        return clubRepository.findAll().stream().collect(Collectors.toMap(Club::getId, Club::getName));
    }

    private MatchDuel toEntity(Match match, MatchEngine.Duel duel, Club home, Club away) {
        MatchDuel entity = new MatchDuel();
        entity.setMatchId(match.getId());
        entity.setSequence(duel.order());
        entity.setAttackingClubId(duel.homeAttacking() ? home.getId() : away.getId());
        entity.setAttackerId(duel.attacker().getId());
        entity.setDefenderId(duel.defender().getId());
        entity.setAttackerName(duel.attacker().getName());
        entity.setDefenderName(duel.defender().getName());
        entity.setAttackerPosition(duel.attacker().getPosition());
        entity.setDefenderPosition(duel.defender().getPosition());
        entity.setAttackerCondition(duel.attacker().getCondition());
        entity.setDefenderCondition(duel.defender().getCondition());
        entity.setAttackAttribute(duel.attackAttribute());
        entity.setDefenseAttribute(duel.defenseAttribute());
        entity.setAttackValue(duel.attackValue());
        entity.setDefenseValue(duel.defenseValue());
        entity.setGoal(duel.goal());
        return entity;
    }

    private MatchView toView(Match match, Map<Integer, String> names, List<MatchDuel> duels) {
        MatchView.TeamResult home = new MatchView.TeamResult(match.getHomeClubId(),
                names.get(match.getHomeClubId()), match.getHomeFormation(), match.getHomeGoals());
        MatchView.TeamResult away = new MatchView.TeamResult(match.getAwayClubId(),
                names.get(match.getAwayClubId()), match.getAwayFormation(), match.getAwayGoals());
        List<MatchView.DuelView> duelViews = duels == null ? null : duels.stream().map(this::toDuelView).toList();
        return new MatchView(match.getId(), BrasiliaTime.format(match.getPlayedAt()), home, away,
                MatchView.resultOf(match.getHomeGoals(), match.getAwayGoals()), duelViews);
    }

    private MatchView.DuelView toDuelView(MatchDuel duel) {
        MatchView.DuelPlayer attacker = new MatchView.DuelPlayer(duel.getAttackerId(), duel.getAttackerName(),
                duel.getAttackerPosition(), duel.getAttackerCondition(), duel.getAttackAttribute(),
                duel.getAttackValue());
        MatchView.DuelPlayer defender = new MatchView.DuelPlayer(duel.getDefenderId(), duel.getDefenderName(),
                duel.getDefenderPosition(), duel.getDefenderCondition(), duel.getDefenseAttribute(),
                duel.getDefenseValue());
        return new MatchView.DuelView(duel.getSequence(), duel.getAttackingClubId(), attacker, defender,
                duel.isGoal());
    }
}
