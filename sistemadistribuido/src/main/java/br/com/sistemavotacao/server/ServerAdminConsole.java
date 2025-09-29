package br.com.sistemavotacao.server;

import java.util.Scanner;

/**
 * Permite administrar o servidor (abrir/fechar votação, ver resultados)
 * pelo console, rodando em uma thread separada.
 */
public class ServerAdminConsole implements Runnable {

    private final VotingServer server;
    private final Scanner scanner;

    public ServerAdminConsole(VotingServer server) {
        this.server = server;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        printMenu();
        while (true) {
            System.out.print("Comando do admin> ");
            String command = scanner.nextLine().toLowerCase();
            switch (command) {
                case "abrir":
                    server.setVotingOpen(true);
                    break;
                case "fechar":
                    server.setVotingOpen(false);
                    break;
                case "resultados":
                    printResults();
                    break;
                case "menu":
                    printMenu();
                    break;
                case "sair":
                    System.out.println("Encerrando servidor... (ação manual)");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Comando inválido. Digite 'menu' para ver as opções.");
                    break;
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Console de Administração do Servidor ---");
        System.out.println("Comandos disponíveis:");
        System.out.println("  abrir      - Abre a sessão de votação");
        System.out.println("  fechar     - Fecha a sessão de votação");
        System.out.println("  resultados - Mostra a contagem de votos atual");
        System.out.println("  menu       - Mostra este menu de ajuda");
        System.out.println("  sair       - Encerra o servidor");
        System.out.println("-------------------------------------------");
    }
    
    private void printResults() {
        System.out.println("\n--- Resultados Parciais ---");
        server.getVoteCounts().forEach((candidate, count) -> {
            System.out.println("Candidato " + candidate + ": " + count + " voto(s)");
        });
        System.out.println("---------------------------");
    }
}
