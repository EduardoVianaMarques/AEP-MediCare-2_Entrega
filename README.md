# MediCare — Segunda entrega AEP

**Versão 0.4.0 · Engenharia de Software · 6S**

Prova de Conceito (PoC) em Java 17, Spring Boot e MongoDB para organizar medicamentos por usuário, consultar registros de uso e acompanhar o estoque manualmente.

## Problema e ODS

Pessoas que utilizam medicamentos, familiares e cuidadores precisam manter informações de uso e disponibilidade organizadas. O MediCare centraliza esses dados e contribui para o **ODS 3 — Saúde e Bem-Estar**.

O sistema não prescreve medicamentos, não calcula doses e não substitui orientação profissional.

## Equipe e organização do trabalho

O trabalho é organizado por Kanban, com as etapas **A fazer**, **Em andamento** e **Concluído**. A distribuição abaixo é proposta para organização da equipe e deve refletir a participação efetiva de cada integrante.

| Integrante | Responsabilidades propostas |
| --- | --- |
| Eduardo | Autenticação, CRUD, isolamento por usuário, histórico, estoque e modelagem MongoDB |
| Leonardo | Interface, busca, filtros, CSV, responsividade, acessibilidade e testes do frontend |
| Pedro | Testes Java, integração, cobertura, evidências, documentação e publicação da versão final |

- Quadro de tarefas disponível no projeto: [docs/QUADRO_TAREFAS.md](docs/QUADRO_TAREFAS.md).
- Quadro no GitHub Projects: **link a preencher após a criação**.
- RAs dos integrantes: **preencher em [docs/ENTREGA.md](docs/ENTREGA.md)**.

## Tecnologias e arquitetura

| Camada | Tecnologias / organização |
| --- | --- |
| Backend | Java 17, Spring Boot 3.5.16, Spring Web e Bean Validation |
| Persistência | MongoDB e Spring Data MongoDB |
| Frontend | HTML, CSS e JavaScript |
| Documentação da API | Springdoc OpenAPI / Swagger UI |
| Testes Java | JUnit, Mockito e MockMvc |
| Testes do frontend | Node.js e Playwright |
| Cobertura | JaCoCo, com limite mínimo de 70% das linhas do backend |
| Automação | Maven Wrapper e GitHub Actions |

Fluxo da aplicação: interface → controllers REST → services → repositories/MongoTemplate → MongoDB.
Models representam os documentos; DTOs delimitam entrada e saída; o mapper converte os dados de medicamentos.

## Funcionalidades

- Cadastro, login, logout e sessão por usuário.
- Proteção de senhas com PBKDF2-HMAC-SHA256 e migração de senhas legadas após login válido.
- Cadastro, consulta, edição e exclusão de medicamentos com isolamento por proprietário.
- Registro e consulta dos últimos 100 usos de cada medicamento.
- Estoque opcional, reposição, retirada manual e alerta visual quando o saldo atinge o mínimo.
- Busca sem acentos por nome, paciente e observações, filtros combinados e ordenação.
- Exportação CSV dos resultados filtrados, com escape e proteção contra interpretação de fórmulas.
- Interface responsiva, tema persistente, diálogos, animações e transições entre páginas.
- Feedback de carregamento, validação, erros, lista vazia e confirmação de exclusão.
- Marca SVG, navegação por teclado e respeito à preferência de redução de movimento.

**Estoque manual:** registrar uso não reduz o saldo automaticamente. Datas de uso são armazenadas em UTC e exibidas no fuso do navegador.

## Modelagem NoSQL

| Coleção | Conteúdo |
| --- | --- |
| `usuarios` | Nome, email, hash da senha e data de cadastro |
| `medicamentos` | Dados do medicamento, `usuarioId`, histórico de usos e estoque opcional |

O campo `medicamentos.usuarioId` referencia `usuarios._id`. O histórico é uma lista de subdocumentos, e o estoque é um documento aninhado. O proprietário é obtido da sessão pelo backend.

Índices de instalação: email único em `usuarios` e `usuarioId` em `medicamentos`. Consulte [docs/TECNICA.md](docs/TECNICA.md) para exemplos, API, migração e decisões de modelagem.

## Executar a aplicação

Requisitos: **JDK 17**, MongoDB acessível e internet para baixar Maven e dependências. Docker com Compose é uma opção para executar o banco. Para testes do frontend, utilize **Node.js 22 ou superior**.

Execute os comandos na pasta que contém `pom.xml` e `mvnw.cmd`.

### 1. Iniciar o MongoDB com Docker

```bash
docker compose up -d
```

Após o banco estar saudável, aplique os índices, inclusive em volumes existentes:

```bash
docker compose exec -T mongo mongosh --quiet /docker-entrypoint-initdb.d/mongo-init.js
```

Se já utiliza MongoDB local, a URI padrão é `mongodb://localhost:27017/medicare`; não é necessário iniciar outro banco na mesma porta. Nesse caso, aplique o script `scripts/mongo-init.js` na base utilizada.

Para outro servidor, configure a variável de ambiente `SPRING_DATA_MONGODB_URI` antes de iniciar a aplicação. Preserve os dados existentes e aplique os índices na base correta.

### 2. Iniciar o backend

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
bash mvnw spring-boot:run
```

O `JAVA_HOME` deve apontar para a pasta do JDK 17, sem `bin` no final. Verifique a instalação com `java -version` e `javac -version`.

### 3. Acessar

- Aplicação: [http://localhost:8080](http://localhost:8080).
- Swagger UI: [http://localhost:8080/docs](http://localhost:8080/docs).
- Contrato OpenAPI: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs).

Cadastre uma conta, entre e adicione um medicamento. Utilize **Registrar uso**, **Histórico** e os controles de estoque para demonstrar os fluxos.

## Testes e cobertura

### Resultados confirmados

A execução local no Windows, em **10/11/2026**, com Java **17.0.12** e MongoDB disponível, terminou com:

| Verificação | Resultado |
| --- | --- |
| Build Maven | `BUILD SUCCESS` |
| Testes Java | **89 aprovados** |
| Falhas / erros / ignorados | **0 / 0 / 0** |
| Integração com MongoDB | **2 testes executados e aprovados**, incluídos nos 89 |
| Verificação JaCoCo | Limite mínimo de **70% das linhas** atendido |
| Relatório JaCoCo apresentado | **328 de 337 linhas cobertas — 97,33%** |
| Instruções / ramificações no relatório apresentado | **95% / 87%** |
| Testes unitários do frontend | **23 aprovados** |
| Testes Playwright | **28 cenários configurados; execução ainda não comprovada** |

Os resultados Java são baseados no log da execução local apresentado pela equipe; os percentuais são do relatório JaCoCo apresentado. Uma nova execução pode atualizar os percentuais. A medição JaCoCo abrange o backend Java de produção, sem exclusões de classes, e não mede HTML, CSS ou JavaScript.

### Executar testes Java sem habilitar a integração

Windows PowerShell, em sessão sem `MONGODB_INTEGRATION=true`:

```powershell
.\mvnw.cmd clean verify
```

Linux/macOS:

```bash
bash mvnw clean verify
```

Sem a variável de integração habilitada, os dois testes de MongoDB são ignorados.

### Executar os 89 testes, incluindo MongoDB real

Mantenha MongoDB acessível em `localhost:27017`.

Windows PowerShell:

```powershell
$env:MONGODB_INTEGRATION = "true"
.\mvnw.cmd clean verify
```

Linux/macOS:

```bash
MONGODB_INTEGRATION=true bash mvnw clean verify
```

Os testes de integração usam uma base temporária `medicare_test_<UUID>` e a removem ao finalizar. Verificam persistência, relacionamento, histórico, limite de registros, isolamento e operações de estoque, incluindo concorrência.

### Consultar e preservar evidências

| Caminho | Conteúdo |
| --- | --- |
| `target/site/jacoco/index.html` | Relatório visual de cobertura |
| `target/site/jacoco/jacoco.xml` | Cobertura em XML |
| `target/surefire-reports/` | Resultados dos testes Java |
| `target/medicare-0.4.0.jar` | Aplicação empacotada |

No PowerShell, abra o relatório:

```powershell
Start-Process .\target\site\jacoco\index.html
```

O comando `clean` remove a pasta `target`; preserve as evidências antes de limpar quando necessário. O workflow [.github/workflows/ci.yml](.github/workflows/ci.yml) está configurado para executar os testes com MongoDB e publicar os relatórios. O sucesso local não comprova, por si só, uma execução no GitHub Actions.

### Testes do frontend

```bash
npm test
```

Para executar os testes de navegador:

```bash
npm install
npx playwright install chromium
npm run test:e2e
```

Os testes unitários Node verificam regras de dados e navegação. O Playwright utiliza API simulada para conferir a interface em desktop e mobile; a integração real com MongoDB é verificada pela suíte Java. A execução dos cenários de navegador e a inspeção visual completa ainda precisam ser registradas.

## Evolução em relação à primeira entrega

| Base descrita na documentação | Evolução implementada |
| --- | --- |
| Cadastro, login e CRUD por usuário | Fluxos mantidos e ampliados |
| Senhas em texto puro | Hash PBKDF2 e migração após login válido |
| Documentos simples | Histórico com subdocumentos e estoque aninhado |
| Sem registro de uso | Registro de uso e consulta do histórico pela interface |
| Horário apenas obrigatório | Validação do formato e intervalo `HH:mm` |
| Índices apenas anotados | Script reproduzível para criação dos índices |
| Interface inicial | Novo painel, marca, tema, busca, filtros, CSV e transições |
| Testes e cobertura | 89 testes Java aprovados com integração e limite de cobertura atendido |
| Documentação inicial | Arquitetura, testes, quadro de tarefas, checklist e roteiro de vídeo |

A evolução deve ser vinculada às referências reais da primeira e da segunda entrega no GitHub.

## Documentação

- [Arquitetura, banco, API e migração](docs/TECNICA.md)
- [Plano de testes](docs/TESTES.md)
- [Quadro de tarefas](docs/QUADRO_TAREFAS.md)
- [Identidade visual](docs/DESIGN_0.4.md)
- [Roteiro do vídeo de 3 a 5 minutos](docs/ROTEIRO_VIDEO.md)
- [Identificação e checklist](docs/ENTREGA.md)
- [Histórico de alterações](CHANGELOG.md)

Os registros antigos em `docs/TESTES.md`, `docs/ENTREGA.md` e `docs/QUADRO_TAREFAS.md` ainda devem ser sincronizados com os resultados confirmados neste README.

## Publicação e versionamento

A versão da aplicação é **0.4.0**. O pacote recebido não contém o histórico Git original. Atualize o repositório existente da equipe, preservando o histórico e revisando os arquivos antes de publicar.

Após a revisão, com os testes aprovados e sem uma tag `v0.4.0` existente:

```bash
git add README.md
git commit -m "docs: atualiza README com validacao da segunda entrega"
git tag -a v0.4.0 -m "Segunda entrega AEP — MediCare"
git push origin HEAD
git push origin v0.4.0
```

Esse exemplo publica a atualização do README; outras alterações do projeto devem ser revisadas e commitadas antes de identificar a versão final. Informe referências reais para os dois marcos da AEP.

## Pendências para finalizar a entrega

- [x] Compilação e empacotamento concluídos.
- [x] 89 testes Java aprovados, incluindo os dois testes com MongoDB.
- [x] Cobertura mínima de 70% do backend comprovada.
- [x] 23 testes unitários do frontend aprovados.
- [x] Código com coleções relacionadas e objetos complexos implementado.
- [ ] Executar e registrar os cenários de navegador e a validação visual dos fluxos.
- [ ] Atualizar os demais documentos e estados do quadro com as evidências reais.
- [ ] Confirmar a divisão de tarefas e preencher os RAs.
- [ ] Informar o link do repositório e do GitHub Projects, quando criado.
- [ ] Publicar o código e informar os commits/tags reais de cada entrega.
- [ ] Gravar e disponibilizar o vídeo de 3 a 5 minutos.

## Links da entrega

| Item | Referência |
| --- | --- |
| Repositório GitHub | A preencher |
| Quadro GitHub Projects | A preencher após criação |
| Primeira entrega — commit/tag/release | A preencher com referência real |
| Segunda entrega — commit/tag/release | A preencher após publicação |
| Vídeo YouTube — 3 a 5 minutos | A preencher |
