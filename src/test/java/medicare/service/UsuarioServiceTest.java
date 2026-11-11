package medicare.service;

import medicare.dto.Usuario.CadastroRequest;
import medicare.model.Usuario;
import medicare.repository.UsuarioRepository;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

 @Mock
 UsuarioRepository repository;

 UsuarioService service;

 @BeforeEach
 void setup() {
  service = new UsuarioService(repository);
 }

 @Test
 void cadastrarSenhaComHash() {
  CadastroRequest r =
          req(" João ", " JOAO@EMAIL.COM ", "123456");

  when(repository.existsByEmail("joao@email.com"))
          .thenReturn(false);

  when(repository.save(any()))
          .thenAnswer(i -> i.getArgument(0));

  Usuario u = service.cadastrar(r);

  assertThat(u.getNome())
          .isEqualTo("João");

  assertThat(u.getEmail())
          .isEqualTo("joao@email.com");

  assertThat(u.getSenha()).startsWith("{pbkdf2}").isNotEqualTo("123456");
  assertThat(service.senhaValida("123456", u)).isTrue();
  assertThat(service.senhaValida("errada", u)).isFalse();
 }

 @Test
 void rejeitaEmailDuplicado() {
  CadastroRequest r =
          req("João", "a@a.com", "123456");

  when(repository.existsByEmail("a@a.com"))
          .thenReturn(true);

  assertThatThrownBy(
          () -> service.cadastrar(r)
  )
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Este email já está cadastrado.");

  verify(repository, never()).save(any());
 }

 @Test
 void buscaEmailNormalizado() {
  Usuario u =
          new Usuario("João", "a@a.com", "x");

  when(repository.findByEmail("a@a.com"))
          .thenReturn(Optional.of(u));

  assertThat(
          service.buscarPorEmail(" A@A.COM ")
  )
          .isSameAs(u);
 }

 @Test
 void buscaInexistente() {
  when(repository.findByEmail("a@a.com"))
          .thenReturn(Optional.empty());

  assertThatThrownBy(
          () -> service.buscarPorEmail("a@a.com")
  )
          .isInstanceOf(IllegalArgumentException.class);
 }

 @Test
 void senhaValida() {
  Usuario u =
          new Usuario("A", "a@a.com", "segredo");

  assertThat(
          service.senhaValida("segredo", u)
  ).isTrue();
  assertThat(u.getSenha()).startsWith("{pbkdf2}");
  verify(repository).save(u);

  assertThat(
          service.senhaValida("errada", u)
  ).isFalse();

  assertThat(
          service.senhaValida("x", null)
  ).isFalse();
 }

 @Test
 void naoMigraSenhaLegadaIncorreta() {
  Usuario u = new Usuario("Ana", "a@a.com", "legada");
  assertThat(service.senhaValida("errada", u)).isFalse();
  assertThat(u.getSenha()).isEqualTo("legada");
  verify(repository, never()).save(any());
 }

 @Test
 void rejeitaValoresNulos() {
  assertThat(service.senhaValida(null, new Usuario())).isFalse();
  assertThat(service.senhaValida("senha", new Usuario())).isFalse();
 }

 @Test
 void suportaSenhaUnicodeLongaSemTruncamento() {
  String senha = "á".repeat(60);
  when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
  Usuario u = service.cadastrar(req("Ana", "a@a.com", senha));
  assertThat(service.senhaValida(senha, u)).isTrue();
  assertThat(service.senhaValida("á".repeat(59) + "b", u)).isFalse();
 }

 private CadastroRequest req(
         String n,
         String e,
         String s
 ) {
  CadastroRequest r =
          new CadastroRequest();

  r.setNome(n);
  r.setEmail(e);
  r.setSenha(s);

  return r;
 }
}