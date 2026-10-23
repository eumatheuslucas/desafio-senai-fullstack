package br.gov.senai.passe.club;

import br.gov.senai.passe.contract.Contract;
import br.gov.senai.passe.contract.ContractRepository;
import br.gov.senai.passe.contract.ContractStatus;
import br.gov.senai.passe.player.Player;
import br.gov.senai.passe.player.PlayerOrder;
import br.gov.senai.passe.player.PlayerPricing;
import br.gov.senai.passe.player.PlayerRepository;
import br.gov.senai.passe.player.PlayerView;
import br.gov.senai.passe.shared.error.ConflictException;
import br.gov.senai.passe.shared.error.NotFoundException;
import br.gov.senai.passe.shared.util.Money;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClubService {

    private static final int MAX_CLUBS = 20;
    private static final BigDecimal INITIAL_CASH = new BigDecimal("300.00");

    private final ClubRepository clubRepository;
    private final PlayerRepository playerRepository;
    private final ContractRepository contractRepository;
    private final Clock clock;

    @Transactional
    public ClubView create(CreateClubCommand command) {
        if (clubRepository.existsByName(command.name())) {
            throw new ConflictException("Já existe um clube com esse nome.");
        }
        if (clubRepository.existsByAcronym(command.acronym())) {
            throw new ConflictException("Já existe um clube com essa sigla.");
        }
        if (clubRepository.count() >= MAX_CLUBS) {
            throw new ConflictException("A liga aceita no máximo " + MAX_CLUBS + " clubes.");
        }
        Club club = new Club();
        club.setName(command.name());
        club.setAcronym(command.acronym());
        club.setCash(INITIAL_CASH);
        club.setCreatedAt(LocalDateTime.now(clock));
        Club saved = clubRepository.save(club);
        return ClubView.of(saved, Money.ZERO);
    }

    @Transactional(readOnly = true)
    public List<ClubView> list(ClubSort sort) {
        return clubRepository.findAll().stream()
                .map(club -> ClubView.of(club, squadValue(squadOf(club.getId()))))
                .sorted(sort.comparator())
                .toList();
    }

    @Transactional(readOnly = true)
    public ClubDetailView get(int id) {
        Club club = clubRepository.findById(id).orElseThrow(() -> new NotFoundException("Clube não encontrado."));
        List<Player> squad = squadOf(id);
        LocalDateTime now = LocalDateTime.now(clock);
        Map<Integer, Contract> contracts = contractRepository.findByClubIdAndStatus(id, ContractStatus.ATIVO).stream()
                .collect(Collectors.toMap(Contract::getPlayerId, Function.identity()));
        List<SquadPlayerView> players = squad.stream()
                .map(player -> new SquadPlayerView(PlayerView.from(player),
                        ContractView.of(contracts.get(player.getId()), now)))
                .toList();
        return new ClubDetailView(ClubView.of(club, squadValue(squad)), players);
    }

    private List<Player> squadOf(Integer clubId) {
        return playerRepository.findByClubIdAndDeletedAtIsNull(clubId).stream()
                .sorted(PlayerOrder.SQUAD)
                .toList();
    }

    private BigDecimal squadValue(List<Player> squad) {
        return squad.stream().map(PlayerPricing::marketValue).reduce(Money.ZERO, BigDecimal::add);
    }
}
