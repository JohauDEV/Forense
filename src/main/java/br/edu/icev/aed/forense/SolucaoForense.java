package br.edu.icev.aed.forense;

import br.edu.icev.aed.forense.Alerta;
import br.edu.icev.aed.forense.AnaliseForenseAvancada;
import br.edu.icev.aed.forense.LogEntry;
import br.edu.icev.aed.forense.LogParser;
import java.io.IOException;
import java.util.*;

public class SolucaoForense implements AnaliseForenseAvancada {

    // ------------------------------------------------------------------------
    // DESAFIO 1
    // ------------------------------------------------------------------------
    @Override
    public Set<String> encontrarSessoesInvalidas(String caminhoArquivoCsv) throws IOException {
        List<LogEntry> logs = LogParser.lerLogs(caminhoArquivoCsv);
        Map<String, Stack<String>> mapa = new HashMap<>();
        Set<String> invalidas = new HashSet<>();

        for (LogEntry e : logs) {
            String user = e.getUserId();
            String session = e.getSessionId();
            String action = e.getActionType();

            mapa.putIfAbsent(user, new Stack<>());
            Stack<String> pilha = mapa.get(user);

            if (action.equalsIgnoreCase("LOGIN")) {
                if (!pilha.isEmpty()) invalidas.add(session);
                pilha.push(session);
            } else if (action.equalsIgnoreCase("LOGOUT")) {
                if (pilha.isEmpty() || !pilha.peek().equals(session)) {
                    invalidas.add(session);
                } else {
                    pilha.pop();
                }
            }
        }

        for (Stack<String> pilha : mapa.values()) {
            invalidas.addAll(pilha);
        }

        return invalidas;
    }

    // ------------------------------------------------------------------------
    // DESAFIO 2
    // ------------------------------------------------------------------------
    @Override
    public List<String> reconstruirLinhaTempo(String caminhoArquivoCsv, String sessionId) throws IOException {
        List<LogEntry> logs = LogParser.lerLogs(caminhoArquivoCsv);
        Queue<String> fila = new LinkedList<>();

        for (LogEntry e : logs) {
            if (e.getSessionId().equals(sessionId)) {
                fila.add(e.getActionType());
            }
        }

        return new ArrayList<>(fila);
    }

    // ------------------------------------------------------------------------
    // DESAFIO 3
    // ------------------------------------------------------------------------
    @Override
    public List<Alerta> priorizarAlertas(String caminhoArquivoCsv, int n) throws IOException {
        List<LogEntry> logs = LogParser.lerLogs(caminhoArquivoCsv);

        PriorityQueue<Alerta> pq = new PriorityQueue<>(
                Comparator.comparingInt(Alerta::getSeverityLevel).reversed()
        );

        for (LogEntry e : logs) {
            // cria uma instância "fake" de Alerta (como a classe é final sem construtor)
            Alerta alerta = new Alerta(e.getTimestamp(), e.getUserId(), e.getSessionId(), e.getActionType(), e.getTargetResource(), e.getSeverityLevel(), e.getBytesTransferred());
                
            pq.add(alerta);
        }

        List<Alerta> resultado = new ArrayList<>();
        for (int i = 0; i < n && !pq.isEmpty(); i++) {
            resultado.add(pq.poll());
        }

        return resultado;
    }

    // ------------------------------------------------------------------------
    // DESAFIO 4
    // ------------------------------------------------------------------------
    @Override
    public Map<Long, Long> encontrarPicosTransferencia(String caminhoArquivoCsv) throws IOException {
        List<LogEntry> logs = LogParser.lerLogs(caminhoArquivoCsv);
        Map<Long, Long> resultado = new HashMap<>();
        Stack<LogEntry> pilha = new Stack<>();

        ListIterator<LogEntry> it = logs.listIterator(logs.size());
        while (it.hasPrevious()) {
            LogEntry e = it.previous();
            long bytes = e.getBytesTransferred();

            while (!pilha.isEmpty() && pilha.peek().getBytesTransferred() <= bytes) {
                pilha.pop();
            }

            if (!pilha.isEmpty()) {
                resultado.put(e.getTimestamp(), pilha.peek().getTimestamp());
            }

            pilha.push(e);
        }

        return resultado;
    }

    // ------------------------------------------------------------------------
    // DESAFIO 5
    // ------------------------------------------------------------------------
    @Override
    public Optional<List<String>> rastrearContaminacao(String caminhoArquivoCsv, String recursoInicial, String recursoAlvo) throws IOException {
        List<LogEntry> logs = LogParser.lerLogs(caminhoArquivoCsv);
        Map<String, List<String>> grafo = new HashMap<>();

        Map<String, List<LogEntry>> porSessao = new HashMap<>();
        for (LogEntry e : logs) {
            porSessao.computeIfAbsent(e.getSessionId(), k -> new ArrayList<>()).add(e);
        }

        for (List<LogEntry> eventos : porSessao.values()) {
            for (int i = 0; i < eventos.size() - 1; i++) {
                String a = eventos.get(i).getTargetResource();
                String b = eventos.get(i + 1).getTargetResource();
                grafo.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
            }
        }

        Queue<String> fila = new LinkedList<>();
        Map<String, String> predecessor = new HashMap<>();
        Set<String> visitados = new HashSet<>();

        fila.add(recursoInicial);
        visitados.add(recursoInicial);

        while (!fila.isEmpty()) {
            String atual = fila.poll();
            if (atual.equals(recursoAlvo)) break;

            for (String viz : grafo.getOrDefault(atual, Collections.emptyList())) {
                if (!visitados.contains(viz)) {
                    visitados.add(viz);
                    predecessor.put(viz, atual);
                    fila.add(viz);
                }
            }
        }

        if (!predecessor.containsKey(recursoAlvo) && !recursoInicial.equals(recursoAlvo))
            return Optional.empty();

        List<String> caminho = new LinkedList<>();
        String passo = recursoAlvo;
        caminho.add(passo);
        while (predecessor.containsKey(passo)) {
            passo = predecessor.get(passo);
            caminho.add(0, passo);
        }

        return Optional.of(caminho);
    }
}
