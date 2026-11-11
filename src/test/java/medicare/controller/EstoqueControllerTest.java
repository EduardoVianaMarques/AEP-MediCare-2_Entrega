package medicare.controller;

import medicare.service.EstoqueService;
import medicare.model.Estoque;
import medicare.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EstoqueController.class)
class EstoqueControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean EstoqueService service;
    private final String url = "/api/medicamentos/m1/estoque";

    @Test void exigeSessao() throws Exception {
        mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content("{\"quantidade\":10,\"minimo\":5}"))
            .andExpect(status().isUnauthorized());
        mvc.perform(post(url + "/movimentos").contentType(MediaType.APPLICATION_JSON).content("{\"quantidade\":1}"))
            .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }
    @Test void inicia() throws Exception {
        when(service.iniciar(eq("m1"), eq("u1"), any())).thenReturn(new Estoque(10, 5));
        mvc.perform(post(url).sessionAttr("usuarioId", "u1").contentType(MediaType.APPLICATION_JSON)
            .content("{\"quantidade\":10,\"minimo\":5}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.quantidade").value(10));
    }
    @Test void movimenta() throws Exception {
        when(service.movimentar(eq("m1"), eq("u1"), any())).thenReturn(new Estoque(9, 5));
        mvc.perform(post(url + "/movimentos").sessionAttr("usuarioId", "u1").contentType(MediaType.APPLICATION_JSON)
            .content("{\"quantidade\":-1}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.quantidade").value(9));
    }
    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"quantidade\":-1,\"minimo\":5}", "{\"quantidade\":100001,\"minimo\":5}", "{\"quantidade\":2,\"minimo\":-1}"})
    void validaEntrada(String json) throws Exception {
        mvc.perform(post(url).sessionAttr("usuarioId", "u1").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    @Test void rejeitaQuantidadeFracionada() throws Exception {
        mvc.perform(post(url).sessionAttr("usuarioId", "u1").contentType(MediaType.APPLICATION_JSON)
            .content("{\"quantidade\":1.5,\"minimo\":0}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    @Test void informaConflito() throws Exception {
        when(service.movimentar(anyString(), anyString(), any())).thenThrow(new ConflitoEstoqueException("Saldo insuficiente"));
        mvc.perform(post(url + "/movimentos").sessionAttr("usuarioId", "u1").contentType(MediaType.APPLICATION_JSON)
            .content("{\"quantidade\":-10}"))
            .andExpect(status().isConflict()).andExpect(jsonPath("$.mensagem").value("Saldo insuficiente"));
    }
    @Test void escondeRegistroDeOutraConta() throws Exception {
        when(service.iniciar(anyString(), anyString(), any())).thenThrow(new MedicamentoNaoEncontradoException("m1"));
        mvc.perform(post(url).sessionAttr("usuarioId", "u2").contentType(MediaType.APPLICATION_JSON)
            .content("{\"quantidade\":10,\"minimo\":5}"))
            .andExpect(status().isNotFound());
    }
}
