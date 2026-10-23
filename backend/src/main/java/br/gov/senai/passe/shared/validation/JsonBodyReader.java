package br.gov.senai.passe.shared.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonBodyReader {

    private final ObjectMapper mapper;

    public JsonBody read(byte[] raw) {
        return JsonBody.parse(mapper, raw);
    }
}
