package br.gov.senai.passe.club;

import br.gov.senai.passe.shared.error.BadRequestException;
import br.gov.senai.passe.shared.util.TextNormalizer;
import br.gov.senai.passe.shared.validation.JsonBody;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ClubValidator {

    private static final Pattern ACRONYM = Pattern.compile("[A-Z]{3}");

    private ClubValidator() {
    }

    public static CreateClubCommand parseCreate(JsonBody body) {
        String name = body.requiredName("name");
        validateName(name);
        String acronym = body.requiredText("acronym");
        validateAcronym(acronym, name);
        return new CreateClubCommand(name, acronym);
    }

    private static void validateName(String name) {
        int length = TextNormalizer.length(name);
        if (length < 3 || length > 60) {
            throw new BadRequestException("O nome do clube deve ter de 3 a 60 caracteres.");
        }
        boolean validCharacters = name.codePoints()
                .allMatch(point -> Character.isLetter(point) || point == ' ' || point == '-' || point == '.');
        if (!validCharacters) {
            throw new BadRequestException("O nome do clube só pode ter letras, espaços, hífen e ponto.");
        }
        long words = TextNormalizer.wordCount(name);
        if (words < 2 || words > 5) {
            throw new BadRequestException("O nome do clube deve ter de 2 a 5 palavras.");
        }
    }

    private static void validateAcronym(String acronym, String name) {
        if (!ACRONYM.matcher(acronym).matches()) {
            throw new BadRequestException("A sigla deve ter exatamente 3 letras de A a Z, maiúsculas.");
        }
        if (acronym.charAt(0) == acronym.charAt(1) && acronym.charAt(1) == acronym.charAt(2)) {
            throw new BadRequestException("A sigla não pode ter as três letras iguais.");
        }
        String letters = TextNormalizer.fold(name).toUpperCase(Locale.ROOT)
                .chars()
                .filter(Character::isLetter)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        if (letters.isEmpty() || letters.charAt(0) != acronym.charAt(0)) {
            throw new BadRequestException("A primeira letra da sigla deve ser a primeira letra do nome.");
        }
        int matched = 0;
        for (int index = 0; index < letters.length() && matched < 3; index++) {
            if (letters.charAt(index) == acronym.charAt(matched)) {
                matched++;
            }
        }
        if (matched < 3) {
            throw new BadRequestException("As letras da sigla devem aparecer no nome, nessa ordem.");
        }
    }
}
