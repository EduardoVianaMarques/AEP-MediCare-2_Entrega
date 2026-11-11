package medicare.APIConfig;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MediCare API",
                version = "0.4.0",
                description = "Medicamentos e histórico de uso (segunda entrega AEP)"
        )
)
public class OpenApiConfiguration {
}
