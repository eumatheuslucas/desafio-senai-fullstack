package br.gov.senai.passe.shared.validation;

import br.gov.senai.passe.shared.error.BadRequestException;
import br.gov.senai.passe.shared.util.TextNormalizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class JsonBody {

    private final JsonNode root;

    private JsonBody(JsonNode root) {
        this.root = root;
    }

    public static JsonBody parse(ObjectMapper mapper, byte[] raw) {
        if (raw == null || raw.length == 0) {
            throw invalidBody();
        }
        try {
            JsonNode node = mapper.readTree(new String(raw, StandardCharsets.UTF_8));
            if (node == null || !node.isObject()) {
                throw invalidBody();
            }
            return new JsonBody(node);
        } catch (JsonProcessingException exception) {
            throw invalidBody();
        }
    }

    public int requiredInt(String field, int min, int max) {
        return readInt(required(field), field, min, max);
    }

    public String requiredText(String field) {
        JsonNode node = required(field);
        if (!node.isTextual()) {
            throw new BadRequestException("O campo '" + field + "' deve ser um texto.");
        }
        return node.textValue();
    }

    public String requiredName(String field) {
        String cleaned = TextNormalizer.clean(requiredText(field));
        if (cleaned.isEmpty()) {
            throw missing(field);
        }
        return cleaned;
    }

    public <E extends Enum<E>> E requiredEnum(String field, Class<E> type) {
        String text = requiredText(field);
        try {
            return Enum.valueOf(type, text);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Valor inválido para o campo '" + field + "'.");
        }
    }

    public List<Integer> requiredIntArray(String field) {
        JsonNode node = required(field);
        if (!node.isArray()) {
            throw new BadRequestException("O campo '" + field + "' deve ser uma lista.");
        }
        List<Integer> values = new ArrayList<>();
        for (JsonNode element : node) {
            values.add(readInt(element, field, 1, Integer.MAX_VALUE));
        }
        return values;
    }

    private JsonNode required(String field) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            throw missing(field);
        }
        return node;
    }

    private static int readInt(JsonNode node, String field, int min, int max) {
        if (!node.isNumber()) {
            throw new BadRequestException("O campo '" + field + "' deve ser um número inteiro.");
        }
        BigDecimal value = node.decimalValue();
        if (value.stripTrailingZeros().scale() > 0) {
            throw new BadRequestException("O campo '" + field + "' deve ser um número inteiro.");
        }
        if (value.compareTo(BigDecimal.valueOf(min)) < 0 || value.compareTo(BigDecimal.valueOf(max)) > 0) {
            throw new BadRequestException("O campo '" + field + "' deve estar entre " + min + " e " + max + ".");
        }
        return value.intValue();
    }

    private static BadRequestException missing(String field) {
        return new BadRequestException("O campo '" + field + "' é obrigatório.");
    }

    private static BadRequestException invalidBody() {
        return new BadRequestException("O corpo da requisição deve ser um objeto JSON válido.");
    }
}
