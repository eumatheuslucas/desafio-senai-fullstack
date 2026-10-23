package br.gov.senai.passe.market;

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
@Table(name = "transacoes")
@Getter
@Setter
@NoArgsConstructor
public class MarketTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "clube_id")
    private Integer clubId;

    @Column(name = "jogador_id")
    private Integer playerId;

    @Column(name = "nome_jogador")
    private String playerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TransactionType type;

    @Column(name = "valor")
    private BigDecimal value;

    @Column(name = "taxa")
    private BigDecimal fee;

    @Column(name = "multa")
    private BigDecimal penalty;

    @Column(name = "lucro")
    private BigDecimal profit;

    @Column(name = "variacao_caixa")
    private BigDecimal cashChange;

    @Column(name = "criado_em")
    private LocalDateTime createdAt;
}
