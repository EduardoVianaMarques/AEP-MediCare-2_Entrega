package medicare.service;

import medicare.dto.Medicamento.MedicamentoRequest;
import medicare.dto.Medicamento.MedicamentoResponse;
import medicare.exception.MedicamentoNaoEncontradoException;
import medicare.mapper.MedicamentoMapper;
import medicare.model.Medicamento;
import medicare.repository.MedicamentoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {
    private final MedicamentoRepository repository;
    private final MedicamentoMapper mapper;

    public MedicamentoService(
            MedicamentoRepository repository,
            MedicamentoMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public MedicamentoResponse cadastrar(
            MedicamentoRequest request,
            String usuarioId
    ) {
        validarUsuario(usuarioId);
        Medicamento medicamento = mapper.toEntity(request);
        medicamento.setUsuarioId(usuarioId);
        Medicamento medicamentoSalvo = repository.save(medicamento);
        return mapper.toResponse(medicamentoSalvo);
    }
    public List<MedicamentoResponse> listarTodos(String usuarioId) {
        validarUsuario(usuarioId);
        return repository
                .findByUsuarioId(usuarioId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
    public MedicamentoResponse buscarPorId(
            String id,
            String usuarioId
    ) {
        validarUsuario(usuarioId);
        Medicamento medicamento = repository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(
                        () -> new MedicamentoNaoEncontradoException(id)
                );
        return mapper.toResponse(medicamento);
    }
    public MedicamentoResponse atualizar(
            String id,
            MedicamentoRequest request,
            String usuarioId
    ) {
        validarUsuario(usuarioId);
        Medicamento medicamento = repository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(
                        () -> new MedicamentoNaoEncontradoException(id)
                );
        mapper.updateEntity(medicamento, request);
        repository.atualizarDados(id, usuarioId, request.nome(), request.dosagem(),
                request.horario(), request.frequencia(), request.paciente(), request.observacao());
        // Zero também significa atualização sem mudanças. A leitura final confirma a existência.
        return buscarPorId(id, usuarioId);
    }
    public void excluir(
            String id,
            String usuarioId
    ) {
        validarUsuario(usuarioId);
        Medicamento medicamento = repository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(
                        () -> new MedicamentoNaoEncontradoException(id)
                );
        repository.delete(medicamento);
    }

    private void validarUsuario(String usuarioId) {
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new IllegalArgumentException(
                    "Usuário não autenticado."
            );
        }
    }
}