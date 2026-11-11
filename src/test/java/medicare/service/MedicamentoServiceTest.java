package medicare.service;

import medicare.dto.Medicamento.MedicamentoRequest;
import medicare.exception.MedicamentoNaoEncontradoException;
import medicare.mapper.MedicamentoMapper;
import medicare.model.Medicamento;
import medicare.repository.MedicamentoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest {

 @Mock
 MedicamentoRepository repository;

 MedicamentoService service;
 MedicamentoRequest request;

 @BeforeEach
 void setUp() {
  service = new MedicamentoService(
          repository,
          new MedicamentoMapper()
  );

  request = new MedicamentoRequest(
          "Losartana",
          "50 mg",
          "08:00",
          "Diária",
          "Ana",
          "Após o café"
  );
 }

 @Test
 void cadastrar() {
  when(repository.save(any())).thenAnswer(i -> {
   Medicamento m = i.getArgument(0);
   m.setId("1");
   return m;
  });

  var r = service.cadastrar(request, "u1");

  assertThat(r.id()).isEqualTo("1");
  assertThat(r.nome()).isEqualTo("Losartana");

  verify(repository).save(
          argThat(m -> "u1".equals(m.getUsuarioId()))
  );
 }

 @Test
 void listar() {
  when(repository.findByUsuarioId("u1"))
          .thenReturn(
                  List.of(
                          med("1", "Losartana"),
                          med("2", "Metformina")
                  )
          );

  assertThat(service.listarTodos("u1"))
          .extracting("nome")
          .containsExactly(
                  "Losartana",
                  "Metformina"
          );

  verify(repository).findByUsuarioId("u1");
 }

 @Test
 void buscar() {
  when(repository.findByIdAndUsuarioId("1", "u1"))
          .thenReturn(
                  Optional.of(
                          med("1", "Losartana")
                  )
          );

  assertThat(
          service.buscarPorId("1", "u1").dosagem()
  ).isEqualTo("50 mg");

  verify(repository)
          .findByIdAndUsuarioId("1", "u1");
 }

 @Test
 void buscarInexistente() {
  when(repository.findByIdAndUsuarioId("99", "u1"))
          .thenReturn(Optional.empty());

  assertThatThrownBy(
          () -> service.buscarPorId("99", "u1")
  )
          .isInstanceOf(
                  MedicamentoNaoEncontradoException.class
          );

  verify(repository)
          .findByIdAndUsuarioId("99", "u1");
 }

 @Test
 void atualizar() {
  var m = med("1", "Antigo");

  when(repository.findByIdAndUsuarioId("1", "u1"))
          .thenReturn(Optional.of(m));



  assertThat(
          service.atualizar(
                  "1",
                  request,
                  "u1"
          ).nome()
  ).isEqualTo("Losartana");

  verify(repository, times(2)).findByIdAndUsuarioId("1", "u1");
  verify(repository).atualizarDados("1", "u1", "Losartana", "50 mg", "08:00",
          "Diária", "Ana", "Após o café");
  verify(repository, never()).save(any());
 }

 @Test
 void atualizarInexistente() {
  when(repository.findByIdAndUsuarioId("99", "u1"))
          .thenReturn(Optional.empty());

  assertThatThrownBy(
          () -> service.atualizar(
                  "99",
                  request,
                  "u1"
          )
  )
          .isInstanceOf(
                  MedicamentoNaoEncontradoException.class
          );

  verify(repository)
          .findByIdAndUsuarioId("99", "u1");

  verify(repository, never())
          .save(any());
 }

 @Test
 void excluir() {
  Medicamento medicamento =
          med("1", "A");

  when(repository.findByIdAndUsuarioId("1", "u1"))
          .thenReturn(
                  Optional.of(medicamento)
          );

  service.excluir("1", "u1");

  verify(repository)
          .findByIdAndUsuarioId("1", "u1");

  verify(repository)
          .delete(medicamento);
 }

 @Test
 void excluirInexistente() {
  when(repository.findByIdAndUsuarioId("99", "u1"))
          .thenReturn(Optional.empty());

  assertThatThrownBy(
          () -> service.excluir("99", "u1")
  )
          .isInstanceOf(
                  MedicamentoNaoEncontradoException.class
          );

  verify(repository)
          .findByIdAndUsuarioId("99", "u1");

  verify(repository, never())
          .delete(any(Medicamento.class));
 }

 @Test
 void exigeUsuario() {
  assertThatThrownBy(
          () -> service.listarTodos(null)
  )
          .isInstanceOf(
                  IllegalArgumentException.class
          );

  assertThatThrownBy(
          () -> service.listarTodos(" ")
  )
          .isInstanceOf(
                  IllegalArgumentException.class
          );
 }

 @Test
 void naoDeveBuscarMedicamentoDeOutroUsuario() {
  when(repository.findByIdAndUsuarioId("1", "u2"))
          .thenReturn(Optional.empty());

  assertThatThrownBy(
          () -> service.buscarPorId(
                  "1",
                  "u2"
          )
  )
          .isInstanceOf(
                  MedicamentoNaoEncontradoException.class
          );

  verify(repository)
          .findByIdAndUsuarioId("1", "u2");
 }

 @Test
 void naoDeveAtualizarMedicamentoDeOutroUsuario() {
  when(repository.findByIdAndUsuarioId("1", "u2"))
          .thenReturn(Optional.empty());

  assertThatThrownBy(
          () -> service.atualizar(
                  "1",
                  request,
                  "u2"
          )
  )
          .isInstanceOf(
                  MedicamentoNaoEncontradoException.class
          );

  verify(repository)
          .findByIdAndUsuarioId("1", "u2");

  verify(repository, never())
          .save(any());
 }

 @Test
 void naoDeveExcluirMedicamentoDeOutroUsuario() {
  when(repository.findByIdAndUsuarioId("1", "u2"))
          .thenReturn(Optional.empty());

  assertThatThrownBy(
          () -> service.excluir(
                  "1",
                  "u2"
          )
  )
          .isInstanceOf(
                  MedicamentoNaoEncontradoException.class
          );

  verify(repository)
          .findByIdAndUsuarioId("1", "u2");

  verify(repository, never())
          .delete(any(Medicamento.class));
 }

 private Medicamento med(
         String id,
         String nome
 ) {
  return new Medicamento(
          id,
          nome,
          "50 mg",
          "08:00",
          "Diária",
          "Ana",
          "Após o café"
  );
 }
}