# MediCare — Segunda entrega da AEP (0.4.0)

O MediCare é uma prova de conceito acadêmica para organizar a rotina de medicamentos. Ele reúne em uma conta os horários, as informações da prescrição, os registros de uso e o estoque. Pode ser usado por quem toma medicamentos, por familiares e por cuidadores que acompanham mais de uma pessoa.

O projeto se relaciona ao **ODS 3 — Saúde e Bem-Estar** ao facilitar a consulta dessas informações no dia a dia. O MediCare é uma ferramenta de organização. Decisões sobre medicamentos, doses e mudanças de tratamento cabem ao profissional de saúde.

## Como o MediCare funciona

1. **Crie uma conta e entre no painel.** Cada conta acessa os medicamentos que cadastrou.
2. **Cadastre um medicamento.** Informe o nome, a dosagem conforme a prescrição, o horário no formato `HH:mm`, a frequência e o paciente. Use o campo de observações para guardar informações adicionais.
3. **Registre o uso.** Ao confirmar **Registrar uso**, o sistema salva o momento atual. Em **Histórico**, é possível consultar os últimos 100 registros do medicamento. As datas são armazenadas em UTC e exibidas no fuso do navegador.
4. **Acompanhe o estoque, se necessário.** Informe o saldo inicial e o limite mínimo de reposição. O painel exibe um alerta quando o saldo chega ao mínimo ou fica abaixo dele. As entradas e saídas são lançadas manualmente. **Registrar uso** grava somente o histórico.

O painel também permite editar ou excluir medicamentos, buscar por nome, paciente ou observações, combinar filtros e exportar o resultado da consulta em CSV. A busca funciona mesmo quando a palavra é digitada sem acento.

## Tecnologias e estrutura

| Parte | Como é usada |
| --- | --- |
| Java 17 e Spring Boot | Servem a interface e a API. Os controllers recebem as requisições, os serviços aplicam as regras e os repositórios acessam o banco. |
| MongoDB 7 | Armazena contas e medicamentos. Cada medicamento é vinculado ao ID da conta e pode conter histórico de uso e informações de estoque. |
| HTML, CSS e JavaScript | Montam as telas, enviam requisições à API e atualizam o painel conforme as ações do usuário. |
| Maven e Docker Compose | O Maven gerencia dependências, compilação e testes. O Compose inicia o MongoDB para uso local. |

## O que mudou na segunda entrega

- A autenticação usa sessões. As senhas são armazenadas como hash PBKDF2-HMAC-SHA256 com salt aleatório. Contas antigas que ainda tinham a senha em texto puro migram após um login válido.
- O cadastro passou a validar o formato do horário. A entrega acrescentou histórico de uso, controle de estoque e um script que cria o índice único de e-mail e o índice de medicamentos por usuário.
- A interface ganhou uma identidade voltada à saúde, com azul-petróleo, turquesa e branco, alertas de reposição em âmbar e uma marca com cruz e traçado de pulso. O painel é responsivo, oferece temas claro e escuro e guarda a preferência no navegador.
- O GitHub Actions executa os testes de backend e frontend em jobs separados e publica os relatórios como artefatos.

## Como executar localmente

Instale o **JDK 17** e o **Docker com Compose**. O Maven Wrapper baixa o Maven e as dependências no primeiro uso, por isso precisa de acesso à internet.

Na raiz do projeto, inicie o banco:

```bash
docker compose up -d
docker compose ps
```

Quando o MongoDB estiver com o status `healthy`, aplique os índices. O mesmo comando funciona com volumes já existentes.

```bash
docker compose exec -T mongo mongosh --quiet /docker-entrypoint-initdb.d/mongo-init.js
```

Inicie a aplicação pelo comando correspondente ao seu sistema.

**Windows PowerShell**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux ou macOS**

```bash
bash mvnw spring-boot:run
```

Abra [http://localhost:8080](http://localhost:8080) e crie uma conta. O MongoDB local fica disponível em `127.0.0.1:27017`, com os dados armazenados em um volume persistente. Para usar uma instância externa, defina `SPRING_DATA_MONGODB_URI` e aplique os índices de `scripts/mongo-init.js` à base usada pela aplicação.

## Testes e cobertura

### Backend

Execute `.\mvnw.cmd clean verify` no PowerShell ou `bash mvnw clean verify` no Linux/macOS. O Maven roda os testes Java e gera o relatório em `target/site/jacoco/index.html`. O JaCoCo exige pelo menos **70% de cobertura de linhas** do código Java de produção.

Para incluir os testes com MongoDB real, mantenha o banco em execução e defina `MONGODB_INTEGRATION=true` antes de rodar o Maven.

```powershell
$env:MONGODB_INTEGRATION = 'true'
.\mvnw.cmd clean verify
```

```bash
MONGODB_INTEGRATION=true bash mvnw clean verify
```

### Frontend

Use **Node.js 22 ou superior**. O primeiro comando executa os testes unitários. Os demais instalam e executam os testes de interface com Playwright.

```bash
npm test
npm install
npx playwright install chromium
npm run test:e2e
```

O Playwright testa os fluxos da interface com uma API simulada, em configurações de desktop e celular. Na validação do redesign, os **23 testes unitários do frontend passaram**. Os **28 cenários de navegador** ficaram pendentes porque o ambiente bloqueou a inicialização do Chromium. A compilação Java, os testes com MongoDB e a cobertura desta versão ainda precisam ser confirmados com `clean verify`.
