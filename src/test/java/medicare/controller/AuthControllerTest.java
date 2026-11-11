package medicare.controller;

import medicare.dto.Usuario.*;
import medicare.model.Usuario;
import medicare.service.UsuarioService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

 @Autowired
 MockMvc mvc;

 @MockitoBean
 UsuarioService service;

 @Test
 void cadastroOk() throws Exception {
  when(service.cadastrar(any()))
          .thenReturn(new Usuario("Ana", "ana@email.com", "x"));

  mvc.perform(
                  post("/api/auth/cadastro")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content("""
                                {
                                  "nome": "Ana",
                                  "email": "ana@email.com",
                                  "senha": "123456"
                                }
                                """)
          )
          .andExpect(status().isOk());
 }

 @Test
 void cadastroDuplicado() throws Exception {
  when(service.cadastrar(any()))
          .thenThrow(new IllegalArgumentException(
                  "Este email já está cadastrado."
          ));

  mvc.perform(
                  post("/api/auth/cadastro")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content("""
                                {
                                  "nome": "Ana",
                                  "email": "ana@email.com",
                                  "senha": "123456"
                                }
                                """)
          )
          .andExpect(status().isBadRequest());
 }

 @Test
 void loginOk() throws Exception {
  Usuario u = new Usuario("Ana", "ana@email.com", "hash");
  u.setId("u1");

  when(service.buscarPorEmail("ana@email.com"))
          .thenReturn(u);

  when(service.senhaValida("123456", u))
          .thenReturn(true);

  mvc.perform(
                  post("/api/auth/login")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content("""
                                {
                                  "email": "ana@email.com",
                                  "senha": "123456"
                                }
                                """)
          )
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.nome").value("Ana"));
 }

 @Test
 void loginSenhaInvalida() throws Exception {
  Usuario u = new Usuario("Ana", "ana@email.com", "hash");

  when(service.buscarPorEmail("ana@email.com"))
          .thenReturn(u);

  when(service.senhaValida(any(), eq(u)))
          .thenReturn(false);

  mvc.perform(
                  post("/api/auth/login")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content("""
                                {
                                  "email": "ana@email.com",
                                  "senha": "errada"
                                }
                                """)
          )
          .andExpect(status().isUnauthorized());
 }

 @Test
 void loginUsuarioInexistente() throws Exception {
  when(service.buscarPorEmail(any()))
          .thenThrow(new IllegalArgumentException(
                  "Usuário não encontrado."
          ));

  mvc.perform(
                  post("/api/auth/login")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content("""
                                {
                                  "email": "x@email.com",
                                  "senha": "123456"
                                }
                                """)
          )
          .andExpect(status().isUnauthorized());
 }

 @Test
 void usuarioSemSessao() throws Exception {
  mvc.perform(get("/api/auth/usuario"))
          .andExpect(status().isUnauthorized());
 }

 @Test
 void usuarioComSessao() throws Exception {
  MockHttpSession s = new MockHttpSession();

  s.setAttribute("usuarioId", "u1");
  s.setAttribute("usuarioNome", "Ana");
  s.setAttribute("usuarioEmail", "a@a.com");

  mvc.perform(
                  get("/api/auth/usuario")
                          .session(s)
          )
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value("u1"));
 }

 @Test
 void logout() throws Exception {
  MockHttpSession s = new MockHttpSession();

  s.setAttribute("usuarioId", "u1");

  mvc.perform(
                  post("/api/auth/logout")
                          .session(s)
          )
          .andExpect(status().isNoContent());

  org.junit.jupiter.api.Assertions.assertThrows(
          IllegalStateException.class,
          () -> s.getAttribute("usuarioId")
  );
 }
}