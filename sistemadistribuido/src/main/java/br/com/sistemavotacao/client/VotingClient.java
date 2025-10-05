package br.com.sistemavotacao.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe do Cliente de Votação - Modo "Terminal de Votação".
 *
 * Responsabilidades:
 * 1. Funcionar em um loop contínuo para registrar múltiplos eleitores.
 * 2. Para cada eleitor: conectar, identificar, registrar um único voto e desconectar.
 * 3. Encerrar o programa de forma limpa quando instruído.
 */
public class VotingClient {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        // O Scanner é criado uma vez, fora do loop principal
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Terminal de Votação Iniciado ---");

        // Loop principal: cada iteração representa uma sessão de votação para um eleitor
        while (true) {
            System.out.println("\n=============================================");
            System.out.print("Digite o nome do eleitor (ou 'sair' para encerrar): ");
            String clientName = scanner.nextLine();

            // Condição para encerrar o programa cliente
            if (clientName.equalsIgnoreCase("sair") || clientName.trim().isEmpty()) {
                break;
            }

            // O 'try-with-resources' garante que a conexão (Socket, etc.) seja
            // aberta para o eleitor e fechada automaticamente no final do bloco.
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Conectado ao servidor para '" + clientName + "'.");
                
                // 1. Envia o nome para o servidor se identificar
                out.println("IDENTIFY:" + clientName);

                // 2. Thread para ouvir a ÚNICA resposta de confirmação do servidor
                Thread serverListener = new Thread(() -> {
                    try {
                        String serverResponse = in.readLine();
                        if (serverResponse != null) {
                            System.out.println("\n[Servidor]: " + serverResponse);
                        }
                    } catch (IOException e) {
                        // Uma exceção aqui é normal quando o socket é fechado pelo cliente.
                    }
                });
                serverListener.start();
                
                // 3. Apresenta o menu de voto (sem loop interno)
                printVoteMenu(clientName);
                String choice = scanner.nextLine();

                if ("1".equals(choice) || "2".equals(choice)) {
                    System.out.print("Digite o número do candidato (10, 20, 30): ");
                    String candidateNumber = scanner.nextLine();
                    
                    String communicationType = "1".equals(choice) ? "SYNC" : "ASYNC";
                    System.out.println("Enviando voto (" + communicationType + ")...");
                    out.println("VOTE:" + communicationType + ":" + candidateNumber);
                    
                    // Pausa para dar tempo da mensagem de confirmação chegar antes de desconectar
                    Thread.sleep(1000); 
                    
                    System.out.println("Voto enviado. Encerrando sessão de '" + clientName + "'.");
                } else {
                    System.out.println("Opção inválida. A sessão de '" + clientName + "' será encerrada.");
                }

            } catch (IOException e) {
                System.err.println("ERRO: Não foi possível conectar ao servidor: " + e.getMessage());
                System.err.println("Aguarde um momento e tente o próximo eleitor.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread do cliente foi interrompida.");
            }
            // Fim do bloco 'try'. O socket e os streams são fechados, desconectando o cliente.
            // O loop 'while' continua para o próximo eleitor.
        }

        scanner.close();
        System.out.println("--- Terminal de Votação Encerrado ---");
    }

    private static void printVoteMenu(String name) {
        System.out.println("\n--- Opções para " + name + " ---");
        System.out.println("1. Votar (Modo Síncrono)");
        System.out.println("2. Votar (Modo Assíncrono)");
        System.out.print("Escolha uma opção: ");
    }
}