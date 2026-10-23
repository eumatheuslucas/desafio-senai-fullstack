package br.gov.senai.passe.match;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partidas")
@Getter
@Setter
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "mandante_id")
    private Integer homeClubId;

    @Column(name = "visitante_id")
    private Integer awayClubId;

    @Column(name = "gols_mandante")
    private int homeGoals;

    @Column(name = "gols_visitante")
    private int awayGoals;

    @Column(name = "formacao_mandante")
    private String homeFormation;

    @Column(name = "formacao_visitante")
    private String awayFormation;

    @Column(name = "jogada_em")
    private LocalDateTime playedAt;
}
