package medicare.service;

import medicare.exception.MedicamentoNaoEncontradoException;
import medicare.model.Medicamento;
import medicare.model.RegistroUso;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class HistoricoService {
    private final MongoTemplate mongo;

    public HistoricoService(MongoTemplate mongo) { this.mongo = mongo; }

    private Query doUsuario(String id, String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank())
            throw new IllegalArgumentException("Usuário não autenticado.");
        return Query.query(Criteria.where("_id").is(id).and("usuarioId").is(usuarioId));
    }

    public List<RegistroUso> listar(String id, String usuarioId) {
        Medicamento medicamento = mongo.findOne(doUsuario(id, usuarioId), Medicamento.class);
        if (medicamento == null) throw new MedicamentoNaoEncontradoException(id);
        return medicamento.getHistorico();
    }

    public RegistroUso registrar(String id, String usuarioId) {
        RegistroUso registro = new RegistroUso(UUID.randomUUID().toString(), Instant.now());
        // $push evita perder registros concorrentes; limita o documento aos 100 últimos usos.
        Update update = new Update().push("historico").slice(-100).each(registro);
        var resultado = mongo.updateFirst(doUsuario(id, usuarioId), update, Medicamento.class);
        if (resultado.getMatchedCount() == 0) throw new MedicamentoNaoEncontradoException(id);
        return registro;
    }
}
