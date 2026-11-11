/* Regras puras compartilhadas entre interface e testes Node, sem dependências. */
(function(root) {
    const normalizar = valor => String(valor ?? "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
    const baixo = m => m.estoque != null && m.estoque.quantidade <= m.estoque.minimo;
    function filtrar(lista, {busca = "", paciente = "", alertas = false, ordem = "horario"} = {}) {
        return lista.filter(m => (!paciente || m.paciente === paciente) && (!alertas || baixo(m)) &&
            normalizar([m.nome,m.paciente,m.observacao].join(" ")).includes(normalizar(busca.trim())))
            .sort((a,b) => ordem === "estoque" ? (a.estoque?.quantidade ?? Infinity) - (b.estoque?.quantidade ?? Infinity) || a.nome.localeCompare(b.nome, "pt-BR") :
                String(a[ordem] ?? "").localeCompare(String(b[ordem] ?? ""), "pt-BR") || a.nome.localeCompare(b.nome, "pt-BR"));
    }
    function resumo(lista) {
        return {total:lista.length, pacientes:new Set(lista.map(m => m.paciente)).size,
            alertas:lista.filter(baixo).length, estoques:lista.filter(m => m.estoque != null).length};
    }
    function csv(lista) {
        // Neutraliza fórmulas de planilha, inclusive após espaços ou caracteres de controle.
        const celula = value => {
            let s = String(value ?? "");
            if (/^[\s\u0000-\u001f]*[=+@-]/.test(s)) s = "'" + s;
            return '"' + s.replace(/"/g, '""') + '"';
        };
        const linhas = [["Medicamento","Dosagem","Horário","Frequência","Paciente","Observação","Estoque","Mínimo"],
            ...lista.map(m => [m.nome,m.dosagem,m.horario,m.frequencia,m.paciente,m.observacao,m.estoque?.quantidade,m.estoque?.minimo])];
        return "\ufeff" + linhas.map(l => l.map(celula).join(";")).join("\r\n");
    }
    const api = {normalizar,baixo,filtrar,resumo,csv};
    if (typeof module !== "undefined") module.exports = api;
    else root.MediCare = api;
})(typeof window !== "undefined" ? window : globalThis);
