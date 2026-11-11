package medicare.service;

import medicare.dto.Medicamento.*;
import medicare.model.*;
import medicare.exception.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {
    @Mock MongoTemplate mongo;
    @InjectMocks EstoqueService service;

    @Test void iniciaSemSobrescreverSaldoExistente() {
        var m = new Medicamento(); m.setEstoque(new Estoque(20, 5));
        when(mongo.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Medicamento.class))).thenReturn(m);
        assertThat(service.iniciar("m1", "u1", new EstoqueRequest(20, 5))).isEqualTo(new Estoque(20, 5));
        var query = ArgumentCaptor.forClass(Query.class);
        verify(mongo).findAndModify(query.capture(), any(Update.class), any(FindAndModifyOptions.class), eq(Medicamento.class));
        assertThat(query.getValue().getQueryObject()).containsEntry("_id", "m1")
            .containsEntry("usuarioId", "u1").containsEntry("estoque", null);
    }
    @Test void movimentaSaldoComFiltroDoProprietario() {
        var m = new Medicamento(); m.setEstoque(new Estoque(3, 5));
        when(mongo.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Medicamento.class))).thenReturn(m);
        assertThat(service.movimentar("m1", "u1", new MovimentoRequest(-2)).quantidade()).isEqualTo(3);
        verify(mongo).findAndModify(argThat(q -> "u1".equals(q.getQueryObject().get("usuarioId"))),
                argThat(u -> u.modifies("estoque.quantidade")), any(FindAndModifyOptions.class), eq(Medicamento.class));
    }
    @Test void retornaConflitoParaSaldoInsuficiente() {
        when(mongo.exists(any(Query.class), eq(Medicamento.class))).thenReturn(true);
        assertThatThrownBy(() -> service.movimentar("m1", "u1", new MovimentoRequest(-10)))
                .isInstanceOf(ConflitoEstoqueException.class);
    }
    @Test void naoReiniciaEstoqueExistente() {
        when(mongo.exists(any(Query.class), eq(Medicamento.class))).thenReturn(true);
        assertThatThrownBy(() -> service.iniciar("m1", "u1", new EstoqueRequest(1, 0)))
                .isInstanceOf(ConflitoEstoqueException.class);
    }
    @Test void naoExpoeMedicamentoDeOutraConta() {
        assertThatThrownBy(() -> service.movimentar("m1", "u2", new MovimentoRequest(10)))
                .isInstanceOf(MedicamentoNaoEncontradoException.class);
    }
    @Test void recusaMovimentoZero() {
        assertThatThrownBy(() -> service.movimentar("m1", "u1", new MovimentoRequest(0)))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(mongo);
    }
    @Test void exigeUsuario() {
        assertThatThrownBy(() -> service.iniciar("m1", null, new EstoqueRequest(1, 0))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.movimentar("m1", " ", new MovimentoRequest(1))).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(mongo);
    }
}
