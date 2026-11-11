package medicare.mapper;

import medicare.dto.Medicamento.*;
import medicare.model.Medicamento;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MedicamentoMapperTest {

 private final MedicamentoMapper mapper = new MedicamentoMapper();


 @Test
 void deveMapearMedicamentoRequestParaEntity() {

  MedicamentoRequest request = new MedicamentoRequest(
          "Paracetamol",
          "500mg",
          "08:00",
          "Diária",
          "Ana",
          "Tomar após o café"
  );

  Medicamento medicamento = mapper.toEntity(request);

  assertThat(medicamento)
          .isNotNull();

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

  assertThat(medicamento.getDataCadastro())
          .isNotNull();
 }


 @Test
 void deveMapearMedicamentoEntityParaResponse() {

  Medicamento medicamento = new Medicamento(
          "med-1",
          "Paracetamol",
          "500mg",
          "08:00",
          "Diária",
          "Ana",
          "Tomar após o café"
  );

  MedicamentoResponse response =
          mapper.toResponse(medicamento);

  assertThat(response)
          .isNotNull();

  assertThat(response.id())
          .isEqualTo("med-1");

  assertThat(response.nome())
          .isEqualTo("Paracetamol");

  assertThat(response.dosagem())
          .isEqualTo("500mg");

  assertThat(response.horario())
          .isEqualTo("08:00");

  assertThat(response.frequencia())
          .isEqualTo("Diária");

  assertThat(response.paciente())
          .isEqualTo("Ana");

  assertThat(response.observacao())
          .isEqualTo("Tomar após o café");
 }


 @Test
 void deveAtualizarTodosOsDadosDaEntity() {

  Medicamento medicamento = new Medicamento(
          "med-1",
          "Paracetamol",
          "500mg",
          "08:00",
          "Diária",
          "Ana",
          "Observação antiga"
  );

  MedicamentoRequest request = new MedicamentoRequest(
          "Dipirona",
          "1g",
          "12:00",
          "Semanal",
          "Carlos",
          "Tomar após o almoço"
  );

  mapper.updateEntity(medicamento, request);

  assertThat(medicamento.getId())
          .isEqualTo("med-1");

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
 void devePreservarIdAoAtualizarEntity() {

  Medicamento medicamento = new Medicamento(
          "med-123",
          "Paracetamol",
          "500mg",
          "08:00",
          "Diária",
          "Ana",
          "Observação"
  );

  MedicamentoRequest request = new MedicamentoRequest(
          "Dipirona",
          "1g",
          "12:00",
          "Diária",
          "Ana",
          "Nova observação"
  );

  mapper.updateEntity(medicamento, request);

  assertThat(medicamento.getId())
          .isEqualTo("med-123");

  assertThat(medicamento.getNome())
          .isEqualTo("Dipirona");
 }


 @Test
 void devePreservarDataDeCadastroAoAtualizarEntity() {

  Medicamento medicamento = new Medicamento(
          "med-1",
          "Paracetamol",
          "500mg",
          "08:00",
          "Diária",
          "Ana",
          "Observação"
  );

  var dataOriginal = medicamento.getDataCadastro();

  MedicamentoRequest request = new MedicamentoRequest(
          "Dipirona",
          "1g",
          "12:00",
          "Diária",
          "Ana",
          "Nova observação"
  );

  mapper.updateEntity(medicamento, request);

  assertThat(medicamento.getDataCadastro())
          .isEqualTo(dataOriginal);
 }
}