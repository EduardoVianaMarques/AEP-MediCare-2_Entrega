# Alterações

## 0.4.0 — Identidade própria e movimento

- Marca e símbolo SVG originais, favicon e ícones vetoriais.
- Redesenho da página inicial, login, cadastro e painel.
- Paleta verde-tinta/papel/lima e composição editorial.
- Transições entre páginas, revelação ao rolar, cartões e diálogos animados.
- Redução de movimento e preservação da navegação nativa.
- Mostrar/ocultar senha e prevenção de envios duplicados na autenticação.
- 23 testes Node aprovados; 28 cenários de navegador configurados.

## 0.3.0 — Ampliação funcional e novo frontend

- Painel e página inicial redesenhados; interface responsiva e tema escuro.
- Indicadores, busca sem acentos, filtros, ordenação e exportação CSV.
- Controle de estoque com alerta de reposição e movimentações atômicas.
- Formulários acessíveis, confirmação, estados vazios e tratamento de erros.
- Novos testes Java de validação, autorização, saldo e concorrência.
- 14 testes Node aprovados; 20 cenários Playwright configurados.
- CI do frontend independente; documentação de resultados e limitações.

## 0.2.0 — Segunda entrega

- Histórico de uso em subdocumentos MongoDB, limitado aos 100 registros mais recentes.
- Registro e consulta pela interface e API, isolados por sessão/proprietário.
- Atualizações atômicas para preservar histórico concorrente.
- Hash PBKDF2-HMAC-SHA256 com migração de contas antigas no login válido.
- Validação HH:mm, erros de JSON e duplicidade de índice.
- Script de índices e publicação local do MongoDB somente em loopback.
- Testes adicionais e GitHub Actions com relatórios e limite JaCoCo de 70%.
- Documentação técnica, testes, Kanban, checklist e roteiro do vídeo.

Não foi criado histórico Git nem afirmada cobertura sem execução.
