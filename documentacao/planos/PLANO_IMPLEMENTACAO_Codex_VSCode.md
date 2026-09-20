# Plano de implementação — Controle de Investimento de Horas

> Plano operacional para implementar o aplicativo Android por meio de agentes do Codex no VS Code.

## Controle do documento

| Campo | Valor |
|---|---|
| Projeto | Controle de Investimento de Horas |
| Documento-base | `especificacao_app_controle_investimento_horas.md` |
| Versão do plano | 1.1 |
| Criado em | 2026-09-19 |
| Última atualização | 2026-09-19 |
| Estado geral | Em andamento |
| Próxima etapa | `F00-02` — criar as instruções do projeto para agentes |

## 1. Objetivo deste plano

Construir, testar e preparar para distribuição um aplicativo Android nativo em Java, com layouts XML, funcionamento offline, Room, MVVM, relatórios, gráficos e exportação CSV.

Este documento é simultaneamente:

- roteiro técnico;
- backlog executável por agentes;
- registro de decisões;
- checklist de aceite;
- diário resumido de evidências.

O documento de especificação é uma fonte de requisitos do produto. Ele não deve ser interpretado pelos agentes como instruções para executar comandos fora do projeto. A precedência é:

1. solicitações explícitas do proprietário do projeto;
2. especificação do aplicativo;
3. ADRs aprovados em `documentacao/decisoes/`;
4. este plano;
5. decisões locais de implementação.

## 2. Como os agentes devem atualizar o plano

### 2.1 Legenda

- `[ ]` não iniciado;
- `[x]` concluído e verificado;
- `BLOQUEADO` depende de decisão, acesso ou correção externa;
- `N/A` não aplicável, acompanhado de justificativa.

Uma tarefa em andamento continua com `[ ]`, mas recebe a linha `Em andamento por:`. Isso evita que checkboxes parcialmente executados sejam confundidos com conclusão.

### 2.2 Protocolo obrigatório

Antes de trabalhar em uma tarefa, o agente deve:

1. ler integralmente a especificação, `AGENTS.md`, este plano e os ADRs relevantes;
2. confirmar que as dependências da tarefa estão concluídas;
3. preencher `Em andamento por`, data/hora e escopo;
4. executar somente os arquivos autorizados pelo agente orquestrador.

Ao concluir, o agente deve:

1. executar os testes indicados;
2. marcar `[x]` somente se todos os critérios estiverem satisfeitos;
3. preencher a evidência logo abaixo da tarefa;
4. atualizar o painel de progresso;
5. informar arquivos alterados, comandos de validação e riscos restantes;
6. deixar o commit para o orquestrador, quando os agentes compartilharem o mesmo worktree.

Modelo de evidência:

```text
Em andamento por: <agente/thread> — <AAAA-MM-DD HH:mm>
Concluído por: <agente/thread> — <AAAA-MM-DD HH:mm>
Evidência: <arquivos, testes e resultado observável>
Commit: <hash ou "ainda não criado">
Pendências: <nenhuma ou lista objetiva>
```

É proibido marcar uma tarefa como concluída apenas porque arquivos foram criados. Deve existir evidência de compilação, teste ou inspeção correspondente.

## 3. Painel de progresso

| Fase | Estado | Responsável | Gate de saída |
|---|---|---|---|
| PRE — ambiente e acessos | Em andamento | Proprietário + orquestrador | Toolchain verificada |
| F00 — governança e decisões | Em andamento | Orquestrador | ADRs aprovados |
| F01 — bootstrap Android | Não iniciado | Arquiteto Android | `assembleDebug`, testes e lint passam |
| F02 — dados locais | Não iniciado | Agente de dados | Room e CRUD testados |
| F03 — domínio e agregações | Não iniciado | Agente de domínio | Regras críticas cobertas por testes |
| F04 — tela Hoje | Não iniciado | Agente de UI | CRUD vertical utilizável |
| F05 — metas | Não iniciado | Agente de UI Metas | Cálculos e edição validados |
| F06 — evolução diária | Não iniciado | Agente de UI Gráficos | 7/30/90 dias validados |
| F07 — semanal | Não iniciado | Agente de UI Semanal | Domingo–sábado validado |
| F08 — dados, CSV e configurações | Não iniciado | Dados + UI | CSV aberto corretamente no Excel |
| F09 — cronômetro | Não iniciado | Domínio + UI | Recuperação após reinício validada |
| F10 — qualidade | Não iniciado | Agente QA | Suíte, acessibilidade e desempenho aprovados |
| F11 — empacotamento e publicação | Não iniciado | Orquestrador + proprietário | AAB assinado/testado ou N/A |
| MVP | Não iniciado | Proprietário | Todos os critérios de aceite aprovados |

## 4. Estratégia de agentes no Codex

### 4.1 Topologia

Usar um agente principal como **orquestrador** e até três subagentes simultâneos. A documentação oficial informa que subagentes podem ser solicitados diretamente no chat da IDE e que agentes personalizados do projeto podem ser definidos em `.codex/agents/`.

- Orquestrador: contratos, integração, arquivos compartilhados, revisão e checkpoints.
- Arquiteto Android: Gradle, estrutura, navegação e padrões comuns.
- Dados/domínio: Room, repositórios, cálculos e CSV.
- UI: fragments, ViewModels, adapters e layouts de uma feature delimitada.
- QA/revisor: leitura, testes, regressões, acessibilidade e desempenho.

Nem todos precisam estar ativos ao mesmo tempo. O limite recomendado é três subagentes, além do orquestrador.

### 4.2 Arquivos reservados ao orquestrador

Durante ondas paralelas, somente o orquestrador edita:

- arquivos Gradle e catálogo de versões;
- `AndroidManifest.xml`;
- `AGENTS.md`;
- banco Room e número de versão do schema após sua estabilização;
- grafo de navegação;
- recursos globais como `strings.xml`, temas e cores compartilhadas;
- este painel de progresso, quando houver risco de edições simultâneas.

Subagentes devem devolver ao orquestrador as strings, destinos de navegação e dependências de que precisam, em vez de editar esses arquivos concorrentemente.

### 4.3 Configuração prevista

Criar `.codex/config.toml`:

```toml
[agents]
enabled = true
max_concurrent_threads_per_session = 3
```

Criar estes arquivos em `.codex/agents/`:

```text
android-architect.toml
data-domain.toml
feature-ui.toml
qa-reviewer.toml
```

Cada agente personalizado deve conter `name`, `description` e `developer_instructions`. Não fixar modelo sem necessidade; assim, a configuração pode acompanhar os modelos disponíveis na instalação.

### 4.4 Contrato para toda delegação

Cada prompt de delegação deve informar:

- ID e objetivo da tarefa;
- requisitos da especificação relacionados;
- pré-condições;
- arquivos/diretórios permitidos;
- arquivos proibidos;
- comportamento esperado;
- testes obrigatórios;
- formato do relatório de retorno;
- proibição de ampliar escopo ou criar dependências sem autorização.

### 4.5 Git e isolamento

No fluxo principal da extensão do VS Code, subagentes podem compartilhar o workspace. Nesse caso:

- distribuir propriedade de caminhos sem sobreposição;
- apenas o orquestrador cria commits;
- criar um checkpoint antes e depois de cada fase;
- nunca iniciar três features que dependam de alterar o mesmo contrato.

Se forem usados chats delegados separados, usar worktrees separados e integrar por commits pequenos. Não fazer checkout da mesma branch em dois worktrees.

## 5. Ferramentas e auditoria da máquina

Auditoria executada em 2026-09-19 no Windows:

| Ferramenta | Necessidade | Situação encontrada | Ação |
|---|---|---|---|
| Git | Obrigatória | Instalado: 2.49.0 | Pode atualizar; versão atual já atende |
| VS Code | Obrigatória para este fluxo | Instalado: 1.138.0 x64 | Manter atualizado |
| Extensão Codex/OpenAI | Obrigatória | `openai.chatgpt` instalada | Confirmar login e acesso aos agentes |
| Java/JDK geral | Solicitada pelo proprietário | OpenJDK 27 GA instalado para os terminais; Oracle JDK 21.0.7 preservado lado a lado | Nenhuma ação no PRE-01A; isolar o Gradle no PRE-03 |
| JDK do build Android | Obrigatória | Ainda depende do Android Studio | Usar o JBR/JDK 17 suportado pelo AGP 9.4; não executar Gradle em JDK 27 |
| Extension Pack for Java | Recomendada | Instalada | Nenhuma ação |
| Gradle for Java | Recomendada | Instalada | Nenhuma ação |
| Android Studio | Obrigatória para SDK/emulador | Não encontrado | Instalar versão estável atual |
| Android SDK | Obrigatória | Não encontrado | Instalar pelo SDK Manager |
| Platform Tools/ADB | Obrigatória para aparelho/emulador | Não encontrado no `PATH` | Instalar pelo SDK Manager e configurar ambiente |
| Gradle global | Não necessário | Não encontrado | Não instalar; usar `gradlew` do projeto |
| Emulador/AVD | Recomendado | Não encontrado | Criar após instalar Android Studio |
| Aparelho Android | Obrigatório para homologação | Samsung Galaxy M62 informado pelo proprietário | Habilitar opções do desenvolvedor/depuração USB e registrar versão real do Android |
| GitHub/GitLab remoto | Opcional, recomendado | Remoto atual inconsistente | Decidir e configurar depois do primeiro commit |

### 5.1 Instalações obrigatórias

- [x] **PRE-01 — Instalar Android Studio estável**
  - Concluído por: /root — 2026-09-19 17:43.
  - Evidência: Android Studio 2026.1.4 Patch 1 instalado em `C:\Program Files\Android\Android Studio`; instalador oficial validado por SHA-256 (`07fb2a6d54d14b137e2a5db274ba0e70be9291eb4245707f53164bb30cd69e95`). O Setup Wizard foi concluído pelo proprietário. O SDK Manager instalou Platform `android-37.0`, Build Tools `36.0.0`, Platform Tools e Emulator; `adb version` retornou `1.0.41` / `37.0.1-15733141`.
  - Baixar do site oficial Android Developers.
  - Instalar Android Studio, Android SDK e Android Virtual Device.
  - Aceitar apenas licenças oficiais apresentadas pelo SDK Manager.
  - Evidência: Android Studio abre e o SDK Manager lista componentes instalados.

- [x] **PRE-01A — Instalar o Java mais recente lado a lado**
  - Concluído por: /root — 2026-09-19 17:54.
  - Evidência: build GA oficial OpenJDK 27+35 para Windows x64, sob GPLv2 com Classpath Exception, instalado no perfil local do usuário e validado pelo SHA-256 oficial (`41172837168dd25a8d9fe5eb253ac1efc568c5f9ff608144bcacadfdf50f876c`). Novos terminais Windows PowerShell e PowerShell 7 retornam `openjdk 27 2026-09-15` e `javac 27`; o Oracle JDK 21.0.7 continua acessível em sua instalação original. `JAVA_HOME` e `PATH` foram registrados apenas no ambiente/perfil local, fora do repositório; `STUDIO_JDK` permanece indefinido.
  - Commit: ainda não criado.
  - Pendências: configurar e provar o JDK do Gradle somente no PRE-03, quando houver wrapper do projeto.
  - Instalar JDK 27 GA, lançado em 2026-09-15, sem remover inicialmente o JDK 21 existente.
  - Preferir uma distribuição OpenJDK 27 adequada ao uso pretendido ou Oracle JDK 27 após revisar sua licença.
  - Fazer `java -version` apontar para o JDK 27 somente para uso geral no terminal.
  - Não usar JDK 27 para iniciar o Android Studio nem para executar o Gradle deste projeto enquanto a matriz oficial não declarar suporte.
  - Evidência: `java -version` informa 27 e o caminho da instalação fica registrado localmente, sem ser versionado.

- [x] **PRE-02 — Instalar componentes pelo SDK Manager**
  - Concluído por: /root — 2026-09-19 18:14.
  - Evidência: SDK em `C:\Users\eduar\AppData\Local\Android\Sdk` contém Platform `android-37.0`, Build Tools `36.0.0`, Platform Tools `37.0.1`, Emulator `37.1.11`, Command-line Tools latest `23.0.0` e imagem Google APIs x86_64 `android-37.0`. `adb version` retornou `1.0.41` / `37.0.1-15733141`; `sdkmanager --list` listou os componentes instalados.
  - Android SDK Platform estável escolhida para `compileSdk`/`targetSdk`.
  - Android SDK Build-Tools correspondente.
  - Android SDK Platform-Tools.
  - Android SDK Command-line Tools (latest).
  - Android Emulator.
  - Uma system image com Google APIs para o AVD.
  - Evidência: `adb version` e `sdkmanager --list` funcionam, diretamente ou por caminho absoluto.

- [x] **PRE-03 — Isolar os JDKs e configurar variáveis do Windows**
  - Concluído por: /root — 2026-09-19 18:14.
  - Evidência: `ANDROID_HOME` e `ANDROID_SDK_ROOT` do usuário apontam para `C:\Users\eduar\AppData\Local\Android\Sdk`; `platform-tools` foi acrescentado ao `PATH` do usuário; `STUDIO_JDK` do usuário está ausente; `java --version` e `javac --version` continuam em 27. O Temurin 17.0.20.1 foi instalado lado a lado para ferramentas Android/Gradle, sem alterar `JAVA_HOME` global. O arquivo local e ignorado `.gradle/config.properties` define `java.home` como JDK 17 para o macro `GRADLE_LOCAL_JAVA_HOME`. Gradle 9.7.1 temporário, validado por SHA-256 oficial, retornou Launcher/Daemon JVM 17.0.20.1.
  - Seguimento obrigatório em F01: depois de gerar e versionar o wrapper, executar `gradlew --version` para comprovar que ele resolve a mesma configuração. O JBR entregue pelo Android Studio instalado é Java 25, portanto não foi usado como JDK 17.
  - Definir `ANDROID_HOME` ou `ANDROID_SDK_ROOT` conforme orientação da versão instalada.
  - Adicionar `platform-tools` ao `PATH`.
  - Permitir que o terminal geral use o JDK 27 solicitado pelo proprietário.
  - Configurar o Gradle do projeto por `GRADLE_LOCAL_JAVA_HOME`/Daemon JVM criteria para usar o JBR/JDK 17 suportado, independentemente do Java global.
  - Não definir `STUDIO_JDK`; o Android Studio deve usar o JBR que acompanha a instalação.
  - Alinhar a configuração Gradle JDK do Android Studio com o JDK 17 usado pelo wrapper.
  - Não codificar caminhos pessoais em arquivos versionados.
  - Evidência: novo PowerShell deve encontrar `java` 27, `javac` 27 e `adb` após reiniciar o terminal; o wrapper de F01 deverá comprovar JVM 17.

- [x] **PRE-04 — Preparar o Galaxy M62 como aparelho principal**
  - Concluído por: /root — 2026-09-19 18:14.
  - Evidência: aparelho Samsung Galaxy M62 conectado e autorizado pelo ADB no estado `device`. Propriedades registradas sem identificador: fabricante `samsung`, modelo `SM-M625F`, Android `13` (API `33`), tela física `1080x2400`, densidade `450` dpi.
  - Usar o Samsung Galaxy M62 como aparelho físico principal de desenvolvimento e homologação.
  - No aparelho: `Configurações > Sobre o telefone > Informações do software`; tocar repetidamente em `Número da compilação` até o aparelho confirmar o modo de desenvolvedor.
  - Ativar `Depuração USB` apenas durante desenvolvimento e aceitar a chave RSA deste computador.
  - Conectar por cabo USB-C com dados; instalar driver USB oficial Samsung/Smart Switch somente se o Windows não reconhecer o aparelho via ADB.
  - Executar `adb devices` e confirmar estado `device`.
  - Registrar, sem identificadores pessoais, as propriedades reais do aparelho:

```powershell
adb shell getprop ro.product.manufacturer
adb shell getprop ro.product.model
adb shell getprop ro.build.version.release
adb shell getprop ro.build.version.sdk
adb shell wm size
adb shell wm density
```

  - Não assumir que o aparelho continua no Android 11 original; usar os comandos acima para registrar a versão instalada e o nível de API efetivo.
  - Após os testes, desativar a depuração USB quando ela não estiver sendo usada.
  - Evidência: Galaxy M62 aparece em `adb devices` e suas versões reais estão registradas no diário de execução.

- [x] **PRE-04A — Criar AVDs complementares**
  - Concluído por: /root — 2026-09-19 18:14.
  - Evidência: virtualização do Windows detectada; AVDs Pixel 5 `controle_horas_api26` (Google APIs, Android 8.0/API 26) e `controle_horas_api37` (Google APIs, Android 17/API 37) criados. Cada um foi iniciado sem janela e apareceu isoladamente em `adb devices` como `emulator-5554\tdevice`; ambos foram encerrados via ADB após a validação.
  - Verificar virtualização na BIOS/UEFI e no Windows.
  - Criar um AVD no API 26 para testar o `minSdk`.
  - Criar um AVD no API estável mais recente suportado pelo toolchain.
  - O Galaxy M62 cobre o uso real; os AVDs cobrem os extremos da matriz de compatibilidade.
  - Evidência: ambos os AVDs inicializam e aparecem em `adb devices` como `device`, não `offline`.

- [x] **PRE-05 — Validar Codex no VS Code**
  - Concluído por: /root + proprietário — 2026-09-19 18:14.
  - Evidência: proprietário confirmou uso ativo do Codex no VS Code; a sessão atual leu integralmente a especificação e executou comandos no workspace. A interface, login e autorização ao workspace estão funcionais no fluxo em uso.
  - Abrir a pasta do projeto no VS Code.
  - Abrir `Codex: Open Codex Sidebar` pela paleta.
  - Confirmar login OpenAI/ChatGPT e permissão para trabalhar no workspace.
  - Confirmar que atividade de subagentes aparece na interface quando uma delegação de teste for solicitada.
  - Evidência: chat do Codex lê a especificação e executa um comando somente leitura.

### 5.2 Ferramentas opcionais

- [ ] `winget`, para atualizações repetíveis no Windows.
- [ ] GitHub CLI, somente se o repositório remoto for GitHub.
- [ ] Cabo USB-C com transferência de dados para o Galaxy M62; driver Samsung apenas se necessário.
- [ ] Excel ou LibreOffice Calc para homologar o CSV.
- [ ] Ferramenta de gestão segura de segredos para senhas do keystore.

Não instalar globalmente Gradle, banco SQLite, servidor, Docker, Firebase CLI ou Google Cloud CLI para este MVP. Nenhum deles é necessário para a arquitetura offline definida.

### 5.3 Política de versões Java

“Usar o Java mais recente” tem três significados distintos neste projeto:

| Camada | Versão decidida | Motivo |
|---|---|---|
| Java disponível no Windows/terminal | JDK 27 GA | É a versão Java SE mais recente em 2026-09-19 |
| JVM que executa Android Studio/Gradle | JBR/JDK 17 | É a versão suportada e padrão do AGP 9.4; Gradle 9.7 ainda não executa em JVM 27 |
| Linguagem/bytecode do aplicativo | Java 17 | É o nível estável suportado pelo toolchain Android escolhido |

Os três níveis não devem ser confundidos. Instalar JDK 27 não torna suas APIs automaticamente disponíveis no Android. O agente responsável pelo bootstrap deve provar essa separação com `java -version` e `gradlew --version` antes de escrever código de produção.

## 6. Cadastros e contas

### 6.1 Necessários para desenvolver localmente

- [ ] **ACC-01 — Conta OpenAI/ChatGPT com acesso ao Codex**
  - Necessária para usar a extensão e agentes.
  - Não requer chave de API para este projeto; o aplicativo não integra a API da OpenAI.

Não é necessário cadastro de desenvolvedor Google para compilar, executar no emulador, instalar via `adb` ou produzir um APK local assinado.

### 6.2 Recomendados, mas não bloqueiam o MVP

- [ ] **ACC-02 — Repositório remoto privado**
  - Criar conta/repositório em GitHub, GitLab ou equivalente.
  - Habilitar autenticação em dois fatores.
  - Nunca versionar keystore, senhas ou `local.properties`.

- [ ] **ACC-03 — Conta Google operacional**
  - Usar uma conta duradoura e com autenticação em dois fatores.
  - Ela será proprietária do Play Console, se houver publicação.

### 6.3 Necessários somente para publicar no Google Play

- [ ] **ACC-04 — Decidir conta pessoal ou organização**
  - Pessoal: indicada para hobby/projeto individual.
  - Organização: indicada para empresa ou atividade comercial/profissional.
  - Organização exige dados legais, site oficial e número D-U-N-S; a emissão do D-U-N-S pode levar até 30 dias.
  - Registrar a decisão em `ADR-002-distribuicao-e-identidade.md`.

- [ ] **ACC-05 — Criar conta Google Play Console**
  - Ter pelo menos 18 anos.
  - Aceitar o contrato de distribuição.
  - Pagar taxa única de cadastro de US$ 25; confirmar o valor exibido no momento do cadastro.
  - Informar e verificar nome/endereço legais, e-mail e telefone.
  - Poderá ser exigido documento oficial e cartão em nome do titular.

- [ ] **ACC-06 — Atender à verificação de dispositivo/identidade**
  - Para novas contas pessoais, instalar o aplicativo móvel Play Console e verificar acesso a um aparelho Android quando solicitado.
  - Concluir todas as verificações do painel antes de planejar produção.

- [ ] **ACC-07 — Planejar teste fechado da conta pessoal**
  - Contas pessoais criadas após 2023-11-13 precisam, segundo a regra consultada em 2026-09-19, manter pelo menos 12 testadores inscritos continuamente por 14 dias.
  - Recrutar testadores antes de concluir o MVP.
  - Registrar canal de feedback e evidências das correções.
  - Revalidar a regra no Play Console antes de iniciar o teste, pois políticas podem mudar.

- [ ] **ACC-08 — Verificação de desenvolvedor Android no Brasil**
  - A documentação consultada informa marco de 2026-09-30 para proteções em dispositivos certificados no Brasil e em outros países participantes.
  - Quem distribuir pelo Google Play normalmente será registrado pelo próprio Play Console.
  - Distribuição exclusivamente fora da Play Store pode exigir Android Developer Console e registro do pacote/chave.
  - Revisar o painel oficial imediatamente antes da distribuição.

- [ ] **ACC-09 — Preparar dados públicos e política de privacidade**
  - E-mail público do desenvolvedor.
  - Nome do desenvolvedor.
  - Política de privacidade publicada em URL estável, mesmo que declare ausência de coleta/compartilhamento.
  - Formulário `Data safety`: declarar corretamente que os dados permanecem no aparelho e só saem por ação explícita de exportar/compartilhar.
  - Revisar também o comportamento de bibliotecas de terceiros.

### 6.4 Cadastros desnecessários no MVP

- Google Cloud Project/API: não necessário.
- Firebase: não necessário.
- OAuth Client ID: não necessário.
- Conta de serviço: não necessária.
- Backend, domínio ou banco em nuvem: não necessários.
- Conta de comerciante/Google Payments para vendas: não necessária enquanto o app for gratuito e sem compras; o perfil de pagamentos usado na verificação da identidade ainda pode ser solicitado pelo Play Console.

## 7. Fase F00 — governança e decisões

- [x] **F00-01 — Criar o primeiro checkpoint Git**
  - Concluído por: /root — 2026-09-19 18:14.
  - Evidência: criado `.gitignore` para artefatos Android/Java/VS Code e material de assinatura, sem regras que excluam `documentacao/`. O remoto `origin` aponta para GitHub, mas `master...origin/master [gone]` confirma que a referência remota local está inconsistente; não foi feito push nem alteração no remoto.
  - Commit: `0361f29` (`chore: add Android project gitignore`).
  - Verificar o estado do remoto atualmente inconsistente.
  - Criar `.gitignore` Android/Java/VS Code sem excluir documentação.
  - Commit sugerido: `docs: add product specification and implementation plan`.

- [x] **F00-02 — Criar `AGENTS.md`**
  - Concluído por: /root — 2026-09-19 18:14.
  - Evidência: `AGENTS.md` criado com as restrições Java/XML/offline, regras de duração e sobreposição, política Room, escopo de arquivos, segredos e comandos de qualidade. Inspeção confirmou alinhamento à especificação e ao plano.
  - Commit: `cb2a77a` (`docs: add project agent guidance`).
  - Java + XML; não introduzir Kotlin/Compose.
  - Segundos inteiros para duração.
  - Sem rede, login ou permissões amplas.
  - Sem migração destrutiva.
  - Testes obrigatórios para regra de negócio.
  - Preservar alterações do usuário.
  - Agente não altera arquivo fora de seu escopo.

- [ ] **F00-03 — Criar configuração e agentes do Codex**
  - Em andamento por: /root — 2026-09-19 18:14. Escopo concluído: arquivos de configuração e quatro perfis de agentes.
  - Evidência parcial: `.codex/config.toml` habilita até três subagentes simultâneos; os perfis `android-architect`, `data-domain`, `feature-ui` e `qa-reviewer` definem os três campos obrigatórios e não fixam modelo. A validação por delegação em interface fica pendente.
  - Commit da configuração: `cb2a77a` (`docs: add project agent guidance`).
  - Criar `.codex/config.toml` e os quatro agentes previstos.
  - Fazer uma delegação curta e somente leitura para validar a configuração.

- [ ] **F00-04 — Resolver ADR-001: ambiguidades funcionais**
  - CSV exige seis colunas, mas um teste menciona observações. Recomendação: V1 com seis colunas, sem `note`; V2 poderá adicionar `Observação`.
  - Definir fórmula/época do identificador sequencial `Semana`.
  - Confirmar que alteração de meta afeta períodos históricos no MVP.
  - Confirmar cronômetro como parte do MVP ou melhoria pós-MVP.
  - Fixar uso do fuso local e intervalos semiabertos `[início, fim)`.

- [ ] **F00-05 — Resolver ADR-002: identidade e distribuição**
  - Escolher `applicationId` definitivo antes da primeira publicação.
  - Escolher conta pessoal ou organização.
  - Definir distribuição: somente local, teste interno, Play Store ou também fora da Play.
  - Definir responsável e armazenamento seguro da upload key.

- [ ] **F00-06 — Resolver ADR-003: biblioteca de gráficos**
  - Fazer spike de compatibilidade Java/API/Gradle da versão disponível de MPAndroidChart.
  - Confirmar origem confiável e licença.
  - Encapsular a biblioteca atrás de componentes próprios.
  - Se não estiver saudável, comparar alternativa antes de implementar telas.

**Gate F00:** decisões registradas, agentes configurados e primeiro commit criado.

## 8. Fase F01 — bootstrap Android

- [ ] **F01-01 — Gerar projeto**
  - Aplicativo Android nativo, Java, Views/XML.
  - `minSdk 26`.
  - Baseline verificada em 2026-09-19: AGP 9.4, Gradle Wrapper 9.6, JDK/JBR 17 para o build e API máxima 37.
  - Antes de gerar o projeto, revalidar se essa combinação continua estável e suportada; atualizar o baseline somente como conjunto compatível.
  - `compileSdk` e `targetSdk` escolhidos a partir da versão estável e das exigências atuais do Google Play.
  - Gradle Wrapper versionado; nunca depender de Gradle global.
  - JDK 27 pode permanecer como Java global, mas não deve ser usado para executar o Gradle enquanto o Gradle/AGP não o suportarem oficialmente.

- [ ] **F01-02 — Configurar módulos e dependências**
  - Material Components, AppCompat e RecyclerView.
  - Room.
  - Lifecycle ViewModel/LiveData.
  - Navigation Component.
  - Testes JUnit e AndroidX Test.
  - `java.time`; com API mínima 26, validar se desugaring ainda é necessário para APIs efetivamente usadas.
  - Fixar explicitamente Java toolchain, `sourceCompatibility` e `targetCompatibility` em Java 17 para o código Android.
  - Não usar APIs ou preview features do Java 27 no aplicativo; a disponibilidade de APIs no Android é determinada por `compileSdk`, `minSdk` e desugaring, não pelo JDK global.
  - Não adicionar WorkManager sem tarefa postergável real.

- [ ] **F01-03 — Criar estrutura de pacotes**

```text
app/src/main/java/<pacote>/
  data/local/dao/
  data/local/entity/
  data/repository/
  domain/model/
  domain/service/
  ui/today/
  ui/goals/
  ui/daily/
  ui/weekly/
  ui/export/
  ui/settings/
  util/
```

- [ ] **F01-04 — Criar shell de navegação e tema**
  - Tema claro/escuro/sistema preparado.
  - Destinos podem começar como placeholders.
  - Nenhuma lógica de negócio nos fragments.

- [ ] **F01-05 — Verificar bootstrap**

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat test
.\gradlew.bat lintDebug
```

**Gate F01:** os três comandos passam e o app abre no emulador.

O gate também deve passar no Galaxy M62 conectado, e `gradlew --version` deve confirmar que o daemon usa JVM 17, não JDK 27.

## 9. Fase F02 — Room e persistência

- [ ] **F02-01 — Implementar entidades**
  - `CategoryEntity` com IDs estáveis.
  - `TimeEntryEntity` com FK para categoria.
  - `AppPreferenceEntity` chave/valor.
  - Índices e unicidade previstos na especificação.

- [ ] **F02-02 — Seed idempotente das 12 categorias**
  - Nomes, ordem, cores, tipo e basis points.
  - Total inicial esperado: 9.950 basis points.
  - Testar banco novo e reabertura sem duplicação.

- [ ] **F02-03 — Implementar DAOs**
  - CRUD de registros.
  - Listagem ordenada.
  - Filtros por período e categoria.
  - Consulta de sobreposição usando `start < novoFim AND end > novoInicio`.
  - Edição deve excluir o próprio ID da busca.

- [ ] **F02-04 — Implementar repositórios e transações**
  - DAO não deve ser chamado diretamente pela UI.
  - Validar duração e sobreposição na fronteira de persistência.
  - ExecutorService central e encerramento controlado.

- [ ] **F02-05 — Migrações**
  - Exportar schemas Room para diretório versionado.
  - Adicionar teste de migração inicial.
  - Proibir `fallbackToDestructiveMigration` em build publicado.

**Gate F02:** testes instrumentados provam seed, CRUD, índices, reabertura e bloqueio de sobreposição.

## 10. Fase F03 — domínio e cálculos

- [ ] **F03-01 — `PeriodSplitter`**
  - Dividir logicamente na meia-noite local.
  - Preservar registro original.
  - Evitar perda/duplicação de segundos.
  - Cobrir transições de fuso/horário de verão aplicáveis.

- [ ] **F03-02 — `GoalCalculator`**
  - Base de 24 h no dia completo e 168 h na semana completa conforme requisito.
  - Base exata em intervalo parcial.
  - `MINIMO`: realizado − meta.
  - `MAXIMO`: meta − realizado.
  - Manter valores reais, limitando apenas representação visual de progresso.

- [ ] **F03-03 — calendário semanal**
  - Domingo–sábado como padrão.
  - Primeiro dia configurável.
  - Número/identificador calculado, nunca digitado.
  - Aplicar a decisão do ADR sobre o campo CSV `Semana`.

- [ ] **F03-04 — agregações**
  - Resumo por dia, semana, categoria e período.
  - Horas não classificadas = duração do período menos duração registrada recortada.
  - Consultas não devem duplicar agregados em tabelas.

- [ ] **F03-05 — formatação**
  - Duração `HH:mm` e horas decimais somente na apresentação.
  - CSV sempre `HH:mm:ss`.
  - Datas internas independentes de texto localizado.

**Gate F03:** todos os casos essenciais de cálculo têm testes unitários determinísticos.

## 11. Fase F04 — tela Hoje e CRUD vertical

- [ ] **F04-01 — listar dia selecionado e navegar por datas**
- [ ] **F04-02 — formulário criar/editar**
- [ ] **F04-03 — duração em tempo real e mensagens de validação**
- [ ] **F04-04 — excluir com confirmação**
- [ ] **F04-05 — totais e resumo compacto por categoria**
- [ ] **F04-06 — alertas de lacuna, sobreposição e total defensivo acima de 24 h**
- [ ] **F04-07 — restauração após rotação/recriação**
- [ ] **F04-08 — teste de persistência após reiniciar app/aparelho**

**Gate F04:** criar, editar e excluir um registro atualiza imediatamente lista e resumo; cruzamento da meia-noite aparece corretamente em ambos os dias.

## 12. Onda paralela F05–F07

Congelar antes da delegação:

- modelos de período;
- modelos de resumo;
- contratos dos repositórios;
- componente/adaptador de gráfico;
- convenção de estados de UI.

### F05 — Metas

- [ ] **F05-01 — período dia/semana/personalizado**
- [ ] **F05-02 — meta, realizado, percentuais, desvios e situação**
- [ ] **F05-03 — horas registradas e não classificadas**
- [ ] **F05-04 — edição de target basis points e tipo**
- [ ] **F05-05 — aviso não bloqueante quando soma != 100%**
- [ ] **F05-06 — testes de ViewModel e UI**

### F06 — Evolução diária

- [ ] **F06-01 — filtros 7/30/90 dias e personalizado**
- [ ] **F06-02 — uma ou várias categorias**
- [ ] **F06-03 — gráfico de horas por dia**
- [ ] **F06-04 — gráfico de desvio acumulado e linha zero**
- [ ] **F06-05 — detalhe ao tocar e tabela acessível equivalente**
- [ ] **F06-06 — testes de transformação dos dados do gráfico**

### F07 — Semanal

- [ ] **F07-01 — lista de semanas recentes**
- [ ] **F07-02 — totais e percentuais por categoria**
- [ ] **F07-03 — detalhe com dias e registros**
- [ ] **F07-04 — barras Realizado versus Meta**
- [ ] **F07-05 — linha de desvio semanal**
- [ ] **F07-06 — filtros 4/8/12/26 semanas**
- [ ] **F07-07 — testes para semana completa e parcial**

**Gate da onda:** cada feature passa seus testes isolados; depois o orquestrador integra navegação/recursos e executa toda a suíte.

## 13. Fase F08 — dados, exportação e configurações

- [ ] **F08-01 — lista pesquisável de registros**
- [ ] **F08-02 — filtros por data/categoria e ordenação**
- [ ] **F08-03 — `CsvExporter` puro**
  - UTF-8 com BOM.
  - Seis cabeçalhos na ordem especificada.
  - Separador configurável.
  - Escape RFC 4180.
  - Registro original ao cruzar meia-noite.

- [ ] **F08-04 — Storage Access Framework**
  - Usuário escolhe destino.
  - Sem permissão ampla de armazenamento.
  - Nome sugerido com timestamp.

- [ ] **F08-05 — compartilhamento seguro**
  - Usar URI temporária/`FileProvider` somente se necessário.
  - Nunca expor caminho `file://`.

- [ ] **F08-06 — configurações**
  - Primeiro dia da semana.
  - Formato de duração.
  - Separador CSV.
  - Tema.
  - Versão.

- [ ] **F08-07 — homologar no Excel pt-BR**
  - Acentos.
  - Aspas.
  - Separadores.
  - Quebras de linha em qualquer campo textual previsto pelo ADR.
  - Colunas e horários preservados.

**Gate F08:** arquivo exportado é byte a byte compatível com as decisões e abre corretamente em Excel/Calc.

## 14. Fase F09 — cronômetro

Executar apenas se o ADR confirmar o recurso no MVP.

- [ ] **F09-01 — modelar estado de cronômetro separado de `TimeEntry` concluído**
- [ ] **F09-02 — iniciar agora e impedir segundo cronômetro**
- [ ] **F09-03 — encerrar e criar registro validado**
- [ ] **F09-04 — recuperar estado após processo morto/reinício**
- [ ] **F09-05 — tratar sobreposição descoberta no encerramento**
- [ ] **F09-06 — testes de relógio usando clock injetável**

**Gate F09:** nenhum registro incompleto corrompe relatórios; cronômetro sobrevive à recriação prevista.

## 15. Fase F10 — qualidade, acessibilidade e desempenho

- [ ] **F10-01 — suíte unitária completa**
- [ ] **F10-02 — Room e migrações instrumentadas**
- [ ] **F10-03 — testes de ViewModel**
- [ ] **F10-04 — testes UI dos fluxos críticos**
- [ ] **F10-05 — teste de cinco anos de dados sintéticos**
- [ ] **F10-06 — consultas e gráficos em até dois segundos no Galaxy M62**
- [ ] **F10-07 — lint sem erros e revisão de warnings**
- [ ] **F10-08 — contraste e tema escuro**
- [ ] **F10-09 — fonte ampliada e layouts sem corte**
- [ ] **F10-10 — content descriptions, foco e áreas de toque**
- [ ] **F10-11 — alternativa textual para gráficos**
- [ ] **F10-12 — inspeção de permissões e tráfego**
  - Confirmar ausência de permissão ampla de armazenamento.
  - Confirmar ausência de acesso à internet, exceto se uma dependência injustificadamente o introduzir.
  - Confirmar que dados só deixam o aparelho em exportação/compartilhamento iniciado pelo usuário.

- [ ] **F10-13 — matriz específica do Galaxy M62**
  - Executar CRUD, cruzamento da meia-noite, gráficos e exportação no aparelho físico.
  - Testar retrato e paisagem na tela FHD+ de 6,7 polegadas.
  - Testar tamanho de fonte padrão e ampliado.
  - Testar tema claro/escuro conforme suportado pela versão instalada do One UI.
  - Testar seletor de arquivos e compartilhamento CSV nos aplicativos disponíveis no aparelho.
  - Reiniciar o aparelho e confirmar persistência Room e recuperação do cronômetro, se implementado.
  - Registrar versão do Android, nível de API, versão do One UI, espaço livre e build do aplicativo usado no teste.

- [ ] **F10-14 — extremos de compatibilidade em emulador**
  - Executar smoke test no AVD API 26.
  - Executar suíte de UI no AVD do API estável mais recente.
  - Não aceitar um resultado apenas no Galaxy M62 como prova de compatibilidade com todo o intervalo suportado.

Comandos mínimos do gate:

```powershell
.\gradlew.bat clean test connectedDebugAndroidTest lintDebug assembleDebug
```

Executar tarefas separadamente se o emulador exigir diagnóstico mais claro.

### Matriz mínima de aparelhos

| Alvo | Papel | Testes obrigatórios |
|---|---|---|
| Samsung Galaxy M62 | Aparelho físico principal e referência de desempenho | Fluxos completos, persistência, CSV, gráficos, acessibilidade e reinício |
| AVD API 26 | Limite inferior do `minSdk` | Instalação, abertura, CRUD e cálculos críticos |
| AVD API estável mais recente | Compatibilidade futura imediata | Instalação, navegação, testes UI e comportamento do Storage Access Framework |

## 16. Fase F11 — release e publicação

### 16.1 Release local

- [ ] **F11-01 — revisar versão, nome, ícone e applicationId**
- [ ] **F11-02 — gerar upload key fora do repositório**
- [ ] **F11-03 — guardar keystore e senhas em backup seguro separado**
- [ ] **F11-04 — gerar APK/AAB release assinado**
- [ ] **F11-05 — instalar e executar release em aparelho real**
  - O aparelho obrigatório para este gate é o Galaxy M62 informado pelo proprietário.

### 16.2 Google Play, se aplicável

- [ ] **F11-06 — criar app no Play Console e reservar pacote**
- [ ] **F11-07 — aderir ao Play App Signing**
- [ ] **F11-08 — preencher ficha da loja e materiais gráficos**
- [ ] **F11-09 — preencher conteúdo, classificação, público, anúncios e Data safety**
- [ ] **F11-10 — publicar no teste interno**
- [ ] **F11-11 — executar teste fechado exigido para conta pessoal**
- [ ] **F11-12 — corrigir feedback e registrar evidências**
- [ ] **F11-13 — solicitar acesso à produção**
- [ ] **F11-14 — confirmar rollout e monitorar Android Vitals**

Segredos de assinatura nunca podem ser enviados ao chat, commitados ou registrados neste plano.

## 17. Matriz dos critérios de aceite

| ID | Critério | Tarefas | Estado | Evidência final |
|---|---|---|---|---|
| AC-01 | CRUD nas 12 categorias | F02, F04 | Pendente | — |
| AC-02 | Persistência offline após reinício | F02-05, F04-08 | Pendente | — |
| AC-03 | Metas para qualquer período | F03, F05 | Pendente | — |
| AC-04 | Evolução diária e desvio acumulado | F06 | Pendente | — |
| AC-05 | Resumo domingo–sábado | F03-03, F07 | Pendente | — |
| AC-06 | Gráfico semanal realizado/meta/desvio | F07 | Pendente | — |
| AC-07 | CSV com seis colunas válidas | F08 | Pendente | — |
| AC-08 | Bloqueio de sobreposição e divisão na meia-noite | F02-03, F03-01 | Pendente | — |
| AC-09 | Sem conexão, conta ou servidor | F10-12 | Pendente | — |

## 18. Ondas e dependências

```text
PRE -> F00 -> F01 -> F02 -> F03 -> F04
                                  |
                                  +--> F05 --+
                                  +--> F06 --+--> F08 --> F09 --> F10 --> F11
                                  +--> F07 --+
```

F05, F06 e F07 podem trabalhar em paralelo somente depois de contratos de domínio e gráficos congelados. F08 pode dividir exportação e configurações entre dois agentes. F10 começa com revisão somente leitura e só depois recebe autorização para correções.

## 19. Prompt inicial para o orquestrador

```text
Leia integralmente AGENTS.md, a especificação e
documentacao/planos/PLANO_IMPLEMENTACAO_Codex_VSCode.md.

Atue como orquestrador. Localize a primeira tarefa não concluída cujas
dependências estejam satisfeitas. Antes de editar, registre no plano o agente,
data/hora e escopo em andamento.

Use no máximo três subagentes simultâneos. Delegue apenas tarefas independentes
e informe para cada uma: ID, objetivo, requisitos, arquivos permitidos,
arquivos proibidos, testes e formato de retorno. Não permita edições concorrentes
nos arquivos compartilhados reservados ao orquestrador.

Ao integrar, revise o diff, rode o gate da fase e registre evidências. Marque uma
tarefa como concluída somente depois da validação. Crie um checkpoint Git ao
final de cada fase. Pare no gate e reporte bloqueios em vez de inventar decisões
de produto, credenciais ou dados legais.
```

## 20. Modelo de prompt para subagente

```text
Tarefa: <ID e título>
Objetivo: <resultado observável>
Requisitos: <seções da especificação>
Pré-condições: <tarefas concluídas>
Arquivos permitidos: <lista fechada de caminhos>
Arquivos proibidos: <lista, incluindo compartilhados>
Testes obrigatórios: <comandos/casos>

Não amplie o escopo, não adicione dependências e não altere contratos públicos
sem consultar o orquestrador. Preserve mudanças existentes.

Ao terminar, retorne:
1. resumo;
2. arquivos alterados;
3. testes e resultados;
4. decisões e riscos;
5. pendências para integração.
```

## 21. Registro de execução

Adicionar entradas no topo da tabela, sem apagar histórico.

| Data/hora | Agente | Tarefa | Resultado | Testes/evidência | Commit |
|---|---|---|---|---|---|
| 2026-09-19 18:14 | /root | F00-03 | Parcial | Configuração e quatro agentes TOML criados; falta validar uma delegação curta em interface | `cb2a77a` |
| 2026-09-19 18:14 | /root | F00-02 | Concluída | `AGENTS.md` criado com arquitetura, segurança, domínio e qualidade alinhados à especificação | `cb2a77a` |
| 2026-09-19 18:14 | /root | F00-01 | Concluída | `.gitignore` Android/Java/VS Code criado; documentação não é ignorada; remoto `origin/master` ausente | `0361f29` |
| 2026-09-19 18:14 | /root + proprietário | PRE-05 | Concluída | Proprietário confirmou uso ativo do Codex no VS Code; sessão leu a especificação e executou comandos no workspace | ainda não criado |
| 2026-09-19 18:14 | /root | PRE-04 | Concluída | Galaxy M62 autorizado pelo ADB como `device`; Samsung SM-M625F, Android 13/API 33, 1080x2400, 450 dpi; sem registrar identificador do aparelho | ainda não criado |
| 2026-09-19 18:14 | /root | PRE-04A | Concluída | AVDs Pixel 5 API 26 e API 37 com Google APIs criados; cada um apareceu como `device` no ADB | ainda não criado |
| 2026-09-19 18:14 | /root | PRE-03 | Concluída | SDK/ADB configurados no ambiente do usuário; JDK 17 isolado validado; `.gradle/config.properties` local configura `GRADLE_LOCAL_JAVA_HOME`; Gradle 9.7.1 confirmou JVM 17 | ainda não criado |
| 2026-09-19 18:14 | /root | PRE-02 | Concluída | API 37, Build Tools 36, Platform Tools, Emulator, Command-line Tools 23 e imagem Google APIs API 37 validados por `sdkmanager --list` e `adb version` | ainda não criado |
| 2026-09-19 18:14 | /root | PRE-04 e PRE-05 | Aguardam interação do proprietário | Galaxy M62 não conectado; confirmação visual da Sidebar/login/subagentes não disponível por terminal | ainda não criado |
| 2026-09-19 17:54 | /root | PRE-01A | Concluída | OpenJDK 27+35 validado por SHA-256; `java --version` e `javac --version` aprovados em novos terminais; JDK 21 preservado; `STUDIO_JDK` ausente | ainda não criado |
| 2026-09-19 17:43 | /root | PRE-01 | Concluída | Android Studio 2026.1.4 Patch 1 instalado; SDK Platform 37, Build Tools 36.0.0, Platform Tools e Emulator presentes; `adb version` aprovado | ainda não criado |
| 2026-09-19 17:40 | /root | PRE-01 | Android Studio 2026.1.4 Patch 1 instalado; Setup Wizard aberto para configuração do SDK | Instalador oficial baixado de Android Developers; SHA-256 validado; `studio64.exe` em execução com título `Android Studio Setup Wizard` | ainda não criado |
| — | — | — | Plano criado; implementação ainda não iniciada | Auditoria local registrada na seção 5 | — |

## 22. Fontes oficiais consultadas

Verificadas em 2026-09-19; requisitos de versões, contas e publicação devem ser conferidos novamente quando a etapa for executada.

- [Extensão do Codex para IDE](https://developers.openai.com/pt-BR/docs/codex/ide)
- [Subagentes no Codex](https://developers.openai.com/pt-BR/docs/agent-configuration/subagents)
- [Git worktrees no Codex](https://developers.openai.com/docs/environments/git-worktrees)
- [Instalar Android Studio](https://developer.android.com/studio/install)
- [Java SE 27 — especificações oficiais](https://docs.oracle.com/javase/specs/)
- [JDK 27 — nota oficial de lançamento](https://docs.oracle.com/en-us/iaas/releasenotes/java-management/jdk-27-release-note.htm)
- [Versões Java em builds Android](https://developer.android.com/build/jdks)
- [Compatibilidade do Android Gradle Plugin 9.4](https://developer.android.com/build/releases/agp-9-4-0-release-notes)
- [Compatibilidade Java do Gradle](https://docs.gradle.org/9.7.0/userguide/compatibility.html)
- [Especificações oficiais do Galaxy M62](https://www.samsung.com/id/press/product-news/galaxy-m62-performa-megang-banget-dengan-baterai-7000-mah/)
- [Ativar opções de desenvolvedor em aparelhos Galaxy](https://www.samsung.com/br/support/mobile-devices/como-ativar-e-para-que-serve-o-menu-opcoes-do-desenvolvedor-no-meu-galaxy/)
- [Criar conta Play Console](https://support.google.com/googleplay/android-developer/answer/6112435?hl=pt-BR)
- [Informações exigidas para conta Play Console](https://support.google.com/googleplay/android-developer/answer/13628312)
- [Teste exigido para novas contas pessoais](https://support.google.com/googleplay/android-developer/answer/14151465)
- [Verificação de desenvolvedor Android](https://developer.android.com/developer-verification)
- [Assinatura de aplicativos Android](https://developer.android.com/studio/publish/app-signing)
- [Preparar app para revisão](https://support.google.com/googleplay/android-developer/answer/9859455)
- [Formulário Data safety](https://support.google.com/googleplay/android-developer/answer/10787469)
