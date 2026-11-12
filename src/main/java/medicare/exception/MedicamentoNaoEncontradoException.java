package medicare.exception;

public class MedicamentoNaoEncontradoException extends RuntimeException {

    public MedicamentoNaoEncontradoException(String id) {
        super("Medicamento não encontrado: " + id);
    }
}
