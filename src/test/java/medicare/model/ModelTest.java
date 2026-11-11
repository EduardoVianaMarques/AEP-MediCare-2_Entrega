package medicare.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ModelTest {

 @Test
 void deveCriarMedicamentoComTodosOsDados() {

  Medicamento medicamento = new Medicamento(
          "1",
          "Paracetamol",
          "500mg",
          "08:00",
          "Diária",
          "Ana",
          "Tomar após o café"
  );

  assertThat(medicamento.getId())
          .isEqualTo("1");

  assertThat(medicamento.getNome())
          .isEqualTo("Paracetamol");

  assertThat(medicamento.getDosagem())
          .isEqualTo("500mg");

  assertThat(medicamento.getHorario())
          .isEqualTo("08:00");

  assertThat(medicamento.getFrequencia())
          .isEqualTo("Diária");

  assertThat(medicamento.getPaciente())
          .isEqualTo("Ana");

  assertThat(medicamento.getObservacao())
          .isEqualTo("Tomar após o café");
 }


 @Test
 void deveAlterarDadosDoMedicamento() {

  Medicamento medicamento = new Medicamento(
          "1",
          "Paracetamol",
          "500mg",
          "08:00",
          "Diária",
          "Ana",
          "Observação inicial"
  );

  medicamento.setId("2");
  medicamento.setNome("Dipirona");
  medicamento.setDosagem("1g");
  medicamento.setHorario("12:00");
  medicamento.setFrequencia("Semanal");
  medicamento.setPaciente("Carlos");
  medicamento.setObservacao("Tomar após o almoço");

  assertThat(medicamento.getId())
          .isEqualTo("2");

  assertThat(medicamento.getNome())
          .isEqualTo("Dipirona");

  assertThat(medicamento.getDosagem())
          .isEqualTo("1g");

  assertThat(medicamento.getHorario())
          .isEqualTo("12:00");

  assertThat(medicamento.getFrequencia())
          .isEqualTo("Semanal");

  assertThat(medicamento.getPaciente())
          .isEqualTo("Carlos");

  assertThat(medicamento.getObservacao())
          .isEqualTo("Tomar após o almoço");
 }


 @Test
 void deveDefinirUsuarioEDataDeCadastroDoMedicamento() {

  Medicamento medicamento = new Medicamento(
          "1",
          "Paracetamol",
          "500mg",
          "08:00",
          "Diária",
          "Ana",
          "Observação"
  );

  LocalDateTime dataCadastro = LocalDateTime.now();

  medicamento.setUsuarioId("usuario-123");
  medicamento.setDataCadastro(dataCadastro);

  assertThat(medicamento.getUsuarioId())
          .isEqualTo("usuario-123");

  assertThat(medicamento.getDataCadastro())
          .isNotNull();

  assertThat(medicamento.getDataCadastro())
          .isEqualTo(dataCadastro);
 }


 @Test
 void devePermitirCriarMedicamentoVazio() {

  Medicamento medicamento = new Medicamento();

  assertThat(medicamento).isNotNull();

  assertThat(medicamento.getId()).isNull();
  assertThat(medicamento.getNome()).isNull();
  assertThat(medicamento.getDosagem()).isNull();
  assertThat(medicamento.getHorario()).isNull();
  assertThat(medicamento.getFrequencia()).isNull();
  assertThat(medicamento.getPaciente()).isNull();
  assertThat(medicamento.getObservacao()).isNull();
 }


 @Test
 void deveCriarUsuarioComTodosOsDados() {

  Usuario usuario = new Usuario(
          "Ana",
          "ana@email.com",
          "senhaHash"
  );

  assertThat(usuario.getNome())
          .isEqualTo("Ana");

  assertThat(usuario.getEmail())
          .isEqualTo("ana@email.com");

  assertThat(usuario.getSenha())
          .isEqualTo("senhaHash");
 }


 @Test
 void deveAlterarDadosDoUsuario() {

  Usuario usuario = new Usuario(
          "Ana",
          "ana@email.com",
          "senhaHash"
  );

  usuario.setId("usuario-1");
  usuario.setNome("Carlos");
  usuario.setEmail("carlos@email.com");
  usuario.setSenha("novaSenha");

  assertThat(usuario.getId())
          .isEqualTo("usuario-1");

  assertThat(usuario.getNome())
          .isEqualTo("Carlos");

  assertThat(usuario.getEmail())
          .isEqualTo("carlos@email.com");

  assertThat(usuario.getSenha())
          .isEqualTo("novaSenha");
 }


 @Test
 void deveDefinirDataDeCadastroDoUsuario() {

  Usuario usuario = new Usuario(
          "Ana",
          "ana@email.com",
          "senhaHash"
  );

  LocalDateTime dataCadastro = LocalDateTime.now();

  usuario.setDataCadastro(dataCadastro);

  assertThat(usuario.getDataCadastro())
          .isNotNull();

  assertThat(usuario.getDataCadastro())
          .isEqualTo(dataCadastro);
 }


 @Test
 void usuarioNovoDevePossuirIdNulo() {

  Usuario usuario = new Usuario();

  assertThat(usuario)
          .isNotNull();

  assertThat(usuario.getId())
          .isNull();
 }


 @Test
 void usuarioDevePermitirAlteracaoCompletaDosDados() {

  Usuario usuario = new Usuario(
          "Ana",
          "ana@email.com",
          "senhaInicial"
  );

  LocalDateTime dataCadastro = LocalDateTime.now();

  usuario.setId("u100");
  usuario.setNome("Maria");
  usuario.setEmail("maria@email.com");
  usuario.setSenha("senhaNova");
  usuario.setDataCadastro(dataCadastro);

  assertThat(usuario.getId())
          .isEqualTo("u100");

  assertThat(usuario.getNome())
          .isEqualTo("Maria");

  assertThat(usuario.getEmail())
          .isEqualTo("maria@email.com");

  assertThat(usuario.getSenha())
          .isEqualTo("senhaNova");

  assertThat(usuario.getDataCadastro())
          .isEqualTo(dataCadastro);
 }
}