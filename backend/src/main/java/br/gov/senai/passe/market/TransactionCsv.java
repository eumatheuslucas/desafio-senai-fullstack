package br.gov.senai.passe.market;

import br.gov.senai.passe.shared.util.BrasiliaTime;
import br.gov.senai.passe.shared.util.Money;
import java.math.BigDecimal;
import java.util.List;

public final class TransactionCsv {

    private static final String HEADER =
            "data_hora;tipo;jogador;valor;taxa_ou_comissao;multa;lucro;variacao_caixa\n";

    private TransactionCsv() {
    }

    public static String render(List<MarketTransaction> transactions) {
        StringBuilder csv = new StringBuilder(HEADER);
        for (MarketTransaction transaction : transactions) {
            csv.append(BrasiliaTime.format(transaction.getCreatedAt())).append(';')
                    .append(transaction.getType()).append(';')
                    .append(quote(transaction.getPlayerName())).append(';')
                    .append(money(transaction.getValue())).append(';')
                    .append(money(transaction.getFee())).append(';')
                    .append(money(transaction.getPenalty())).append(';')
                    .append(transaction.getProfit() == null ? "" : money(transaction.getProfit())).append(';')
                    .append(money(transaction.getCashChange())).append('\n');
        }
        return csv.toString();
    }

    private static String money(BigDecimal value) {
        return Money.round(value).toPlainString().replace('.', ',');
    }

    private static String quote(String text) {
        if (text.contains(";") || text.contains("\"")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
