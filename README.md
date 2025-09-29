Sistema de Votação Distribuído Simples
Este projeto implementa um sistema de votação cliente-servidor básico utilizando Sockets em Java e gerenciado com Maven.

Arquitetura
O sistema é composto por duas partes principais:

VotingServer (Servidor):

Centraliza a lógica da votação, aguarda conexões e gerencia threads para múltiplos clientes.

Mantém a contagem de votos de forma segura.

Possui um console de administração para controlar o acesso (abrir/fechar) e visualizar resultados.

VotingClient (Cliente):

Conecta-se ao servidor e fornece uma interface de linha de comando para o usuário votar de forma síncrona ou assíncrona.

Mecanismo de Coordenação: Controle de Acesso
Um dos requisitos do projeto é um "mecanismo simples de coordenação". No nosso sistema, implementamos isso na forma de Controle de Acesso.

O que é?
Controle de Acesso, neste contexto, é a capacidade do nó coordenador (o nosso VotingServer) de permitir ou bloquear o acesso dos outros nós (os VotingClient) a um recurso compartilhado. O recurso compartilhado aqui é a "urna eletrônica", ou seja, a capacidade de registrar um voto.

Como funciona no projeto?

Estado Controlado: O servidor possui uma variável isVotingOpen que funciona como uma "chave". Por padrão, ela começa como false (votação fechada).

Comandos do Administrador: Pelo console do servidor, um administrador pode usar os comandos abrir e fechar para alterar o estado dessa variável para true ou false.

Verificação: Toda vez que um cliente tenta votar, a thread que o atende no servidor (ClientHandler) primeiro verifica o valor de isVotingOpen.

Se for true, o voto é processado normalmente.

Se for false, o servidor envia uma mensagem de volta ao cliente informando que "A votação está fechada", e o voto é rejeitado.

Essa é uma forma de coordenação centralizada e simples, que atende perfeitamente ao requisito do projeto.

Como Compilar e Executar (Método Garantido)
Este método separa a compilação da execução, que é a forma mais robusta e padrão de rodar aplicações Java.

1. Compile e Empacote o Projeto
Primeiro, vamos gerar o arquivo .jar que contém todo o seu código compilado. Execute este comando apenas uma vez (ou sempre que alterar o código):

mvn package


Se tudo ocorrer bem, você verá uma mensagem "BUILD SUCCESS" e um novo arquivo será criado em target/votacao-distribuida-1.0-SNAPSHOT.jar.

2. Execute o Servidor
Agora, para iniciar o servidor, use o comando java diretamente. Ele irá executar a classe principal a partir do JAR que acabamos de criar.

java -cp target/votacao-distribuida-1.0-SNAPSHOT.jar br.com.sistemavotacao.server.VotingServer


O servidor será iniciado. Lembre-se de digitar abrir no console dele para começar a votação.

3. Execute o Cliente
Em um novo terminal, execute o comando abaixo para iniciar um cliente. Você pode abrir vários terminais e rodar este comando em cada um para simular múltiplos eleitores.

java -cp target/votacao-distribuida-1.0-SNAPSHOT.jar br.com.sistemavotacao.client.VotingClient

