package br.gov.senai.passe.shared.validation;

import br.gov.senai.passe.shared.error.BadRequestException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public final class QueryParams {

    private static final Pattern INTEGER = Pattern.compile("[1-9][0-9]*");
    private static final Pattern DECIMAL = Pattern.compile("(0|[1-9][0-9]*)(\\.[0-9]{1,2})?");

    private final Map<String, String> raw;

    public QueryParams(Map<String, String> raw) {
        this.raw = raw;
    }

    public Optional<String> text(String name) {
        String value = raw.get(name);
        if (value == null) {
            return Optional.empty();
        }
        if (value.isEmpty()) {
            throw new BadRequestException("O parâmetro '" + name + "' não pode ser vazio.");
        }
        return Optional.of(value);
    }

    public Optional<Integer> integer(String name, int min, int max) {
        return text(name).map(value -> {
            if (!INTEGER.matcher(value).matches() || value.length() > 10) {
                throw invalid(name);
            }
            long number = Long.parseLong(value);
            if (number < min || number > max) {
                throw new BadRequestException("O parâmetro '" + name + "' deve estar entre " + min + " e " + max + ".");
            }
            return (int) number;
        });
    }

    public Optional<BigDecimal> decimal(String name, int min, int max) {
        return text(name).map(value -> {
            if (!DECIMAL.matcher(value).matches()) {
                throw invalid(name);
            }
            BigDecimal number = new BigDecimal(value);
            if (number.compareTo(BigDecimal.valueOf(min)) < 0 || number.compareTo(BigDecimal.valueOf(max)) > 0) {
                throw new BadRequestException("O parâmetro '" + name + "' deve estar entre " + min + " e " + max + ".");
            }
            return number;
        });
    }

    public <E extends Enum<E>> Optional<E> enumValue(String name, Class<E> type) {
        return text(name).map(value -> {
            try {
                return Enum.valueOf(type, value);
            } catch (IllegalArgumentException exception) {
                throw invalid(name);
            }
        });
    }

    public Optional<Boolean> bool(String name) {
        return text(name).map(value -> {
            if (value.equals("true")) {
                return true;
            }
            if (value.equals("false")) {
                return false;
            }
            throw invalid(name);
        });
    }

    private static BadRequestException invalid(String name) {
        return new BadRequestException("Valor inválido para o parâmetro '" + name + "'.");
    }
}
