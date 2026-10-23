package br.gov.senai.passe.market;

import br.gov.senai.passe.shared.validation.IdParser;
import br.gov.senai.passe.shared.validation.JsonBodyReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players/{id}")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService service;
    private final BuyValidator buyValidator;
    private final JsonBodyReader bodyReader;

    @PostMapping("/buy")
    public PurchaseView buy(@PathVariable String id, @RequestBody(required = false) byte[] body) {
        int playerId = IdParser.parse(id);
        BuyCommand command = buyValidator.parse(bodyReader.read(body));
        return service.buy(playerId, command);
    }

    @PostMapping("/sell")
    public SaleView sell(@PathVariable String id) {
        return service.sell(IdParser.parse(id));
    }
}
