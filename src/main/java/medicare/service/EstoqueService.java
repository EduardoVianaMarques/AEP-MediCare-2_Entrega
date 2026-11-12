package medicare.service;

import medicare.dto.Medicamento.*;
import medicare.model.*;
import medicare.exception.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

@Service
public class EstoqueService {
    private final MongoTemplate mongo;
    public EstoqueService(MongoTemplate mongo) { this.mongo = mongo; }

    private Criteria proprietario(String id, String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank()) throw new IllegalArgumentException("Usuário não autenticado.");
        return Criteria.where("_id").is(id).and("usuarioId").is(usuarioId);
    }

    public Estoque iniciar(String id, String usuarioId, EstoqueRequest request) {
        var estoque = new Estoque(request.quantidade(), request.minimo());
        var query = Query.query(proprietario(id, usuarioId).and("estoque").is(null));
        var salvo = mongo.findAndModify(query, new Update().set("estoque", estoque),
                FindAndModifyOptions.options().returnNew(true), Medicamento.class);
        if (salvo == null) falha(id, usuarioId, "O estoque já foi iniciado. Atualize a página.");
        return salvo.getEstoque();
    }

    public Estoque movimentar(String id, String usuarioId, MovimentoRequest request) {
        int quantidade = request.quantidade();
        if (quantidade == 0) throw new IllegalArgumentException("Informe uma quantidade diferente de zero.");
        // O filtro e o incremento ocorrem na mesma operação, evitando saldo negativo em concorrência.
        var query = Query.query(proprietario(id, usuarioId).and("estoque.quantidade")
                .gte(Math.max(0, -quantidade)).lte(Math.min(100000, 100000 - quantidade)));
        var salvo = mongo.findAndModify(query, new Update().inc("estoque.quantidade", quantidade),
                FindAndModifyOptions.options().returnNew(true), Medicamento.class);
        if (salvo == null) falha(id, usuarioId,
                "Estoque não iniciado, saldo insuficiente ou limite de 100.000 unidades excedido.");
        return salvo.getEstoque();
    }

    private void falha(String id, String usuarioId, String mensagem) {
        if (!mongo.exists(Query.query(proprietario(id, usuarioId)), Medicamento.class))
            throw new MedicamentoNaoEncontradoException(id);
        throw new ConflitoEstoqueException(mensagem);
    }
}
