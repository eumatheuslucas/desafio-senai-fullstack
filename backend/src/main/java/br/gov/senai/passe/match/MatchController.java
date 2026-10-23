package br.gov.senai.passe.match;

import br.gov.senai.passe.shared.validation.IdParser;
import br.gov.senai.passe.shared.validation.JsonBodyReader;
import br.gov.senai.passe.shared.validation.QueryParams;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService service;
    private final JsonBodyReader bodyReader;

    @PostMapping
    public ResponseEntity<MatchView> play(@RequestBody(required = false) byte[] body) {
        MatchRequest request = MatchValidator.parse(bodyReader.read(body));
        MatchView match = service.play(request);
        return ResponseEntity.created(URI.create("/api/v1/matches/" + match.id())).body(match);
    }

    @GetMapping
    public List<MatchView> list(@RequestParam Map<String, String> params) {
        return service.list(new QueryParams(params).integer("club_id", 1, Integer.MAX_VALUE));
    }

    @GetMapping("/{id}")
    public MatchView get(@PathVariable String id) {
        return service.get(IdParser.parse(id));
    }
}
