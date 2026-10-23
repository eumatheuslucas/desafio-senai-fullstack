package br.gov.senai.passe.market;

import java.util.List;

public record HistoryView(List<TransactionView> data, TransactionSummary summary) {
}
