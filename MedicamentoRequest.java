package medicare.dto.Medicamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedicamentoRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "A dosagem é obrigatória")
        String dosagem,

        @NotBlank(message = "O horário é obrigatório")
        @jakarta.validation.constraints.Pattern(regexp = "([01][0-9]|2[0-3]):[0-5][0-9]",
                message = "Informe um horário válido no formato HH:mm")
        String horario,

        @NotBlank(message = "A frequência é obrigatória")
        String frequencia,

        @NotBlank(message = "O paciente é obrigatório")
        String paciente,

        @Size(max = 300, message = "A observação deve ter no máximo 300 caracteres")
        String observacao
) {
}
