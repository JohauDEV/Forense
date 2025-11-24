import br.edu.icev.aed.forense.AnaliseForenseAvancada;
import br.edu.icev.aed.forense.SolucaoForense;
import java.util.Set;
import java.io.IOException;

public class TesteExecucao {
    public static void main(String[] args) {
        try {
            // Instanciar a classe de implementação
            AnaliseForenseAvancada impl = new SolucaoForense();
            
            // O arquivo CSV está no mesmo diretório
            String csvPath = "arquivo_logs.csv";
            
            System.out.println("Iniciando análise forense no arquivo: " + csvPath);
            
            // Chamando o método principal: Desafio 1
            Set<String> invalidas = impl.encontrarSessoesInvalidas(csvPath);
            
            System.out.println("Análise concluída.");
            System.out.println("Resultado do método encontrarSessoesInvalidas:");
            System.out.println("Total de sessões inválidas encontradas: " + invalidas.size());
            
            // Imprimir as sessões inválidas encontradas
            for (String sessao : invalidas) {
                System.out.println(sessao);
            }
            
        } catch (IOException e) {
            System.err.println("Erro de I/O ao ler o arquivo CSV:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro durante a execução:");
            e.printStackTrace();
        }
    }
}
