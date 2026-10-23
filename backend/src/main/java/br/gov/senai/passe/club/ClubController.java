package br.gov.senai.passe.club;

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
@RequestMapping("/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService service;
    private final JsonBodyReader bodyReader;

    @GetMapping
    public List<ClubView> list(@RequestParam Map<String, String> params) {
        ClubSort sort = new QueryParams(params).text("sort").map(ClubSort::parse).orElse(ClubSort.NAME);
        return service.list(sort);
    }

    @PostMapping
    public ResponseEntity<ClubView> create(@RequestBody(required = false) byte[] body) {
        CreateClubCommand command = ClubValidator.parseCreate(bodyReader.read(body));
        ClubView created = service.create(command);
        return ResponseEntity.created(URI.create("/api/v1/clubs/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public ClubDetailView get(@PathVariable String id) {
        return service.get(IdParser.parse(id));
    }
}
