package br.gov.senai.passe.player;

import br.gov.senai.passe.shared.PageView;
import br.gov.senai.passe.shared.validation.IdParser;
import br.gov.senai.passe.shared.validation.JsonBodyReader;
import br.gov.senai.passe.shared.validation.QueryParams;
import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService service;
    private final JsonBodyReader bodyReader;

    @GetMapping
    public PageView<PlayerView> list(@RequestParam Map<String, String> params) {
        return service.list(PlayerFilterParser.parse(new QueryParams(params)));
    }

    @GetMapping("/{id}")
    public PlayerView get(@PathVariable String id) {
        return service.get(IdParser.parse(id));
    }

    @PostMapping
    public ResponseEntity<PlayerView> create(@RequestBody(required = false) byte[] body) {
        CreatePlayerCommand command = PlayerValidator.parseCreate(bodyReader.read(body));
        PlayerView created = service.create(command);
        return ResponseEntity.created(URI.create("/api/v1/players/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public PlayerView update(@PathVariable String id, @RequestBody(required = false) byte[] body) {
        int playerId = IdParser.parse(id);
        UpdatePlayerCommand command = PlayerValidator.parseUpdate(bodyReader.read(body));
        return service.update(playerId, command);
    }

    @PatchMapping("/{id}/condition")
    public PlayerView changeCondition(@PathVariable String id, @RequestBody(required = false) byte[] body) {
        int playerId = IdParser.parse(id);
        Condition condition = PlayerValidator.parseCondition(bodyReader.read(body));
        return service.changeCondition(playerId, condition);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(IdParser.parse(id));
        return ResponseEntity.noContent().build();
    }
}
