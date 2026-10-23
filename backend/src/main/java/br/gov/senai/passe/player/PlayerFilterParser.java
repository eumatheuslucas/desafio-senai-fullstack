package br.gov.senai.passe.player;

import br.gov.senai.passe.shared.error.BadRequestException;
import br.gov.senai.passe.shared.util.TextNormalizer;
import br.gov.senai.passe.shared.validation.QueryParams;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class PlayerFilterParser {

    private static final int MAX_SORT_CRITERIA = 2;

    private PlayerFilterParser() {
    }

    public static PlayerFilter parse(QueryParams params) {
        Optional<String> search = params.text("search").map(PlayerFilterParser::parseSearch);
        Optional<Position> position = params.enumValue("position", Position.class);
        Optional<Condition> condition = params.enumValue("condition", Condition.class);
        Optional<Boolean> free = params.bool("free");
        Optional<Integer> clubId = params.integer("club_id", 1, Integer.MAX_VALUE);
        int minAge = params.integer("min_age", 16, 45).orElse(16);
        int maxAge = params.integer("max_age", 16, 45).orElse(45);
        BigDecimal minValue = params.decimal("min_value", 0, 550).orElse(new BigDecimal("0.00"));
        BigDecimal maxValue = params.decimal("max_value", 0, 550).orElse(new BigDecimal("550.00"));
        List<PlayerSort> sort = parseSort(params.text("sort").orElse("name"));
        int page = params.integer("page", 1, Integer.MAX_VALUE).orElse(1);
        int limit = params.integer("limit", 1, 50).orElse(10);

        if (minAge > maxAge) {
            throw new BadRequestException("min_age não pode ser maior que max_age.");
        }
        if (minValue.compareTo(maxValue) > 0) {
            throw new BadRequestException("min_value não pode ser maior que max_value.");
        }
        if (free.orElse(false) && clubId.isPresent()) {
            throw new BadRequestException("Os filtros free=true e club_id não podem ser combinados.");
        }
        return new PlayerFilter(search, position, condition, free, clubId, minAge, maxAge, minValue, maxValue,
                sort, page, limit);
    }

    private static String parseSearch(String raw) {
        String cleaned = TextNormalizer.clean(raw);
        int length = TextNormalizer.length(cleaned);
        if (length < 2 || length > 100) {
            throw new BadRequestException("A busca deve ter de 2 a 100 caracteres.");
        }
        return TextNormalizer.fold(cleaned);
    }

    private static List<PlayerSort> parseSort(String raw) {
        String[] tokens = raw.split(",", -1);
        if (tokens.length > MAX_SORT_CRITERIA) {
            throw new BadRequestException("O parâmetro 'sort' aceita no máximo dois critérios.");
        }
        List<PlayerSort> criteria = new ArrayList<>();
        Set<String> fields = new HashSet<>();
        for (String token : tokens) {
            PlayerSort criterion = findSort(token);
            if (!fields.add(criterion.field())) {
                throw new BadRequestException("O parâmetro 'sort' não pode repetir o campo.");
            }
            criteria.add(criterion);
        }
        return criteria;
    }

    private static PlayerSort findSort(String token) {
        for (PlayerSort candidate : PlayerSort.values()) {
            if (candidate.label().equals(token)) {
                return candidate;
            }
        }
        throw new BadRequestException("Valor inválido para o parâmetro 'sort'.");
    }
}
