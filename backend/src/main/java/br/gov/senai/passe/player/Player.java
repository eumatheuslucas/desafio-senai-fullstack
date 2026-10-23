package br.gov.senai.passe.player;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jogadores")
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "posicao")
    private Position position;

    @Column(name = "agilidade")
    private int agility;

    @Column(name = "idade")
    private int age;

    @Column(name = "valor_base")
    private BigDecimal baseValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicao")
    private Condition condition;

    @Column(name = "clube_id")
    private Integer clubId;

    @Column(name = "velocidade")
    private int pace;

    @Column(name = "finalizacao")
    private int shooting;

    @Column(name = "passe")
    private int passing;

    @Column(name = "drible")
    private int dribbling;

    @Column(name = "defesa")
    private int defending;

    @Column(name = "fisico")
    private int physical;

    @Column(name = "potencial")
    private int potential;

    @Column(name = "excluido_em")
    private LocalDateTime deletedAt;

    @Column(name = "criado_em")
    private LocalDateTime createdAt;

    @Column(name = "atualizado_em")
    private LocalDateTime updatedAt;
}
