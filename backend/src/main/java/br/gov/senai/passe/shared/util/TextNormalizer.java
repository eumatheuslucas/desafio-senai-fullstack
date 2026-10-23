package br.gov.senai.passe.shared.util;

import br.gov.senai.passe.shared.error.BadRequestException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

public final class TextNormalizer {

    private TextNormalizer() {
    }

    public static String fold(String text) {
        String decomposed = Normalizer.normalize(text, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{Mn}", "").toLowerCase(Locale.ROOT);
    }

    public static String clean(String text) {
        boolean hasControl = text.codePoints().anyMatch(point -> Character.getType(point) == Character.CONTROL);
        if (hasControl) {
            throw new BadRequestException("O texto não pode conter caracteres de controle.");
        }
        return text.replaceAll(" {2,}", " ").replaceAll("^ +| +$", "");
    }

    public static int length(String text) {
        return text.codePointCount(0, text.length());
    }

    public static long wordCount(String text) {
        return Arrays.stream(text.split(" "))
                .filter(word -> word.codePoints().anyMatch(Character::isLetter))
                .count();
    }
}
