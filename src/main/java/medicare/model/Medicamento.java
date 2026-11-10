package medicare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "medicamentos")
public class Medicamento {

    @Id
    private String id;

    private Estoque estoque;
    public Estoque getEstoque() { return estoque; }
    public void setEstoque(Estoque estoque) { this.estoque = estoque; }

    private String nome;
    private String dosagem;
    private String horario;
    private String frequencia;
    private String paciente;
    private String observacao;
    @org.springframework.data.mongodb.core.index.Indexed
    private String usuarioId;
    private java.util.List<RegistroUso> historico = new java.util.ArrayList<>();

    public java.util.List<RegistroUso> getHistorico() {
        return historico == null ? java.util.List.of() : java.util.List.copyOf(historico);
    }

    public void setHistorico(java.util.List<RegistroUso> historico) {
        this.historico = historico == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(historico);
    }
    private LocalDateTime dataCadastro;

    public Medicamento() {
    }

    public Medicamento(
            String id,
            String nome,
            String dosagem,
            String horario,
            String frequencia,
            String paciente,
            String observacao
    ) {

        this.id = id;
        this.nome = nome;
        this.dosagem = dosagem;
        this.horario = horario;
        this.frequencia = frequencia;
        this.paciente = paciente;
        this.observacao = observacao;
        this.dataCadastro = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}