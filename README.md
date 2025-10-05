# Sistema de Votação Distribuído Simples
## 1. Visão Geral do Projeto
Este projeto foi desenvolvido para a disciplina de Sistemas Distribuídos e tem como objetivo a implementação de um sistema de votação básico. A aplicação explora conceitos fundamentais como comunicação via Sockets, concorrência com Threads e mecanismos de coordenação centralizada, como o controle de acesso a um recurso compartilhado.

## 2. Ferramentas e Tecnologias
Linguagem de Programação: Java

Gerenciador de Build e Dependências: Apache Maven

Tecnologia de Comunicação: Java Sockets API (para comunicação em rede TCP/IP)

Controle de Concorrência: Java Threads e ExecutorService

IDE de Desenvolvimento: VS Code

## 3. Arquitetura
A arquitetura escolhida foi a Cliente-Servidor, onde um nó central (Servidor) gerencia o estado da votação, enquanto múltiplos nós (Clientes) podem se conectar para registrar seus votos de forma síncrona ou assíncrona.

## 4. Partes
O sistema é composto por duas partes principais:

### VotingServer (Servidor):

Centraliza a lógica da votação, aguarda conexões e gerencia threads para múltiplos clientes.

Mantém a contagem de votos de forma segura.

Possui um console de administração para controlar o acesso (abrir/fechar) e visualizar resultados.

### VotingClient (Cliente):

Conecta-se ao servidor e fornece uma interface de linha de comando para o usuário votar de forma síncrona ou assíncrona.

## 5. Mecanismo de Coordenação: Controle de Acesso
Um dos requisitos do projeto é um "mecanismo simples de coordenação". No nosso sistema, implementamos isso na forma de Controle de Acesso.

### 5.1. O que é?
Controle de Acesso, neste contexto, é a capacidade do nó coordenador (o nosso VotingServer) de permitir ou bloquear o acesso dos outros nós (os VotingClient) a um recurso compartilhado. O recurso compartilhado aqui é a "urna eletrônica", ou seja, a capacidade de registrar um voto.

### 5.2. Como funciona no projeto?

Estado Controlado: O servidor possui uma variável isVotingOpen que funciona como uma "chave". Por padrão, ela começa como false (votação fechada).

Comandos do Administrador: Pelo console do servidor, um administrador pode usar os comandos abrir e fechar para alterar o estado dessa variável para true ou false.

Verificação: Toda vez que um cliente tenta votar, a thread que o atende no servidor (ClientHandler) primeiro verifica o valor de isVotingOpen.

Se for true, o voto é processado normalmente.

Se for false, o servidor envia uma mensagem de volta ao cliente informando que "A votação está fechada", e o voto é rejeitado.

Essa é uma forma de coordenação centralizada e simples.

## 6. Como Compilar e Executar (Método Garantido)
Este método separa a compilação da execução, que é a forma mais robusta e padrão de rodar aplicações Java.

### 6.1. Compile e Empacote o Projeto
Primeiro, vamos gerar o arquivo .jar que contém todo o seu código compilado. Execute este comando apenas uma vez (ou sempre que alterar o código):
```diff
mvn package
```
Se tudo ocorrer bem, você verá uma mensagem "BUILD SUCCESS" e um novo arquivo será criado em target/votacao-distribuida-1.0-SNAPSHOT.jar.

### 6.2. Execute o Servidor
Agora, para iniciar o servidor, use o comando java diretamente. Ele irá executar a classe principal a partir do JAR que acabamos de criar.
```diff
java -cp target/votacao-distribuida-1.0-SNAPSHOT.jar br.com.sistemavotacao.server.VotingServer
```
O servidor será iniciado. Lembre-se de digitar abrir no console dele para começar a votação.

### 6.3. Execute o Cliente
Em um novo terminal, execute o comando abaixo para iniciar um cliente. Você pode abrir vários terminais, no codigo limitado a 10, e rodar este comando em cada um para simular múltiplos eleitores.
```diff
java -cp target/votacao-distribuida-1.0-SNAPSHOT.jar br.com.sistemavotacao.client.VotingClient
```

ALUNOS: Arthur Renato Normando Vasconcelos, Bruno Vaz Ferreira
