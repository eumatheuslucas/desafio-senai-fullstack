package br.gov.senai.passe.contract;

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
@Table(name = "contratos")
@Getter
@Setter
@NoArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "jogador_id")
    private Integer playerId;

    @Column(name = "clube_id")
    private Integer clubId;

    @Column(name = "valor_compra")
    private BigDecimal purchaseValue;

    @Column(name = "fidelidade_ate")
    private LocalDateTime loyaltyUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao")
    private ContractStatus status;

    @Column(name = "iniciado_em")
    private LocalDateTime startedAt;

    @Column(name = "encerrado_em")
    private LocalDateTime endedAt;
}
