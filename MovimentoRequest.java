package medicare.dto.Medicamento;
import jakarta.validation.constraints.*;
public record MovimentoRequest(
    @NotNull(message = "Informe a quantidade")
    @Min(value = -100000, message = "A saída máxima é de 100.000 unidades")
    @Max(value = 100000, message = "A entrada máxima é de 100.000 unidades") Integer quantidade) { }
