package br.gov.senai.passe.match;

import br.gov.senai.passe.player.Condition;
import br.gov.senai.passe.player.Position;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "duelos_partida")
@Getter
@Setter
@NoArgsConstructor
public class MatchDuel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "partida_id")
    private Integer matchId;

    @Column(name = "ordem")
    private int sequence;

    @Column(name = "clube_atacante_id")
    private Integer attackingClubId;

    @Column(name = "atacante_id")
    private Integer attackerId;

    @Column(name = "defensor_id")
    private Integer defenderId;

    @Column(name = "nome_atacante")
    private String attackerName;

    @Column(name = "nome_defensor")
    private String defenderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "posicao_atacante")
    private Position attackerPosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "posicao_defensor")
    private Position defenderPosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicao_atacante")
    private Condition attackerCondition;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicao_defensor")
    private Condition defenderCondition;

    @Column(name = "atributo_ataque")
    private String attackAttribute;

    @Column(name = "atributo_defesa")
    private String defenseAttribute;

    @Column(name = "valor_ataque")
    private int attackValue;

    @Column(name = "valor_defesa")
    private int defenseValue;

    @Column(name = "gol")
    private boolean goal;
}
