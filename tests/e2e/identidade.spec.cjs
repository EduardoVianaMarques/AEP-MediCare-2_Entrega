const {test,expect}=require('playwright/test');
test('página inicial e navegação até cadastro mantêm identidade',async({page})=>{
    await page.goto('/');await expect(page.getByRole('heading',{level:1})).toContainText('Cuidar cabe');
    await expect(page.locator('.site-header .mc-brand img')).toHaveAttribute('src','/img/medicare-mark.svg');
    expect(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth)).toBe(true);
    await page.screenshot({path:test.info().outputPath('inicio-0.4.png'),fullPage:true});
    await page.getByRole('link',{name:'Organizar meu cuidado'}).click();await expect(page).toHaveURL(/\/cadastro$/);
    await expect(page.locator('#cadastroForm')).toBeVisible();
});
test('login mostra senha apenas quando solicitado',async({page})=>{
    await page.goto('/login');await page.locator('#senha').fill('senha-teste');
    await page.getByRole('button',{name:'Mostrar senha',exact:true}).click();await expect(page.locator('#senha')).toHaveAttribute('type','text');
    await page.getByRole('button',{name:'Ocultar senha',exact:true}).click();await expect(page.locator('#senha')).toHaveAttribute('type','password');
    await page.screenshot({path:test.info().outputPath('login-0.4.png'),fullPage:true});
});
test('redução de movimento mantém navegação e conteúdo',async({page})=>{
    await page.emulateMedia({reducedMotion:'reduce'});await page.goto('/');
    expect(await page.locator('.care-symbol').evaluate(el=>getComputedStyle(el).animationName)).toBe('none');
    await page.getByRole('link',{name:'Organizar meu cuidado'}).click();await expect(page).toHaveURL(/\/cadastro$/);
});
test('cadastro com senhas diferentes conserva os campos e mostra erro',async({page})=>{
    await page.goto('/cadastro');await page.locator('#nome').fill('Ana Silva');await page.locator('#email').fill('ana@example.com');
    await page.locator('#senha').fill('senha123');await page.locator('#confirmarSenha').fill('senha456');
    await page.getByRole('button',{name:'Criar minha conta'}).click();await expect(page.locator('#mensagem')).toHaveText('As senhas não coincidem.');
    await expect(page.locator('#nome')).toHaveValue('Ana Silva');
});
