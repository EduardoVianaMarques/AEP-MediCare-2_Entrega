package medicare.controller;

import medicare.service.HistoricoService;
import medicare.model.RegistroUso;
import medicare.exception.MedicamentoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HistoricoController.class)
class HistoricoControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean HistoricoService service;

    @Test void exigeSessaoEmAmbasOperacoes() throws Exception {
        mvc.perform(get("/api/medicamentos/m1/historico")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/medicamentos/m1/historico")).andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }
    @Test void registraUsoNaContaDaSessao() throws Exception {
        when(service.registrar("m1", "u1")).thenReturn(new RegistroUso("r1", Instant.parse("2026-10-29T12:00:00Z")));
        mvc.perform(post("/api/medicamentos/m1/historico").sessionAttr("usuarioId", "u1"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value("r1"));
    }
    @Test void lista() throws Exception {
        when(service.listar("m1", "u1")).thenReturn(List.of(new RegistroUso("r1", Instant.now())));
        mvc.perform(get("/api/medicamentos/m1/historico").sessionAttr("usuarioId", "u1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value("r1"));
    }
    @Test void retorna404ParaOutroUsuario() throws Exception {
        when(service.registrar("m1", "u2")).thenThrow(new MedicamentoNaoEncontradoException("m1"));
        mvc.perform(post("/api/medicamentos/m1/historico").sessionAttr("usuarioId", "u2"))
                .andExpect(status().isNotFound());
    }
}
