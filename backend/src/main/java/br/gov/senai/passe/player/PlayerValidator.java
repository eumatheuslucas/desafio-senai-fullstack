package br.gov.senai.passe.player;

import br.gov.senai.passe.shared.error.BadRequestException;
import br.gov.senai.passe.shared.util.TextNormalizer;
import br.gov.senai.passe.shared.validation.JsonBody;

public final class PlayerValidator {

    private static final int MAX_ATTRIBUTE_SUM = 480;
    private static final int OLD_AGE_LIMIT = 35;
    private static final int OLD_AGILITY_LIMIT = 85;
    private static final int NO_GROWTH_AGE = 33;

    private PlayerValidator() {
    }

    public static CreatePlayerCommand parseCreate(JsonBody body) {
        String name = parseName(body);
        Position position = body.requiredEnum("position", Position.class);
        int age = body.requiredInt("age", 16, 45);
        int agility = parseAgility(body, age);
        int pace = body.requiredInt("pace", 1, 99);
        int shooting = body.requiredInt("shooting", 1, 99);
        int passing = body.requiredInt("passing", 1, 99);
        int dribbling = body.requiredInt("dribbling", 1, 99);
        int defending = body.requiredInt("defending", 1, 99);
        int physical = body.requiredInt("physical", 1, 99);
        int potential = body.requiredInt("potential", 1, 99);

        int sum = pace + shooting + passing + dribbling + defending + physical;
        if (sum > MAX_ATTRIBUTE_SUM) {
            throw new BadRequestException("A soma dos seis atributos não pode passar de " + MAX_ATTRIBUTE_SUM + ".");
        }
        int highest = Math.max(Math.max(Math.max(pace, shooting), Math.max(passing, dribbling)),
                Math.max(defending, physical));
        if (potential < highest) {
            throw new BadRequestException("O potencial não pode ser menor que o maior atributo.");
        }
        if (age >= NO_GROWTH_AGE && potential != highest) {
            throw new BadRequestException("Com 33 anos ou mais, o potencial deve ser igual ao maior atributo.");
        }
        return new CreatePlayerCommand(name, position, agility, age, pace, shooting, passing, dribbling,
                defending, physical, potential);
    }

    public static UpdatePlayerCommand parseUpdate(JsonBody body) {
        String name = parseName(body);
        Position position = body.requiredEnum("position", Position.class);
        int age = body.requiredInt("age", 16, 45);
        int agility = parseAgility(body, age);
        return new UpdatePlayerCommand(name, position, agility, age);
    }

    public static Condition parseCondition(JsonBody body) {
        return body.requiredEnum("condition", Condition.class);
    }

    private static String parseName(JsonBody body) {
        String name = body.requiredName("name");
        int length = TextNormalizer.length(name);
        if (length < 3 || length > 100) {
            throw new BadRequestException("O nome deve ter de 3 a 100 caracteres.");
        }
        if (TextNormalizer.wordCount(name) < 2) {
            throw new BadRequestException("O nome deve ter pelo menos duas palavras.");
        }
        if (name.chars().anyMatch(character -> character >= '0' && character <= '9')) {
            throw new BadRequestException("O nome não pode conter dígitos.");
        }
        return name;
    }

    private static int parseAgility(JsonBody body, int age) {
        int agility = body.requiredInt("agility", 1, 100);
        if (age >= OLD_AGE_LIMIT && agility > OLD_AGILITY_LIMIT) {
            throw new BadRequestException("Com 35 anos ou mais, a agilidade é de no máximo 85.");
        }
        return agility;
    }
}
