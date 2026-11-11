const {test} = require('node:test');
const assert = require('node:assert/strict');
const {filtrar,resumo,csv,baixo} = require('../../src/main/resources/static/js/painel-core.js');
const lista = [
 {nome:'Zinco',paciente:'João',horario:'09:00',observacao:'Após refeição',estoque:{quantidade:2,minimo:5}},
 {nome:'Ácido fólico',paciente:'Ana',horario:'07:00',estoque:{quantidade:20,minimo:5}},
 {nome:'Vitamina',paciente:'Ana',horario:'12:00',estoque:null}
];
test('busca ignora acentos e caixa',() => assert.equal(filtrar(lista,{busca:' ACIDO '})[0].nome,'Ácido fólico'));
test('busca também paciente',() => assert.equal(filtrar(lista,{busca:'joao'}).length,1));
test('busca observações',() => assert.equal(filtrar(lista,{busca:'refeicao'}).length,1));
test('combina filtros e não confunde estoque ausente com zero',() => assert.equal(filtrar(lista,{paciente:'Ana',alertas:true}).length,0));
test('ordena horários e não modifica entrada',() => { assert.equal(filtrar(lista)[0].horario,'07:00');assert.equal(lista[0].nome,'Zinco'); });
test('ordena estoque com não acompanhado por último',() => assert.deepEqual(filtrar(lista,{ordem:'estoque'}).map(m=>m.nome),['Zinco','Ácido fólico','Vitamina']));
test('resumo conta pacientes únicos e alertas',() => assert.deepEqual(resumo(lista),{total:3,pacientes:2,alertas:1,estoques:2}));
test('estoque no mínimo dispara alerta',() => assert.equal(baixo({estoque:{quantidade:5,minimo:5}}),true));
test('estoque zero dispara alerta',() => assert.equal(baixo({estoque:{quantidade:0,minimo:0}}),true));
test('lista vazia tem resumo zero',() => assert.deepEqual(resumo([]),{total:0,pacientes:0,alertas:0,estoques:0}));
test('CSV escapa aspas, separador e quebras de linha',() => assert.ok(csv([{nome:'A;"B"\nC'}]).includes('"A;""B""\nC"')));
test('CSV neutraliza fórmulas e preserva UTF-8',() => {const s=csv([{nome:'  =2+2',paciente:'@SUM(A1)',observacao:'João'}]);assert.ok(s.startsWith('\ufeff'));assert.ok(s.includes("'  =2+2"));assert.ok(s.includes("'@SUM(A1)"));assert.ok(s.includes('João'));});
test('CSV não exporta undefined nem null',() => assert.ok(!/undefined|null/.test(csv([{nome:'Exemplo'}]))));
test('sem resultados não produz itens',() => assert.deepEqual(filtrar(lista,{busca:'inexistente'}),[]));
