package medicare.dto.Medicamento;
import jakarta.validation.constraints.*;
public record EstoqueRequest(
    @NotNull(message = "Informe o saldo inicial")
    @Min(value = 0, message = "O saldo não pode ser negativo")
    @Max(value = 100000, message = "O limite é de 100.000 unidades") Integer quantidade,
    @NotNull(message = "Informe o mínimo para alerta")
    @Min(value = 0, message = "O mínimo não pode ser negativo")
    @Max(value = 100000, message = "O limite é de 100.000 unidades") Integer minimo) { }
