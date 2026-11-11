package medicare.controller;

import medicare.dto.Medicamento.*;
import medicare.exception.*;
import medicare.service.MedicamentoService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicamentoController.class)
class MedicamentoControllerTest {

 @Autowired
 MockMvc mockMvc;

 @MockitoBean
 MedicamentoService service;

 @Test
 void semSessaoRetorna401() throws Exception {
  mockMvc.perform(
                  get("/api/medicamentos")
          )
          .andExpect(status().isUnauthorized());
 }

 @Test
 void cadastra() throws Exception {
  when(service.cadastrar(any(), eq("u1")))
          .thenReturn(resp("1"));

  mockMvc.perform(
                  post("/api/medicamentos")
                          .session(session())
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(json())
          )
          .andExpect(status().isCreated())
          .andExpect(
                  header().string(
                          "Location",
                          "/api/medicamentos/1"
                  )
          );
 }

 @Test
 void lista() throws Exception {
  when(service.listarTodos("u1"))
          .thenReturn(java.util.List.of(resp("1")));

  mockMvc.perform(
                  get("/api/medicamentos")
                          .session(session())
          )
          .andExpect(status().isOk())
          .andExpect(
                  jsonPath("$[0].nome")
                          .value("Losartana")
          );
 }

 @Test
 void busca() throws Exception {
  when(service.buscarPorId("1", "u1"))
          .thenReturn(resp("1"));

  mockMvc.perform(
                  get("/api/medicamentos/1")
                          .session(session())
          )
          .andExpect(status().isOk())
          .andExpect(
                  jsonPath("$.id")
                          .value("1")
          );
 }

 @Test
 void atualiza() throws Exception {
  when(service.atualizar(
          eq("1"),
          any(),
          eq("u1")
  ))
          .thenReturn(resp("1"));

  mockMvc.perform(
                  put("/api/medicamentos/1")
                          .session(session())
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(json())
          )
          .andExpect(status().isOk());
 }

 @Test
 void exclui() throws Exception {
  doNothing()
          .when(service)
          .excluir("1", "u1");

  mockMvc.perform(
                  delete("/api/medicamentos/1")
                          .session(session())
          )
          .andExpect(status().isNoContent());
 }

 @Test
 void validaCampos() throws Exception {
  mockMvc.perform(
                  post("/api/medicamentos")
                          .session(session())
                          .contentType(MediaType.APPLICATION_JSON)
                          .content("""
                                {
                                  "nome": "",
                                  "dosagem": "",
                                  "horario": "",
                                  "frequencia": "",
                                  "paciente": ""
                                }
                                """)
          )
          .andExpect(status().isBadRequest())
          .andExpect(
                  jsonPath("$.campos.nome")
                          .value("O nome é obrigatório")
          );
 }

 @Test
 void naoEncontrado() throws Exception {
  when(service.buscarPorId("9", "u1"))
          .thenThrow(
                  new MedicamentoNaoEncontradoException("9")
          );

  mockMvc.perform(
                  get("/api/medicamentos/9")
                          .session(session())
          )
          .andExpect(status().isNotFound());
 }

 @Test
 void rejeitaHorarioInvalido() throws Exception {
  mockMvc.perform(post("/api/medicamentos").session(session())
          .contentType(MediaType.APPLICATION_JSON).content(json().replace("08:00", "25:70")))
          .andExpect(status().isBadRequest()).andExpect(jsonPath("$.campos.horario").exists());
 }

 @Test
 void rejeitaJsonMalformado() throws Exception {
  mockMvc.perform(post("/api/medicamentos").session(session())
          .contentType(MediaType.APPLICATION_JSON).content("{"))
          .andExpect(status().isBadRequest());
 }

 private MockHttpSession session() {
  MockHttpSession s = new MockHttpSession();

  s.setAttribute("usuarioId", "u1");

  return s;
 }

 private MedicamentoResponse resp(String id) {
  return new MedicamentoResponse(
          id,
          "Losartana",
          "50 mg",
          "08:00",
          "Diária",
          "Ana",
          "Após o café"
  );
 }

 private String json() {
  return """
                {
                  "nome": "Losartana",
                  "dosagem": "50 mg",
                  "horario": "08:00",
                  "frequencia": "Diária",
                  "paciente": "Ana",
                  "observacao": "Após o café"
                }
                """;
 }
}