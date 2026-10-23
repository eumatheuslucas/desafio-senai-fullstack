package br.gov.senai.passe.market;

import br.gov.senai.passe.shared.validation.IdParser;
import br.gov.senai.passe.shared.validation.QueryParams;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clubs/{id}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @GetMapping
    public HistoryView history(@PathVariable String id, @RequestParam Map<String, String> params) {
        TransactionFilter filter = TransactionFilter.parse(new QueryParams(params));
        return service.history(IdParser.parse(id), filter);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@PathVariable String id, @RequestParam Map<String, String> params) {
        int clubId = IdParser.parse(id);
        TransactionFilter filter = TransactionFilter.parse(new QueryParams(params));
        String csv = service.exportCsv(clubId, filter);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transacoes-clube-" + clubId + ".csv\"")
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
}
