package br.gov.senai.passe.shared.validation;

import br.gov.senai.passe.shared.error.NotFoundException;
import java.util.regex.Pattern;

public final class IdParser {

    private static final Pattern ID = Pattern.compile("[1-9][0-9]{0,9}");

    private IdParser() {
    }

    public static int parse(String raw) {
        if (!ID.matcher(raw).matches() || Long.parseLong(raw) > Integer.MAX_VALUE) {
            throw new NotFoundException("Recurso não encontrado.");
        }
        return Integer.parseInt(raw);
    }
}
