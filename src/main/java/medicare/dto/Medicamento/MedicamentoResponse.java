package medicare.dto.Medicamento;

import medicare.model.Estoque;

public record MedicamentoResponse(String id, String nome, String dosagem, String horario,
        String frequencia, String paciente, String observacao, Estoque estoque) {
    // Mantém compatibilidade com consumidores da primeira versão do contrato Java.
    public MedicamentoResponse(String id, String nome, String dosagem, String horario,
            String frequencia, String paciente, String observacao) {
        this(id, nome, dosagem, horario, frequencia, paciente, observacao, null);
    }
}
