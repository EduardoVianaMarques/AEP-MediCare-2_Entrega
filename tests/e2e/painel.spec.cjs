const {test,expect} = require('playwright/test');

// API simulada: estes testes validam a interface; o backend tem suíte própria com MongoDB.
test.beforeEach(async ({page}) => {
    let meds = [
        {id:'1',nome:'Medicamento A',dosagem:'Conforme receita',horario:'08:00',frequencia:'Diária',paciente:'Ana',observacao:'Após refeição',estoque:{quantidade:2,minimo:5}},
        {id:'2',nome:'Vitamina B',dosagem:'Conforme receita',horario:'10:00',frequencia:'Diária',paciente:'João',observacao:'',estoque:null}
    ];
    const history = [];
    await page.route('**/api/**',async route => {
        const req = route.request(), url = new URL(req.url()).pathname, method=req.method();
        let data = {}, status = 200;
        if(url === '/api/auth/usuario') data = {id:'u1',nome:'Ana Silva'};
        else if(url === '/api/auth/logout') status=204;
        else if(url.endsWith('/historico')) {
            if(method==='POST') {data={id:'r1',registradoEm:'2026-10-29T12:00:00Z'};history.push(data);status=201;}
            else data=history;
        } else if(url.includes('/estoque')) {
            const med=meds.find(m=>m.id===url.split('/')[3]), body=req.postDataJSON();
            if(url.endsWith('/movimentos')) {
                if(med.estoque.quantidade+body.quantidade<0) {status=409;data={mensagem:'Saldo insuficiente'};}
                else {med.estoque.quantidade+=body.quantidade;data=med.estoque;}
            } else {med.estoque=body;data=body;status=201;}
        } else if(url==='/api/medicamentos') {
            if(method==='POST') {data={...req.postDataJSON(),id:'3',estoque:null};meds.push(data);status=201;}
            else data=meds;
        } else {
            const id=url.split('/').pop();
            if(method==='PUT') {const m=meds.find(m=>m.id===id);Object.assign(m,req.postDataJSON());data=m;}
            if(method==='DELETE') {meds=meds.filter(m=>m.id!==id);status=204;}
        }
        await route.fulfill({status,contentType:'application/json',body:status===204?'':JSON.stringify(data)});
    });
    await page.goto('/medicamentos');
    await expect(page.locator('.med-card')).toHaveCount(2);
});

test('painel resume dados e não gera rolagem horizontal',async ({page})=>{
    await expect(page.locator('#total')).toHaveText('2');
    await expect(page.locator('#alertas-total')).toHaveText('1');
    expect(await page.evaluate(()=>document.documentElement.scrollWidth<=window.innerWidth)).toBe(true);
    await page.screenshot({path:test.info().outputPath('painel.png'),fullPage:true});
});
test('busca sem acentos, paciente e reposição',async ({page})=>{
    await page.locator('#busca').fill('joao'); await expect(page.locator('.med-card')).toHaveCount(1);
    await page.locator('#busca').fill(''); await page.locator('#paciente-filtro').selectOption('Ana');
    await page.locator('#apenas-alertas').check(); await expect(page.locator('.med-card h3')).toHaveText('Medicamento A');
    await page.locator('#busca').fill('inexistente'); await expect(page.locator('#vazio-titulo')).toHaveText('Nenhum resultado encontrado');
    await page.getByRole('button',{name:'Limpar filtros',exact:true}).click();await expect(page.locator('.med-card')).toHaveCount(2);
});
test('cadastra, edita e exclui com confirmação',async ({page})=>{
    await page.locator('#novo').click();
    await page.locator('#nome').fill('Novo medicamento'); await page.locator('#dosagem').fill('Conforme receita');
    await page.locator('#horario').fill('09:00');await page.locator('#frequencia').fill('Diária');await page.locator('#paciente').fill('Maria');
    await page.getByRole('button',{name:'Salvar medicamento'}).click();await expect(page.locator('.med-card')).toHaveCount(3);
    let card=page.locator('.med-card').filter({has:page.getByRole('heading',{name:'Novo medicamento',exact:true})});
    await card.getByRole('button',{name:'Editar',exact:true}).click();await page.locator('#nome').fill('Nome alterado');
    await page.getByRole('button',{name:'Salvar medicamento'}).click();
    card=page.locator('.med-card').filter({has:page.getByRole('heading',{name:'Nome alterado',exact:true})});
    await card.getByRole('button',{name:'Excluir',exact:true}).click();await page.locator('#confirmacao-ok').click();
    await expect(page.locator('.med-card')).toHaveCount(2);
});
test('registra uso e consulta histórico',async ({page})=>{
    const card=page.locator('.med-card').first();await card.getByRole('button',{name:'Registrar uso'}).click();
    await page.locator('#confirmacao-ok').click();await expect(page.locator('#confirmacao')).not.toBeVisible();
    await card.getByRole('button',{name:'Histórico',exact:true}).click();await expect(page.locator('.timeline li')).toHaveCount(1);
});
test('entrada e erro de saldo insuficiente',async ({page})=>{
    const card=page.locator('.med-card').first();await card.getByRole('button',{name:'Estoque',exact:true}).click();
    await page.locator('#estoque-quantidade').fill('10');await page.locator('#estoque-form button[type=submit]').click();
    await expect(page.locator('#alertas-total')).toHaveText('0');
    await page.locator('.med-card').first().getByRole('button',{name:'Estoque',exact:true}).click();
    await page.locator('#movimento-tipo').selectOption('-1');await page.locator('#estoque-quantidade').fill('100');
    await page.locator('#estoque-form button[type=submit]').click();await expect(page.locator('#estoque-erro')).toHaveText('Saldo insuficiente');
    await expect(page.locator('#estoque-dialog')).toBeVisible();
});
test('exporta lista filtrada e persiste preferência de tema',async ({page})=>{
    await page.locator('#busca').fill('joao');const download=page.waitForEvent('download');await page.locator('#exportar').click();
    expect((await download).suggestedFilename()).toBe('medicare-medicamentos.csv');
    await page.locator('#tema').click();await expect(page.locator('html')).toHaveAttribute('data-theme','dark');
    await page.reload();await expect(page.locator('html')).toHaveAttribute('data-theme','dark');
});
test('não interpreta HTML vindo do cadastro',async ({page})=>{
    await page.route('**/api/medicamentos',route=>route.fulfill({json:[{id:'x',nome:'<img src=x onerror="window.invadiu=true">',paciente:'Ana',horario:'08:00',dosagem:'Teste',frequencia:'Diária'}]}));
    await page.reload();await expect(page.locator('.med-card h3')).toContainText('<img');
    expect(await page.evaluate(()=>window.invadiu)).toBeUndefined();await expect(page.locator('.med-card img')).toHaveCount(0);
});
test('mostra erro de carregamento e permite tentar novamente',async ({page})=>{
    let falhou=false;await page.route('**/api/medicamentos',async route=>{
        if(!falhou){falhou=true;await route.fulfill({status:500,json:{mensagem:'Falha simulada'}});}
        else await route.fallback();
    });
    await page.reload();await expect(page.locator('#vazio-titulo')).toHaveText('Não foi possível carregar');
    await page.getByRole('button',{name:'Tentar novamente'}).click();await expect(page.locator('.med-card')).toHaveCount(2);
});
test('erro de validação mantém formulário e dados',async ({page})=>{
    await page.route('**/api/medicamentos',async route=>{
        if(route.request().method()==='POST') await route.fulfill({status:400,json:{campos:{nome:'Nome inválido'}}});
        else await route.fallback();
    });
    await page.locator('#novo').click();await page.locator('#nome').fill('Teste');await page.locator('#dosagem').fill('Receita');
    await page.locator('#horario').fill('08:00');await page.locator('#frequencia').fill('Diária');await page.locator('#paciente').fill('Ana');
    await page.getByRole('button',{name:'Salvar medicamento'}).click();await expect(page.locator('#form-erro')).toHaveText('Nome inválido');
    await expect(page.locator('#nome')).toHaveValue('Teste');await expect(page.locator('#editor')).toBeVisible();
});

test('formulário fecha por teclado e devolve o foco',async ({page})=>{
    await page.locator('#novo').click();await expect(page.locator('#nome')).toBeFocused();
    await page.keyboard.press('Escape');await expect(page.locator('#editor')).not.toBeVisible();
    await expect(page.locator('#novo')).toBeFocused();
});
