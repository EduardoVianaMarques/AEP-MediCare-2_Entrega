package medicare.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaginaController.class)
class PaginaControllerTest {

 @Autowired
 private MockMvc mvc;

 @Test
 void inicio() {
  perform("/", "index.html");
 }

 @Test
 void login() {
  perform("/login", "login.html");
 }

 @Test
 void cadastro() {
  perform("/cadastro", "cadastro.html");
 }

 @Test
 void medicamentos() {
  perform("/medicamentos", "medicamentos.html");
 }

 private void perform(String path, String target) {
  try {
   mvc.perform(get(path))
           .andExpect(status().isOk())
           .andExpect(forwardedUrl("/" + target));
  } catch (Exception e) {
   throw new AssertionError(e);
  }
 }
}