package br.edu.icev.aed.forense;

import java.io.*;
import java.util.*;

/**
 * Faz a leitura do arquivo forensic_logs.csv e converte em lista de LogEntry.
 */
public class LogParser {

    public static List<LogEntry> lerLogs(String caminho) throws IOException {
        List<LogEntry> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha = br.readLine(); // pular cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] p = linha.split(",");
                if (p.length < 7) continue;

                long timestamp = Long.parseLong(p[0]);
                String userId = p[1];
                String sessionId = p[2];
                String actionType = p[3];
                String target = p[4];
                int severity = Integer.parseInt(p[5]);
                long bytes = Long.parseLong(p[6]);

                lista.add(new LogEntry(timestamp, userId, sessionId, actionType, target, severity, bytes));
            }
        }

        return lista;
    }
}
