# ADR-002 — Identidade e distribuição

**Estado:** Aceito  
**Data:** 2026-09-19

## Contexto

O MVP é um aplicativo offline de uso pessoal e ainda não possui conta Play Console, entidade jurídica declarada ou material de assinatura de release. O `applicationId` deve permanecer estável desde o primeiro build distribuído.

## Decisões

### Identidade do aplicativo

O `applicationId` definitivo é:

```text
io.github.matosoe.controlehoras
```

O namespace usa a identidade pública já associada ao repositório GitHub. Ele não deve ser alterado após gerar um artefato de release ou registrar o aplicativo em uma loja.

### Conta e distribuição

Se for necessária publicação futura no Google Play, será usada uma conta de desenvolvedor **pessoal**, pois o aplicativo é de uso pessoal e não representa uma organização ou negócio.

Para o MVP, a distribuição está limitada ao desenvolvimento e homologação local no Galaxy M62 e AVDs, por Android Studio/ADB. Não haverá publicação em Play Console, teste externo ou distribuição a terceiros nesta etapa. Uma decisão posterior poderá abrir teste interno no Play depois de cumprir os requisitos da conta e da F11.

### Assinatura

Durante o desenvolvimento, usam-se somente artefatos debug. A upload key de release será gerada somente na F11.

O proprietário do projeto é o responsável pela upload key e pelas senhas. A keystore e suas senhas devem permanecer fora do repositório, em armazenamento criptografado e com backup seguro separado. Nenhum segredo será enviado ao chat, incluído no Git ou anotado neste ADR.

## Consequências

- O bootstrap Android deve usar exatamente `io.github.matosoe.controlehoras`.
- A F11 precisa criar a upload key e validar o artefato antes de qualquer distribuição além de ADB local.
- Antes de mudar para conta de organização ou publicar, revisar requisitos atuais do Play Console e de verificação do desenvolvedor.
