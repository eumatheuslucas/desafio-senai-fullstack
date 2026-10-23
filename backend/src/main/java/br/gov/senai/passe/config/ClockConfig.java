package br.gov.senai.passe.config;

import br.gov.senai.passe.shared.util.BrasiliaTime;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.system(BrasiliaTime.OFFSET);
    }
}
