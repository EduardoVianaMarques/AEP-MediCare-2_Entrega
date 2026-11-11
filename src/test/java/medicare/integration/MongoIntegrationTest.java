package medicare.integration;

import medicare.dto.Medicamento.MedicamentoRequest;
import medicare.dto.Usuario.CadastroRequest;
import medicare.exception.MedicamentoNaoEncontradoException;
import medicare.model.*;
import medicare.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

/** Usa uma base temporária própria. Nunca aponta para a base medicare. */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "MONGODB_INTEGRATION", matches = "true")
class MongoIntegrationTest {
    private static final String DATABASE = "medicare_test_" + UUID.randomUUID().toString().replace("-", "");
    @DynamicPropertySource
    static void banco(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/" + DATABASE);
    }
    @Autowired MongoTemplate mongo;
    @Autowired MedicamentoService medicamentos;
    @Autowired HistoricoService historico;
    @Autowired UsuarioService usuarios;
    @Autowired EstoqueService estoques;

    @AfterEach void limparSomenteBaseTemporaria() { mongo.getDb().drop(); }

    @Test void estoquePersisteESaidasConcorrentesNaoGeramSaldoNegativo() throws Exception {
        var req = new MedicamentoRequest("Exemplo", "Conforme receita", "08:00", "Diária", "Teste", null);
        var m = medicamentos.cadastrar(req, "u1");
        estoques.iniciar(m.id(), "u1", new medicare.dto.Medicamento.EstoqueRequest(1, 0));
        assertThatThrownBy(() -> estoques.iniciar(m.id(), "u1", new medicare.dto.Medicamento.EstoqueRequest(10, 0)))
            .isInstanceOf(medicare.exception.ConflitoEstoqueException.class);
        var pool = java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            java.util.concurrent.Callable<Boolean> retirada = () -> {
                try { estoques.movimentar(m.id(), "u1", new medicare.dto.Medicamento.MovimentoRequest(-1)); return true; }
                catch (medicare.exception.ConflitoEstoqueException e) { return false; }
            };
            var resultados = pool.invokeAll(java.util.List.of(retirada, retirada));
            int sucessos = 0;
            for (var resultado : resultados) if (resultado.get()) sucessos++;
            assertThat(sucessos).isEqualTo(1);
        } finally { pool.shutdownNow(); }
        assertThat(medicamentos.buscarPorId(m.id(), "u1").estoque().quantidade()).isZero();
        estoques.movimentar(m.id(), "u1", new medicare.dto.Medicamento.MovimentoRequest(100000));
        assertThatThrownBy(() -> estoques.movimentar(m.id(), "u1", new medicare.dto.Medicamento.MovimentoRequest(1)))
            .isInstanceOf(medicare.exception.ConflitoEstoqueException.class);
        assertThatThrownBy(() -> estoques.movimentar(m.id(), "u2", new medicare.dto.Medicamento.MovimentoRequest(-1)))
            .isInstanceOf(MedicamentoNaoEncontradoException.class);
        medicamentos.atualizar(m.id(), req, "u1");
        assertThat(medicamentos.buscarPorId(m.id(), "u1").estoque().quantidade()).isEqualTo(100000);
        historico.registrar(m.id(), "u1");
        assertThat(medicamentos.buscarPorId(m.id(), "u1").estoque().quantidade()).isEqualTo(100000);
    }

    @Test void persisteRelacionamentoHistoricoLimiteEEdicaoParcial() {
        mongo.indexOps(Usuario.class).ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
        var cadastro = new CadastroRequest();
        cadastro.setNome("Conta teste");
        cadastro.setEmail("teste@example.com");
        cadastro.setSenha("senha-teste");
        var usuario = usuarios.cadastrar(cadastro);
        assertThat(usuarios.senhaValida("senha-teste", usuarios.buscarPorEmail("teste@example.com"))).isTrue();
        assertThatThrownBy(() -> usuarios.cadastrar(cadastro)).isInstanceOf(IllegalArgumentException.class);

        var request = new MedicamentoRequest("Exemplo", "Conforme receita", "08:00", "Diária", "Teste", null);
        var medicamento = medicamentos.cadastrar(request, usuario.getId());
        for (int i = 0; i < 101; i++) historico.registrar(medicamento.id(), usuario.getId());
        assertThat(historico.listar(medicamento.id(), usuario.getId())).hasSize(100);
        assertThat(mongo.findById(medicamento.id(), Medicamento.class).getUsuarioId()).isEqualTo(usuario.getId());

        var alteracao = new MedicamentoRequest("Novo nome", "Conforme receita", "09:00", "Diária", "Teste", null);
        assertThat(medicamentos.atualizar(medicamento.id(), alteracao, usuario.getId()).nome()).isEqualTo("Novo nome");
        assertThat(historico.listar(medicamento.id(), usuario.getId())).hasSize(100);
        assertThatThrownBy(() -> historico.registrar(medicamento.id(), "outra-conta"))
                .isInstanceOf(MedicamentoNaoEncontradoException.class);
        assertThat(medicamentos.listarTodos("outra-conta")).isEmpty();
        medicamentos.excluir(medicamento.id(), usuario.getId());
        assertThatThrownBy(() -> historico.listar(medicamento.id(), usuario.getId()))
                .isInstanceOf(MedicamentoNaoEncontradoException.class);
    }
}
