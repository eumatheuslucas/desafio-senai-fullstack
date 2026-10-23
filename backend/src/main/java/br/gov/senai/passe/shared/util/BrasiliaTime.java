package br.gov.senai.passe.shared.util;

import br.gov.senai.passe.shared.error.BadRequestException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

public final class BrasiliaTime {

    public static final ZoneOffset OFFSET = ZoneOffset.ofHours(-3);

    private static final Pattern INPUT_SHAPE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}-03:00");
    private static final DateTimeFormatter INPUT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm'-03:00'").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss'-03:00'");

    private BrasiliaTime() {
    }

    public static LocalDateTime parse(String text) {
        if (!INPUT_SHAPE.matcher(text).matches()) {
            throw invalid();
        }
        try {
            return LocalDateTime.parse(text, INPUT);
        } catch (DateTimeParseException exception) {
            throw invalid();
        }
    }

    public static String format(LocalDateTime dateTime) {
        return OUTPUT.format(dateTime);
    }

    private static BadRequestException invalid() {
        return new BadRequestException("Data e hora inválida. Use o formato AAAA-MM-DDTHH:MM-03:00.");
    }
}
