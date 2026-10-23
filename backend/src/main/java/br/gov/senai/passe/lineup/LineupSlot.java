package br.gov.senai.passe.lineup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "escalacoes")
@IdClass(LineupSlotId.class)
@Getter
@Setter
@NoArgsConstructor
public class LineupSlot {

    @Id
    @Column(name = "clube_id")
    private Integer clubId;

    @Id
    @Column(name = "jogador_id")
    private Integer playerId;

    @Column(name = "criado_em")
    private LocalDateTime createdAt;
}
