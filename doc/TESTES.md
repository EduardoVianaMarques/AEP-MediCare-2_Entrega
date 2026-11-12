# Estratégia de testes e resultados

[Voltar ao índice](README.md) · [Abrir catálogo de casos](CASOS_DE_TESTE.md)

## O que cada suíte verifica

| Suíte | Ferramentas | Escopo |
| --- | --- | --- |
| Java, regras e modelos | JUnit e Mockito | Autenticação, propriedade dos medicamentos, regras de histórico e estoque, mapeamento de dados e exceções. |
| Java, camada HTTP | MockMvc | Rotas, códigos de resposta, sessão, validação de JSON, páginas e contrato OpenAPI. |
| Java, integração com banco | Spring Boot e MongoDB real | Persistência, saídas concorrentes, limite do histórico e preservação de dados durante a edição. Usa uma base temporária local e a limpa após cada teste. |
| Frontend, testes unitários | `node:test` | Busca, filtros, ordenação, indicadores, CSV e comportamento de navegação, tema e redução de movimento. |
| Frontend, testes de interface | Playwright e Chromium | Navegação e interação com formulários e painel em desktop e celular. A API do painel é simulada para isolar a interface. |

## Como executar

Execute os comandos a partir da raiz do projeto. O [guia principal](../README.md#como-executar-localmente) mostra como iniciar o MongoDB e a aplicação.

**Java e cobertura**

```bash
bash mvnw clean verify
# Com o MongoDB local em execução em 127.0.0.1:27017:
MONGODB_INTEGRATION=true bash mvnw clean verify
```

No Windows, use `.\mvnw.cmd clean verify`. Para incluir a integração, defina primeiro `$env:MONGODB_INTEGRATION = 'true'` no PowerShell. Use JDK 17 e acesso ao Maven Central para baixar as dependências.

O Maven gera os resultados da suíte em `target/surefire-reports/` e o relatório JaCoCo em `target/site/jacoco/index.html`. O limite configurado é de **70% de cobertura de linhas Java**. O frontend é verificado em suítes próprias.

**Frontend**

```bash
npm test
npm install
npx playwright install chromium
npm run test:e2e
```

Use Node.js 22 ou superior. O Playwright inicia o servidor estático de testes e gera o relatório em `playwright-report/`. Os artefatos por execução, como capturas e traces quando configurados, ficam em `test-results/`.

## Resultados verificados

| Verificação | Resultado | Evidência |
| --- | --- | --- |
| `npm test` | **23 aprovados, 0 falhas** | Execução repetida no código do redesign durante a preparação desta documentação. |
| Playwright | **28 execuções previstas, sem conclusão** | Na tentativa anterior com o mesmo redesign, o runner reconheceu 14 cenários em dois perfis. O Chromium foi bloqueado ao iniciar com `socket() failed: Operation not permitted`, antes de executar as verificações de interface. |
| `bash mvnw -B clean verify` | **Bloqueado antes da compilação** | O Maven Wrapper retornou `UnknownHostException: repo.maven.apache.org` ao tentar baixar a distribuição. Nenhuma execução Java ou cobertura foi confirmada nesta validação. |

Os workflows de backend e frontend rodam em jobs separados no GitHub Actions. O job Java sobe o MongoDB, habilita a integração e publica relatórios de testes e cobertura. O job de frontend instala o Chromium, executa Node e Playwright e disponibiliza os relatórios. Os resultados de uma execução do CI devem ser conferidos no commit avaliado.

## Roteiro de verificação manual

Use dados fictícios em uma instância de teste.

| ID | Ação | Resultado esperado |
| --- | --- | --- |
| MAN-01 | Crie uma conta, entre, cadastre um medicamento e recarregue a página. | O medicamento continua disponível após recarregar. |
| MAN-02 | Crie uma segunda conta e entre nela em uma janela privativa. | A lista da segunda conta fica isolada da primeira. |
| MAN-03 | Inicie o estoque com 6 unidades e mínimo 5. Retire 1 unidade e filtre por reposição. | O saldo passa a 5 e o medicamento aparece no filtro. |
| MAN-04 | Registre um uso do medicamento do cenário anterior e abra o histórico. | Aparece um registro de uso e o saldo permanece em 5. |
| MAN-05 | Teste login e painel com largura de celular, alterne o tema e recarregue. | Os controles continuam utilizáveis e o tema selecionado é restaurado. |

Este roteiro descreve verificações manuais propostas. Os resultados acima registram as execuções disponíveis.
