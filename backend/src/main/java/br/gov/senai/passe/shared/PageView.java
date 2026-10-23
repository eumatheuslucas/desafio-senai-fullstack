package br.gov.senai.passe.shared;

import java.util.List;

public record PageView<T>(List<T> data, Meta meta) {

    public record Meta(int page, int limit, long totalItems, int totalPages) {
    }
}
