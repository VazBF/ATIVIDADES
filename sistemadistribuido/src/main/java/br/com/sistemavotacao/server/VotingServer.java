package br.com.sistemavotacao.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Classe principal do Servidor de Votação.
 *
 * Responsabilidades:
 * 1. Iniciar o serviço e aguardar conexões de clientes em uma porta específica.
 * 2. Manter o estado da votação (aberta/fechada).
 * 3. Armazenar os candidatos e a contagem de votos de forma segura (thread-safe).
 * 4. Lidar com múltiplas conexões de clientes simultaneamente usando um pool de threads.
 * 5. Coordenar o acesso ao recurso de votação (mecanismo de coordenação).
 */
public class VotingServer {

    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 10; // Limite de conexões simultâneas

    // Mapa para armazenar os votos. ConcurrentHashMap é thread-safe.
    // Chave: Número do candidato, Valor: Contagem de votos.
    private final Map<Integer, Integer> voteCounts = new ConcurrentHashMap<>();

    // Estado da votação (controlado pelo servidor). 'volatile' garante visibilidade entre threads.
    private volatile boolean isVotingOpen = false;

    public VotingServer() {
        // Inicializa os candidatos com 0 votos
        voteCounts.put(10, 0); // Candidato 10
        voteCounts.put(20, 0); // Candidato 20
        voteCounts.put(30, 0); // Candidato 30
    }

    public void startServer() {
        // ExecutorService para gerenciar um pool de threads para os clientes
        ExecutorService clientPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor de Votação iniciado na porta " + PORT);
            System.out.println("Aguardando conexões de clientes...");
            
            // Inicia uma thread para o console de administração do servidor
            new Thread(new ServerAdminConsole(this)).start();

            while (true) {
                // Aceita uma nova conexão de cliente
                Socket clientSocket = serverSocket.accept();
                
                // A mensagem "Novo cliente conectado" foi MOVIDA para o ClientHandler
                // para que possa incluir o nome do cliente.
                
                // Cria um novo manipulador para o cliente e o submete ao pool de threads
                clientPool.execute(new ClientHandler(clientSocket, this));
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        } finally {
            clientPool.shutdown();
        }
    }

    // Método para registrar um voto. 'synchronized' para garantir atomicidade.
    public synchronized String registerVote(int candidateNumber) {
        if (!isVotingOpen) {
            return "ERRO: A votação está fechada.";
        }
        if (voteCounts.containsKey(candidateNumber)) {
            voteCounts.put(candidateNumber, voteCounts.get(candidateNumber) + 1);
            System.out.println("Voto registrado para o candidato " + candidateNumber);
            return "VOTO_CONFIRMADO: Seu voto para " + candidateNumber + " foi registrado.";
        } else {
            return "ERRO: Candidato inexistente.";
        }
    }
    
    // --- Getters e Setters para o estado da votação (coordenação) ---

    public boolean isVotingOpen() {
        return isVotingOpen;
    }

    public void setVotingOpen(boolean votingOpen) {
        this.isVotingOpen = votingOpen;
        String status = votingOpen ? "ABERTA" : "FECHADA";
        System.out.println("\n--- A VOTAÇÃO FOI " + status + " ---");
    }

    public Map<Integer, Integer> getVoteCounts() {
        return voteCounts;
    }

    public static void main(String[] args) {
        VotingServer server = new VotingServer();
        server.startServer();
    }
}