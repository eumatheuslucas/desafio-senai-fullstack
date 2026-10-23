package br.gov.senai.passe.lineup;

import br.gov.senai.passe.shared.validation.IdParser;
import br.gov.senai.passe.shared.validation.JsonBodyReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clubs/{id}/lineup")
@RequiredArgsConstructor
public class LineupController {

    private final LineupService service;
    private final JsonBodyReader bodyReader;

    @PutMapping
    public LineupView replace(@PathVariable String id, @RequestBody(required = false) byte[] body) {
        int clubId = IdParser.parse(id);
        return service.replace(clubId, LineupValidator.parse(bodyReader.read(body)));
    }

    @GetMapping
    public LineupView get(@PathVariable String id) {
        return service.get(IdParser.parse(id));
    }
}
