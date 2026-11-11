const {test} = require('node:test');
const assert = require('node:assert/strict');
const vm = require('node:vm');
const fs = require('node:fs');
const path = require('node:path');
const source = fs.readFileSync(path.join(__dirname,'../../src/main/resources/static/js/experience.js'),'utf8');

// Ambiente mínimo para verificar navegação e preferência de movimento sem Chromium.
function setup({reduced=false,native=false}={}) {
    const events={}, timers=[], assignments=[], classes=new Set(), stored={};
    const document={documentElement:{dataset:{theme:'light'}},body:{classList:{add:c=>classes.add(c),remove:c=>classes.delete(c)}},
        querySelector:()=>null,querySelectorAll:()=>[],getElementById:()=>null,
        addEventListener:(type,fn)=>events[type]=fn};
    const location={href:'http://localhost:4173/login',origin:'http://localhost:4173',pathname:'/login',search:'',assign:url=>assignments.push(url)};
    const window={matchMedia:()=>({matches:reduced,addEventListener:()=>{}}),setTimeout:(fn,ms)=>{timers.push({fn,ms});},
        addEventListener:(type,fn)=>events[type]=fn};
    if(native){window.onpageswap=null;window.CSSViewTransitionRule=function(){};}
    vm.runInNewContext(source,{window,document,location,URL,Intl,Date,localStorage:{setItem:(k,v)=>stored[k]=v}});
    return {api:window.MediCareUI,window,document,events,timers,assignments,classes,stored};
}
test('navegação reduzida é imediata e não anima',()=>{
    const s=setup({reduced:true});s.api.navigate('/cadastro');
    assert.equal(s.assignments[0],'http://localhost:4173/cadastro');assert.equal(s.timers.length,0);
});
test('navegação nativa não sofre atraso artificial',()=>{
    const s=setup({native:true});s.api.navigate('/cadastro');assert.equal(s.assignments.length,1);assert.equal(s.timers.length,0);
});
test('fallback faz saída curta e evita clique duplicado',()=>{
    const s=setup();s.api.navigate('/cadastro');s.api.navigate('/');assert.equal(s.assignments.length,0);
    assert.ok(s.classes.has('page-leaving'));s.timers.find(t=>t.ms===140).fn();assert.equal(s.assignments.length,1);
});
test('voltar pelo histórico restaura a interação',()=>{
    const s=setup();s.api.navigate('/cadastro');s.events.pageshow();assert.ok(!s.classes.has('page-leaving'));
});
test('navegação interrompida não deixa a página invisível',()=>{
    const s=setup();s.api.navigate('/cadastro');s.timers.find(t=>t.ms===2000).fn();assert.ok(!s.classes.has('page-leaving'));
});
test('alternar tema persiste preferência mesmo sem View Transition API',()=>{
    const s=setup();s.api.toggleTheme();assert.equal(s.document.documentElement.dataset.theme,'dark');assert.equal(s.stored['medicare-tema'],'dark');
});
test('fechar diálogo respeita redução de movimento',async()=>{
    const s=setup({reduced:true});let closed=false;
    await s.api.closeDialog({open:true,close:()=>{closed=true;},animate:()=>{throw Error('Não deve animar');}});assert.ok(closed);
});
test('clique com Ctrl preserva a abertura em outra aba',()=>{
    const s=setup();let prevented=false;
    s.events.click({target:{closest:()=>({href:'http://localhost:4173/cadastro',hasAttribute:()=>false})},button:0,ctrlKey:true,preventDefault:()=>{prevented=true;}});
    assert.equal(prevented,false);assert.equal(s.timers.length,0);
});
test('âncora na própria página mantém comportamento padrão',()=>{
    const s=setup();let prevented=false;
    s.events.click({target:{closest:()=>({href:'http://localhost:4173/login#form',hasAttribute:()=>false})},button:0,preventDefault:()=>{prevented=true;}});
    assert.equal(prevented,false);
});
