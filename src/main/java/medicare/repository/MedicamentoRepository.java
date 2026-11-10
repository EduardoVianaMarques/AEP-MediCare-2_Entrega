package medicare.repository;

import medicare.model.Medicamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepository extends MongoRepository<Medicamento, String> {

    List<Medicamento> findByUsuarioId(String usuarioId);

    @org.springframework.data.mongodb.repository.Query("{ '_id': ?0, 'usuarioId': ?1 }")
    @org.springframework.data.mongodb.repository.Update("{ '$set': { 'nome': ?2, 'dosagem': ?3, 'horario': ?4, 'frequencia': ?5, 'paciente': ?6, 'observacao': ?7 } }")
    long atualizarDados(String id, String usuarioId, String nome, String dosagem,
                        String horario, String frequencia, String paciente, String observacao);

    Optional<Medicamento> findByIdAndUsuarioId(
            String id,
            String usuarioId
    );
}