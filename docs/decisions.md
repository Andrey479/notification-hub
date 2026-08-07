# Decisões de Arquitetura — Notification Hub

Registro das decisões técnicas relevantes do projeto, no formato ADR (Architecture
Decision Record): contexto, decisão, alternativas consideradas e trade-off aceito.
Serve como referência para o README e como preparo direto para perguntas de entrevista.

---

## ADR-001: RestClient em vez de WebClient

**Contexto:** o serviço precisa consumir a API pública do Open Library para
enriquecer dados de livros (capa, sinopse, número de páginas).

**Decisão:** usar `RestClient` (síncrono, introduzido no Spring 6.1).

**Alternativa considerada:** `WebClient` (reativo, non-blocking).

**Motivo:** o projeto não tem requisito de alta concorrência — o enriquecimento é
disparado por chamada individual, não em lote paralelo. `RestClient` é mais simples
de configurar, testar e ler.

**Trade-off aceito:** não escala para chamadas paralelas não-bloqueantes sem
refatoração. Se o volume de enriquecimentos simultâneos crescesse muito, `WebClient`
passaria a se justificar.

**Resposta de entrevista:** "Escolheria WebClient se precisasse de programação
reativa ou chamadas paralelas não-bloqueantes."

---

## ADR-002: Banco de dados compartilhado com o Library Manager

**Contexto:** o Notification Hub precisa ler e escrever dados de `Book` e `Loan`
que também pertencem ao domínio do Library Manager.

**Decisão:** os dois serviços apontam para o mesmo banco PostgreSQL. O Notification
Hub roda com `spring.jpa.hibernate.ddl-auto=validate` — nunca cria ou altera schema,
apenas valida que a estrutura esperada já existe. Só o Library Manager (dono do
schema) roda com `ddl-auto=update`.

**Alternativa considerada:** bancos separados por serviço (padrão correto de
microsserviços).

**Motivo:** para um projeto de portfólio júnior, dois bancos adicionariam
complexidade operacional (sincronização, consistência eventual) sem ganho
demonstrável no escopo do projeto.

**Trade-off aceito:** em produção real isso é um anti-pattern — acopla os dois
serviços via schema compartilhado. A fronteira de ownership é reforçada onde dá:
`Book.isbn` tem `@Column(insertable = false, updatable = false)` no Notification
Hub, porque quem escreve esse campo é o Library Manager.

**Resposta de entrevista:** "Não usaria em produção real — cada serviço teria seu
próprio banco para isolamento. Aqui foi trade-off consciente para manter o
portfólio focado no que importa demonstrar, com o ownership dos dados ainda
expresso no código via `insertable=false`."

---

## ADR-003: Fallback gracioso em falhas da API externa (nunca 500 para o cliente)

**Contexto:** a Open Library é uma API de terceiros — pode retornar 404, ficar
lenta, ou devolver JSON malformado. O cliente do Notification Hub não deveria
sofrer com isso.

**Decisão:** `OpenLibraryClient` diferencia três cenários de falha e trata cada um:
- `HttpClientErrorException.NotFound` → `Optional.empty()` (ISBN/key não existe)
- `ResourceAccessException` (timeout, erro de rede) → relançada para a camada de
  serviço decidir
- `RestClientException` genérica (ex: JSON malformado) → `Optional.empty()` +
  `log.warn`

O enriquecimento nunca falha com 500 — na ausência de dados externos, o livro é
persistido como estava, sem enriquecimento.

**Motivo:** disponibilidade de terceiro não deveria derrubar a experiência do
próprio serviço. Falha em enriquecer é degradação aceitável, não erro fatal.

**Validação:** coberto por `BookEnrichmentServiceIntegrationTest`
(`shouldFallbackGracefullyWhenOpenLibraryTimesOut`, usando delay do WireMock maior
que o `read-timeout` configurado) e pelos testes de malformação em
`OpenLibraryClientTest`.

**Resposta de entrevista:** "Testo com WireMock simulando 404, timeout e JSON
malformado — cada um cai em um catch diferente, todos resultam em resposta graciosa,
nunca em 500 para o cliente."

---

## ADR-004: `Book.isbn` como somente-leitura no Notification Hub

**Contexto:** o Notification Hub lê o ISBN do livro para consultar a Open Library,
mas quem é dono desse dado é o Library Manager.

**Decisão:** `@Column(insertable = false, updatable = false)` no campo `isbn` da
entidade `Book` do Notification Hub.

**Motivo:** expressar a fronteira de responsabilidade entre os dois serviços no
próprio código — não como comentário ou convenção informal, mas como restrição que
o JPA/Hibernate reforça. Mesmo com banco compartilhado (ADR-002), o ownership do
dado fica claro.

**Trade-off aceito:** dados de teste não podem ser inseridos via
`bookRepository.save()` (o `insertable=false` bloqueia); é necessário usar
`JdbcTemplate` diretamente para simular o estado real do banco em testes de
integração.

**Resposta de entrevista:** "Mesmo com banco compartilhado, a fronteira de
ownership dos dados é expressa em código — o Hibernate impede a escrita, não é só
uma convenção que alguém pode esquecer."

---

## ADR-005: Deserializer customizado para inconsistência de formato da Open Library

**Contexto:** o campo `description` retornado pela Open Library para um "work" vem
em dois formatos diferentes dependendo do registro: às vezes uma string simples,
às vezes um objeto `{type, value}`.

**Decisão:** `BookWorkDTO.description` usa um `ValueDeserializer<String>` customizado
(`DescriptionDeserializer`) que verifica o token JSON atual — se é `VALUE_STRING`,
lê direto; caso contrário, lê como árvore e extrai o campo `value`.

**Motivo:** a inconsistência é real e vem de uma API que não controlamos. Sem
tratamento, a desserialização falharia para parte dos livros.

**Trade-off aceito:** acoplamento a um formato de resposta específico da Open
Library; se a API mudar o shape de novo, o deserializer precisa ser atualizado.

**Resposta de entrevista:** "Como você lida com uma API externa inconsistente?" —
"Descobri na prática que o formato variava, escrevi um deserializer Jackson que
normaliza os dois casos, testado com ambos os formatos reais da API."

---

## ADR-006: Catch-all genérico não expõe detalhes de exceção ao cliente

**Contexto:** Handlers específicos (ResourceNotFoundException, BusinessException)
cobrem erros esperados, mas exceções não previstas (NPE, falhas de infraestrutura)
vazavam sem tratamento formal, arriscando expor stack traces ou mensagens internas.

**Decisão:** Adicionar @ExceptionHandler(Exception.class) como catch-all, retornando
mensagem genérica fixa e status 500, com log.error registrando a exceção completa
apenas no servidor.

**Alternativas consideradas:** Deixar sem catch-all (rejeitado: expõe whitelabel
error page do Spring, com stack trace); usar ex.getMessage() no corpo (rejeitado:
information disclosure).

**Trade-off:** Cliente perde granularidade de diagnóstico em erros inesperados —
aceitável, pois erros inesperados não deveriam orientar comportamento do cliente,
só alertar suporte via logs/monitoramento.