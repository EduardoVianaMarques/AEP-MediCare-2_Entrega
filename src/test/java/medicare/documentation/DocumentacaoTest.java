package medicare.documentation;

import medicare.repository.MedicamentoRepository;
import medicare.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentacaoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicamentoRepository medicamentoRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void deveDisponibilizarFrontend() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/index.html"));
    }

    @Test
    void deveGerarContratoOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title")
                        .value("MediCare API"))
                .andExpect(jsonPath("$.paths['/api/medicamentos'].get")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/medicamentos'].post")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/medicamentos/{id}'].put")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/medicamentos/{id}'].delete")
                        .exists());
    }

    @Test
    void deveDisponibilizarSwaggerUi() throws Exception {
        mockMvc.perform(get("/docs"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(
                        "Location",
                        org.hamcrest.Matchers.containsString("swagger-ui")
                ));
    }
}