/* Painel MediCare: renderização segura e operações explícitas por usuário. */
const $ = seletor => document.querySelector(seletor);
const core = window.MediCare;
const api = '/api/medicamentos';
let medicamentos = [], carregou = false, estoqueAtual = null, acaoConfirmada = null, versaoHistorico = 0;
let confirmarOrigem = null;

// Preferência visual é local; nenhum dado clínico é salvo no navegador.
try { document.documentElement.dataset.theme = localStorage.getItem('medicare-tema') || 'light'; } catch {}
$('#tema').setAttribute('aria-pressed', String(document.documentElement.dataset.theme === 'dark'));
$('#tema').addEventListener('click', () => window.MediCareUI.toggleTheme());

function mensagem(texto, erro = false) {
    $('#mensagem').textContent = texto;
    $('#mensagem').className = erro ? 'error' : '';
    $('#mensagem').hidden = !texto;
}
async function requisicao(url, opcoes = {}) {
    const resposta = await fetch(url, opcoes);
    const dados = resposta.status === 204 ? null : await resposta.json().catch(() => null);
    if (resposta.status === 401) { window.MediCareUI.navigate('/login'); throw new Error('Sessão encerrada. Entre novamente.'); }
    if (!resposta.ok) {
        const detalhes = dados?.campos ? Object.values(dados.campos).join(' · ') : '';
        throw new Error(detalhes || dados?.mensagem || 'Não foi possível concluir. Tente novamente.');
    }
    return dados;
}
const json = (method, body) => ({method,headers:{'Content-Type':'application/json'},body:JSON.stringify(body)});
const caminho = id => `${api}/${encodeURIComponent(id)}`;
function elemento(tag, classe, texto) {
    const el = document.createElement(tag);
    if (classe) el.className = classe;
    if (texto != null) el.textContent = texto;
    return el;
}
function botao(texto, classe, acao) {
    const el = elemento('button', classe, texto); el.type = 'button'; el.addEventListener('click', acao); return el;
}
function opcoesFiltro() {
    return {busca:$('#busca').value,paciente:$('#paciente-filtro').value,alertas:$('#apenas-alertas').checked,ordem:$('#ordem').value};
}
function atualizarResumo() {
    const r = core.resumo(medicamentos);
    $('#total').textContent = r.total; $('#pacientes-total').textContent = r.pacientes;
    $('#alertas-total').textContent = r.alertas; $('#estoques-total').textContent = r.estoques;
    const anterior = $('#paciente-filtro').value;
    $('#paciente-filtro').replaceChildren(new Option('Todos os pacientes', ''));
    [...new Set(medicamentos.map(m => m.paciente))].sort((a,b) => a.localeCompare(b,'pt-BR'))
        .forEach(p => $('#paciente-filtro').add(new Option(p,p)));
    if ([...$('#paciente-filtro').options].some(o => o.value === anterior)) $('#paciente-filtro').value = anterior;
}
function renderizar() {
    if (!carregou) return;
    const filtrados = core.filtrar(medicamentos, opcoesFiltro());
    $('#lista').replaceChildren(...filtrados.map((m, index) => { const card = cartao(m); card.style.setProperty("--card-index", index); return card; }));
    $('#resultado').textContent = `${filtrados.length} de ${medicamentos.length} medicamento(s)`;
    $('#vazio').hidden = filtrados.length > 0;
    $('#exportar').disabled = filtrados.length === 0;
    const semCadastros = medicamentos.length === 0;
    $('#vazio-titulo').textContent = semCadastros ? 'Seu cuidado começa aqui' : 'Nenhum resultado encontrado';
    $('#vazio-texto').textContent = semCadastros ? 'Adicione um medicamento para organizar suas informações.' : 'Experimente outro termo ou limpe os filtros.';
    $('#vazio-acao').textContent = semCadastros ? 'Cadastrar medicamento' : 'Limpar filtros';
    $('#vazio-acao').onclick = semCadastros ? () => editar() : limparFiltros;
}
function limparFiltros() {
    $('#busca').value = ''; $('#paciente-filtro').value = ''; $('#apenas-alertas').checked = false; renderizar();
}
function cartao(m) {
    const card = elemento('article','med-card');
    const top = elemento('div','card-top');
    const icon = elemento('span','med-icon');
    // SVG estático: nenhum conteúdo do usuário é interpretado como HTML.
    const svg = document.createElementNS('http://www.w3.org/2000/svg','svg');
    svg.setAttribute('viewBox','0 0 24 24'); svg.setAttribute('fill','none');
    const pill = document.createElementNS('http://www.w3.org/2000/svg','path');
    pill.setAttribute('d','M8 4a5 5 0 0 1 7 7l-4 4a5 5 0 0 1-7-7l4-4Zm-2 2 7 7');
    pill.setAttribute('stroke','currentColor'); pill.setAttribute('stroke-width','1.5');
    pill.setAttribute('stroke-linecap','round'); svg.append(pill); icon.append(svg); icon.setAttribute('aria-hidden','true');
    const badge = elemento('span',`stock-badge ${m.estoque ? (core.baixo(m) ? 'low' : '') : 'off'}`,
        m.estoque ? `${core.baixo(m) ? 'Reposição · ' : ''}${m.estoque.quantidade} un.` : 'Estoque não acompanhado');
    top.append(icon,badge);
    const details = elemento('div','details');
    for (const [label,value] of [['Paciente',m.paciente],['Horário',m.horario],['Frequência',m.frequencia],['Mínimo em estoque',m.estoque ? `${m.estoque.minimo} un.` : 'Não definido']]) {
        const campo = elemento('div'); campo.append(elemento('small','',label),elemento('strong','',value)); details.append(campo);
    }
    const actions = elemento('div','card-actions');
    actions.append(
        botao('✓ Registrar uso','primary',() => confirmar('Registrar uso',`Registrar uso de ${m.nome} agora? Esta ação não altera o estoque.`,async () => {
            await requisicao(`${caminho(m.id)}/historico`,{method:'POST'}); mensagem('Uso registrado. Consulte o histórico para conferir.');
        })),
        botao('Histórico','secondary',() => historico(m)),
        botao('Estoque','secondary',() => abrirEstoque(m)),
        botao('Editar','secondary',() => editar(m)),
        botao('Excluir','secondary remove',() => confirmar('Excluir medicamento',`Excluir ${m.nome}? O histórico de uso e o estoque deste medicamento também serão removidos.`,async () => {
            await requisicao(caminho(m.id),{method:'DELETE'}); mensagem('Medicamento excluído.'); await carregar();
        }))
    );
    card.append(top,elemento('h3','',m.nome),elemento('p','dosage',m.dosagem),details,
        elemento('p','observation',m.observacao || 'Nenhuma observação adicionada.'),actions);
    return card;
}
async function carregar() {
    $('#lista').setAttribute('aria-busy','true');
    if (!carregou) $('#lista').replaceChildren(...Array.from({length:4},() => elemento('div','skeleton')));
    try {
        medicamentos = await requisicao(api); carregou = true;
        atualizarResumo(); renderizar();
    } catch (erro) {
        mensagem(erro.message,true);
        if (!carregou) {
            $('#lista').replaceChildren(); $('#vazio').hidden = false;
            $('#vazio-titulo').textContent = 'Não foi possível carregar';
            $('#vazio-texto').textContent = 'Confira sua conexão e tente novamente.';
            $('#vazio-acao').textContent = 'Tentar novamente'; $('#vazio-acao').onclick = carregar;
            $('#resultado').textContent = 'Dados indisponíveis'; $('#exportar').disabled = true;
        } else mensagem('A operação terminou, mas a lista não pôde ser atualizada. Recarregue a página para conferir os dados.',true);
    } finally { $('#lista').setAttribute('aria-busy','false'); }
}
function editar(m = {}) {
    $('#formulario').reset(); $('#form-erro').textContent = '';
    for (const campo of ['id','nome','dosagem','horario','frequencia','paciente','observacao']) $(`#${campo}`).value = m[campo] ?? '';
    $('#titulo-formulario').textContent = m.id ? 'Editar medicamento' : 'Novo medicamento';
    $('#editor').showModal(); $('#nome').focus();
}
$('#formulario').addEventListener('submit',async event => {
    event.preventDefault(); const botaoSalvar = event.submitter; botaoSalvar.disabled = true;
    $('#form-erro').textContent = '';
    const dados = Object.fromEntries(['nome','dosagem','horario','frequencia','paciente','observacao'].map(c => [c,$(`#${c}`).value.trim()]));
    const id = $('#id').value;
    try {
        await requisicao(id ? caminho(id) : api,json(id ? 'PUT' : 'POST',dados));
        $('#editor').close(); mensagem(id ? 'Medicamento atualizado.' : 'Medicamento cadastrado.'); await carregar();
    } catch (erro) { $('#form-erro').textContent = erro.message; }
    finally { botaoSalvar.disabled = false; }
});
function confirmar(titulo,texto,acao) {
    confirmarOrigem = document.activeElement;
    $('#confirmacao-titulo').textContent = titulo; $('#confirmacao-texto').textContent = texto;
    $('#confirmacao-erro').textContent = ''; acaoConfirmada = acao;
    $('#confirmacao').showModal(); $('#confirmacao-cancelar').focus();
}
$('#confirmacao-cancelar').onclick = () => window.MediCareUI.closeDialog($('#confirmacao'));
$('#confirmacao-ok').onclick = async () => {
    if (!acaoConfirmada) return;
    $('#confirmacao-ok').disabled = true; $('#confirmacao-cancelar').disabled = true;
    try { await acaoConfirmada(); $('#confirmacao').close(); acaoConfirmada = null; }
    catch (erro) { $('#confirmacao-erro').textContent = erro.message; }
    finally { $('#confirmacao-ok').disabled = false; $('#confirmacao-cancelar').disabled = false; }
};
$('#confirmacao').addEventListener('cancel',event => { if ($('#confirmacao-ok').disabled) event.preventDefault(); });
$('#confirmacao').addEventListener('close',() => { if (confirmarOrigem?.isConnected) confirmarOrigem.focus(); });
async function historico(m) {
    const versao = ++versaoHistorico;
    $('#historico-titulo').textContent = m.nome;
    $('#historico-lista').replaceChildren(); $('#historico-vazio').hidden = false;
    $('#historico-vazio').textContent = 'Carregando registros…'; $('#historico-dialog').showModal();
    try {
        const registros = await requisicao(`${caminho(m.id)}/historico`);
        if (versao !== versaoHistorico) return;
        $('#historico-lista').replaceChildren(...[...registros].reverse().map(r => elemento('li','',
            new Date(r.registradoEm).toLocaleString('pt-BR',{dateStyle:'medium',timeStyle:'short'}))));
        $('#historico-vazio').hidden = registros.length > 0; $('#historico-vazio').textContent = 'Nenhum uso registrado.';
    } catch (erro) { if (versao === versaoHistorico) $('#historico-vazio').textContent = erro.message; }
}
function abrirEstoque(m) {
    estoqueAtual = m; $('#estoque-form').reset(); $('#estoque-erro').textContent = '';
    $('#estoque-titulo').textContent = m.nome;
    const inicial = !m.estoque;
    $('#estoque-descricao').textContent = inicial ? 'Inicie o acompanhamento informando o saldo que você possui.' : `Saldo atual: ${m.estoque.quantidade} unidades. Alerta: ${m.estoque.minimo} ou menos.`;
    $('#movimento-label').hidden = inicial; $('#minimo-label').hidden = !inicial;
    $('#estoque-minimo').required = inicial; $('#estoque-minimo').disabled = !inicial;
    $('#estoque-quantidade').min = inicial ? '0' : '1'; $('#estoque-quantidade').value = '';
    $('#estoque-dialog').showModal(); $('#estoque-quantidade').focus();
}
$('#estoque-form').addEventListener('submit',async event => {
    event.preventDefault(); const submit = event.submitter; submit.disabled = true; $('#estoque-erro').textContent = '';
    const inicial = !estoqueAtual.estoque;
    const quantidade = Number($('#estoque-quantidade').value);
    const dados = inicial ? {quantidade,minimo:Number($('#estoque-minimo').value)} : {quantidade:quantidade*Number($('#movimento-tipo').value)};
    try {
        await requisicao(`${caminho(estoqueAtual.id)}/estoque${inicial ? '' : '/movimentos'}`,json('POST',dados));
        $('#estoque-dialog').close(); mensagem('Estoque atualizado.'); await carregar();
    } catch (erro) { $('#estoque-erro').textContent = erro.message; }
    finally { submit.disabled = false; }
});
$('#exportar').addEventListener('click',() => {
    const lista = core.filtrar(medicamentos,opcoesFiltro());
    const url = URL.createObjectURL(new Blob([core.csv(lista)],{type:'text/csv;charset=utf-8;'}));
    const link = elemento('a'); link.href = url; link.download = 'medicare-medicamentos.csv';
    document.body.append(link); link.click(); link.remove(); setTimeout(() => URL.revokeObjectURL(url),1000);
    mensagem('Lista filtrada exportada. O arquivo contém informações pessoais; guarde-o com cuidado.');
});
$('#novo').onclick = () => editar();
for (const id of ['busca','paciente-filtro','apenas-alertas','ordem']) $(`#${id}`).addEventListener('input',renderizar);
document.querySelectorAll('[data-close]').forEach(b => b.onclick = () => window.MediCareUI.closeDialog(document.getElementById(b.dataset.close)));
$('#logout').onclick = async () => {
    $('#logout').disabled = true;
    try { await requisicao('/api/auth/logout',{method:'POST'}); window.MediCareUI.navigate('/login'); }
    catch (erro) { mensagem(erro.message,true); }
    finally { $('#logout').disabled = false; }
};
(async () => {
    $('#exportar').disabled = true;
    try {
        const usuario = await requisicao('/api/auth/usuario');
        $('#usuario-nome').textContent = usuario.nome; $('#avatar').textContent = usuario.nome.slice(0,1).toUpperCase();
        $('#boasVindas').textContent = `Olá, ${usuario.nome}. Vamos organizar sua rotina?`;
        await carregar();
    } catch (erro) { mensagem(erro.message,true); $('#boasVindas').textContent = 'Não foi possível carregar sua conta. Recarregue a página.'; }
})();
