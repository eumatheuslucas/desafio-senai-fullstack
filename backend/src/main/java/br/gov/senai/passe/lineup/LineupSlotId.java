package br.gov.senai.passe.lineup;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LineupSlotId implements Serializable {

    private Integer clubId;
    private Integer playerId;
}
