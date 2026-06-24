package br.com.sistemacalculosjudiciais.config;

import br.com.sistemacalculosjudiciais.service.IndiceEconomicoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

// Runner executado na inicialização da aplicação para importar automaticamente os índices SELIC padrão
@Component
@ConditionalOnProperty(prefix = "app.indices", name = "import-selic-startup", havingValue = "true", matchIfMissing = true)
public class SelicStartupImportRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(SelicStartupImportRunner.class);

    private final IndiceEconomicoService indiceEconomicoService;

    public SelicStartupImportRunner(IndiceEconomicoService indiceEconomicoService) {
        this.indiceEconomicoService = indiceEconomicoService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            var response = indiceEconomicoService.importarSelicPadrao();
            logger.info("{} importado automaticamente na inicialização com {} registros ({}) vindos de {}. Persistido: {}.",
                    response.tipoIndice(), response.registrosImportados(), response.registrosPersistidos(), response.origem(), response.persistido());
        } catch (Exception exception) {
            logger.warn("Falha ao importar SELIC na inicialização. O sistema continuará iniciando sem a carga automática.", exception);
        }
    }
}