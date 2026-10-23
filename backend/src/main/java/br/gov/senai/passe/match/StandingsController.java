package br.gov.senai.passe.match;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StandingsController {

    private final StandingsService service;

    @GetMapping("/standings")
    public List<StandingRow> standings() {
        return service.standings();
    }
}
