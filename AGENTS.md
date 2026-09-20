# Instruções do projeto

## Escopo e tecnologia

- Desenvolva exclusivamente o aplicativo Android nativo deste repositório em Java, com layouts Views/XML. Não introduza Kotlin nem Jetpack Compose.
- O MVP funciona 100% offline: não adicione login, backend, sincronização, Firebase, tráfego de rede ou permissões de internet.
- Use Room/SQLite para persistência, MVVM, `ViewModel`/`LiveData`, Navigation Component e `ExecutorService` para trabalho assíncrono.
- O `minSdk` será 26. Use `java.time` e Java 17 para o código do aplicativo; não use APIs do JDK 27 como APIs Android.

## Regras de domínio e dados

- Armazene duração como segundos inteiros; calcule duração, nunca use horas decimais para persistência.
- Bloqueie intervalos inválidos e sobrepostos. A regra de sobreposição é `start < novoFim AND end > novoInicio`.
- Intervalos que cruzam a meia-noite são válidos e só são divididos logicamente nas agregações.
- Não use migração destrutiva em Room em builds publicados.
- Dados só podem sair do aparelho por exportação ou compartilhamento iniciado explicitamente pelo usuário. Não solicite permissão ampla de armazenamento.

## Trabalho no repositório

- Leia a especificação, o plano de implementação e ADRs relevantes antes de alterar comportamento do produto.
- Preserve alterações existentes do usuário e mantenha cada mudança no escopo da tarefa solicitada.
- Não altere Gradle, `AndroidManifest.xml`, o schema Room, navegação, strings/tema globais ou o plano durante trabalho paralelo sem coordenação explícita.
- Não versione `local.properties`, `.gradle/`, keystores, senhas, caminhos pessoais ou artefatos de build.
- Atualize o plano somente com evidência real de compilação, teste ou inspeção; não marque tarefas concluídas apenas por criar arquivos.

## Git e sincronização

- Use Gitflow como convenção: `master` contém apenas marcos estáveis; `develop` integra o trabalho concluído; use `feature/<id>-<resumo>` para features, `release/<versão>` para preparação de release e `hotfix/<resumo>` para correções urgentes a partir de `master`.
- Não faça commits diretos em `master` durante desenvolvimento. Crie um commit local ao concluir cada etapa verificável e sincronize a branch de trabalho com `origin` depois da validação.
- Use mensagens Conventional Commits concisas. Quando Codex participar materialmente do commit, acrescente o trailer `Co-authored-by: Codex <noreply@openai.com>`.
- Não faça force-push, rebase de branch já publicada, alteração de remoto ou push de segredos sem solicitação explícita do proprietário.

## Qualidade

- Crie ou atualize testes unitários para regras de negócio e testes instrumentados para Room/UI quando a tarefa os exigir.
- Antes de concluir uma fase Android, execute os comandos aplicáveis: `./gradlew.bat test`, `./gradlew.bat lintDebug` e `./gradlew.bat assembleDebug`.
- Mantenha interface acessível: contraste adequado, suporte a fonte ampliada, foco e descrições de conteúdo em controles interativos.
