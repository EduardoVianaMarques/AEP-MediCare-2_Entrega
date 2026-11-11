package medicare.exception;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ExceptionTest {

 @Test
 void exceptionMessage() {
  var e = new MedicamentoNaoEncontradoException("42");
  assertThat(e.getMessage()).isEqualTo("Medicamento não encontrado: 42");
 }

 @Test
 void apiError() {
  var now = Instant.now();
  var e = new ApiError(now, 400, "erro", "/x", Map.of("campo", "msg"));

  assertThat(e.timestamp()).isEqualTo(now);
  assertThat(e.status()).isEqualTo(400);
  assertThat(e.mensagem()).isEqualTo("erro");
  assertThat(e.caminho()).isEqualTo("/x");
  assertThat(e.campos()).containsEntry("campo", "msg");
 }
}
