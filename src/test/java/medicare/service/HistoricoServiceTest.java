package medicare.service;

import medicare.model.*;
import medicare.exception.MedicamentoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import com.mongodb.client.result.UpdateResult;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class HistoricoServiceTest {
    @Mock MongoTemplate mongo;
    @InjectMocks HistoricoService service;

    @Test void registraAtomicamenteComFiltroDoProprietarioELimite() {
        when(mongo.updateFirst(any(Query.class), any(Update.class), eq(Medicamento.class)))
                .thenReturn(UpdateResult.acknowledged(1, 1L, null));
        var registro = service.registrar("m1", "u1");
        assertThat(registro.id()).isNotBlank();
        assertThat(registro.registradoEm()).isBeforeOrEqualTo(Instant.now());
        var query = ArgumentCaptor.forClass(Query.class);
        var update = ArgumentCaptor.forClass(Update.class);
        verify(mongo).updateFirst(query.capture(), update.capture(), eq(Medicamento.class));
        assertThat(query.getValue().getQueryObject()).containsEntry("_id", "m1")
                .containsEntry("usuarioId", "u1");
        assertThat(update.getValue().modifies("historico")).isTrue();
        assertThat(update.getValue().getUpdateObject()).containsKey("$push");
        // O limite e a conversão BSON são verificados com MongoDB real no teste de integração.
    }

    @Test void naoRegistraEmMedicamentoAusenteOuDeOutraConta() {
        when(mongo.updateFirst(any(Query.class), any(Update.class), eq(Medicamento.class)))
                .thenReturn(UpdateResult.acknowledged(0, 0L, null));
        assertThatThrownBy(() -> service.registrar("m1", "u2"))
                .isInstanceOf(MedicamentoNaoEncontradoException.class);
    }

    @Test void listaHistoricoESuportaDocumentoAntigo() {
        var medicamento = new Medicamento();
        when(mongo.findOne(any(Query.class), eq(Medicamento.class))).thenReturn(medicamento);
        assertThat(service.listar("m1", "u1")).isEmpty();
        var registro = new RegistroUso("r1", Instant.now());
        medicamento.setHistorico(List.of(registro));
        assertThat(service.listar("m1", "u1")).containsExactly(registro);
        medicamento.setHistorico(null);
        assertThat(service.listar("m1", "u1")).isEmpty();
        verify(mongo, times(3)).findOne(argThat(q -> "u1".equals(q.getQueryObject().get("usuarioId"))),
                eq(Medicamento.class));
    }

    @Test void naoListaMedicamentoDeOutraConta() {
        assertThatThrownBy(() -> service.listar("m1", "u2"))
                .isInstanceOf(MedicamentoNaoEncontradoException.class);
    }

    @Test void exigeUsuarioAntesDeAcessarBanco() {
        assertThatThrownBy(() -> service.listar("m1", null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.registrar("m1", " ")).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(mongo);
    }
}
