# MediCare 0.4 — identidade e movimento

## Direção visual

Identidade própria baseada em verde-tinta (#153d35), papel (#f4f3eb) e lima (#d9f19c).
Marca SVG desenhada para o projeto: dois arcos formam um m com um ponto de apoio.
Um símbolo radial vetorial aparece como elemento decorativo nas telas de entrada.
Tipografia de sistema para leitura e Georgia em títulos editoriais. Sem dependências de fontes remotas.
A página inicial, login, cadastro e painel compartilham marca, botões, espaçamento e linguagem visual.

## Movimento

- Transições entre documentos via CSS View Transitions quando suportadas.
- Fallback progressivo: saída de 140 ms e entrada suave. Links funcionam sem JS.
- Cliques modificados, downloads, âncoras e links externos mantêm comportamento padrão.
- Voltar/avançar com cache do navegador limpa o estado de saída.
- Caso o usuário interrompa uma navegação, o estado visual é restaurado após 2 segundos.
- Elementos editoriais aparecem ao entrar na área visível; conteúdo continua acessível sem JS.
- Símbolos decorativos giram lentamente; cartões têm entrada escalonada e interação ao passar o mouse.
- Diálogos têm animação de entrada e fechamento pelos botões; Escape preserva o comportamento nativo.
- Tema do painel usa View Transition quando disponível.
- prefers-reduced-motion desativa movimentos e navegação artificialmente atrasada.

## Arquivos

- static/img/medicare-mark.svg: marca e favicon.
- static/img/medicare-symbol.svg: elemento visual decorativo.
- static/css/identity.css: identidade e transições compartilhadas.
- static/css/home.css: página inicial editorial.
- static/css/auth.css: login e cadastro com layout dividido.
- static/css/panel-art.css: identidade do painel, preservando os componentes existentes.
- static/js/experience.js: navegação, tema, revelação e controles de mostrar senha.

## Validação

23 testes Node executados e aprovados, incluindo os 14 testes de dados e 9 testes novos de experiência.
28 cenários Playwright reconhecidos: 14 cenários em dois tamanhos (desktop/mobile).
A sintaxe dos scripts e as referências de assets foram verificadas.
As quatro páginas e os novos assets responderam HTTP 200 no servidor estático de testes; isso não valida o backend Spring Boot.
O navegador Chromium não está disponível; inspeção visual e testes E2E não foram executados com sucesso.
Os testes Java e a cobertura JaCoCo continuam pendentes da validação já descrita na versão 0.3.
Não atribuir o percentual do módulo de regras a toda a interface.

## Executar e conferir

- npm test: testes de regras e navegação.
- npm install; npx playwright install chromium; npm run test:e2e: testes de navegador com API simulada.
- .\mvnw.cmd clean spring-boot:run: execução da aplicação completa no Windows.
- Test-Path .\target\classes\static\index.html: confirma se a página foi copiada para a execução.

Abra o projeto completo pelo pom.xml e preserve src/main/resources/static.
Caso o IntelliJ não encontre index.html, pare a execução antiga e inicie pelo Maven após clean.
