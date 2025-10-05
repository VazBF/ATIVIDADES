package br.com.sistemavotacao.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe do Cliente de Votação.
 * Responsabilidades:
 * 1. Conectar-se ao servidor de votação.
 * 2. Apresentar uma interface de usuário no console.
 * 3. Permitir que o usuário escolha entre comunicação síncrona e assíncrona.
 * 4. Enviar o voto para o servidor.
 * 5. Receber e exibir a resposta do servidor (no modo síncrono).
 */
public class VotingClient {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado ao servidor de votação.");

            // ---- ALTERAÇÃO INSERIDA AQUI ----
            // 1. Pede o nome do usuário primeiro
            System.out.print("Para começar, digite seu primeiro nome: ");
            String clientName = scanner.nextLine();
            
            // 2. Envia o nome para o servidor para identificação
            out.println("IDENTIFY:" + clientName);
            // ------------------------------------

            // Thread para ouvir mensagens do servidor de forma assíncrona (confirmações, status)
            Thread serverListener = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("\n[Servidor]: " + serverResponse);
                        System.out.print("Seu voto> "); // Reapresenta o prompt
                    }
                } catch (IOException e) {
                    System.out.println("\nConexão com o servidor perdida.");
                }
            });
            serverListener.start();

            while (true) {
                printMenu(clientName); // Passa o nome para o menu
                String choice = scanner.nextLine();

                if ("3".equals(choice)) {
                    break;
                }

                System.out.print("Digite o número do candidato (10, 20, 30): ");
                String candidateNumber = scanner.nextLine();

                switch (choice) {
                    case "1":
                        // Comunicação SÍNCRONA: Envia e espera a resposta
                        System.out.println("Enviando voto (Síncrono)...");
                        out.println("VOTE:SYNC:" + candidateNumber);
                        // A resposta será capturada pela thread 'serverListener'
                        break;
                    case "2":
                        // Comunicação ASSÍNCRONA: Apenas envia
                        System.out.println("Enviando voto (Assíncrono)... O programa não vai esperar a confirmação.");
                        out.println("VOTE:ASYNC:" + candidateNumber);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                        break;
                }

                // Pequena pausa para a resposta chegar
                Thread.sleep(500);
            }

        } catch (IOException e) {
            System.err.println("Não foi possível conectar ao servidor: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread do cliente interrompida.");
        }
        System.out.println("Encerrando cliente.");
    }

    private static void printMenu(String name) {
        System.out.println("\n--- Sistema de Votação (Olá, " + name + "!) ---");
        System.out.println("1. Votar (Modo Síncrono)");
        System.out.println("2. Votar (Modo Assíncrono)");
        System.out.println("3. Sair");
        System.out.print("Escolha uma opção: ");
    }
}