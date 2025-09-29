package br.com.sistemavotacao.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Lida com a comunicação de um único cliente em uma thread separada.
 *
 * Responsabilidades:
 * 1. Ler as mensagens enviadas pelo cliente.
 * 2. Processar as requisições (votar, pedir status).
 * 3. Chamar os métodos apropriados no servidor principal.
 * 4. Enviar respostas de volta para o cliente.
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final VotingServer server;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, VotingServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Mensagem recebida de " + clientSocket.getInetAddress().getHostAddress() + ": " + inputLine);
                processMessage(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Cliente " + clientSocket.getInetAddress().getHostAddress() + " desconectado.");
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket do cliente: " + e.getMessage());
            }
        }
    }

    private void processMessage(String message) {
        String[] parts = message.split(":");
        String command = parts[0].toUpperCase();

        switch (command) {
            case "VOTE":
                handleVote(parts);
                break;
            case "STATUS":
                // Informa ao cliente se a votação está aberta ou fechada
                out.println(server.isVotingOpen() ? "STATUS:ABERTA" : "STATUS:FECHADA");
                break;
            default:
                out.println("ERRO: Comando desconhecido.");
                break;
        }
    }

    private void handleVote(String[] parts) {
        // Exemplo de mensagem: VOTE:SYNC:20 ou VOTE:ASYNC:20
        if (parts.length < 3) {
            out.println("ERRO: Formato de voto inválido.");
            return;
        }

        String communicationType = parts[1].toUpperCase();
        
        try {
            int candidateNumber = Integer.parseInt(parts[2]);
            String response = server.registerVote(candidateNumber);

            // Para comunicação síncrona, sempre enviamos a resposta.
            if ("SYNC".equals(communicationType)) {
                out.println(response);
            }
            // Para assíncrona, o cliente não espera resposta, mas o servidor pode enviar
            // uma notificação se desejar. Por simplicidade, aqui também respondemos,
            // mas o cliente escolherá se vai ler ou não.
            else if ("ASYNC".equals(communicationType)) {
                 out.println(response); // Opcional: o servidor pode notificar a confirmação
            }

        } catch (NumberFormatException e) {
            out.println("ERRO: Número do candidato inválido.");
        }
    }
}
