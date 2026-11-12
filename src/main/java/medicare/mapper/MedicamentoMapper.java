package medicare.mapper;

import medicare.dto.Medicamento.MedicamentoRequest;
import medicare.dto.Medicamento.MedicamentoResponse;
import medicare.model.Medicamento;
import org.springframework.stereotype.Component;

@Component
public class MedicamentoMapper {

    public Medicamento toEntity(MedicamentoRequest request) {
        return new Medicamento(
                null,
                request.nome(),
                request.dosagem(),
                request.horario(),
                request.frequencia(),
                request.paciente(),
                request.observacao()
        );
    }

    public MedicamentoResponse toResponse(Medicamento medicamento) {
        return new MedicamentoResponse(
                medicamento.getId(),
                medicamento.getNome(),
                medicamento.getDosagem(),
                medicamento.getHorario(),
                medicamento.getFrequencia(),
                medicamento.getPaciente(),
                medicamento.getObservacao(),
                medicamento.getEstoque()
        );
    }

    public void updateEntity(Medicamento medicamento, MedicamentoRequest request) {
        medicamento.setNome(request.nome());
        medicamento.setDosagem(request.dosagem());
        medicamento.setHorario(request.horario());
        medicamento.setFrequencia(request.frequencia());
        medicamento.setPaciente(request.paciente());
        medicamento.setObservacao(request.observacao());
    }
}
