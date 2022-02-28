package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class SincronizacaoReceita {

	private static final String NOVA_LINHA = "\n";
	private static final String AGENCIA = "agencia";
	private static final String SPLIT = ";";

	public static void main(String[] args) throws IOException {
		for (String string : args) {
			verificarArquivo(string);
		}
	}

	public static void verificarArquivo(String arquivo) throws IOException {
		System.out.println("SincronizacaoReceita.verificarArquivo - Start");
		StringBuffer csvReport = new StringBuffer();
		csvReport.append("agencia;conta;saldo;status;atualizado\n");

		try {
			FileReader fileReader = new FileReader(arquivo);
			BufferedReader reader = new BufferedReader(fileReader);
			Stream<String> linhas = reader.lines();

			linhas.forEach(linha -> {
				try {
					String[] split = linha.split(SPLIT);
					if (!AGENCIA.equals(split[0])) {

						boolean atualizarConta = atualizarConta(split[0], split[1].replace("-", ""),
								Double.parseDouble(split[2].replace(",", ".")), split[3]);

						csvReport.append(split[0] + SPLIT + split[1].replace("-", "") + SPLIT
								+ Double.parseDouble(split[2].replace(",", ".")) + SPLIT + split[3] + SPLIT
								+ atualizarConta + NOVA_LINHA);
					}
				} catch (RuntimeException | InterruptedException e) {
					throw new RuntimeException(
							"SincronizacaoReceita.verificarArquivo - Falha ao atualizar conta - Error: {}"
									+ e.getMessage() + e);
				}
			});
			gerarCSV(csvReport);
			fileReader.close();
			reader.close();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(
					"SincronizacaoReceita.verificarArquivo - Arquivo nao encontrado - Error: {}" + e.getMessage() + e);
		}
		System.out.println("SincronizacaoReceita.verificarArquivo - End");
	}

	public static boolean atualizarConta(String agencia, String conta, double saldo, String status)
			throws RuntimeException, InterruptedException {

		// Formato agencia: 0000
		if (agencia == null || agencia.length() != 4) {
			return false;
		}

		// Formato conta: 000000
		if (conta == null || conta.length() != 6) {
			return false;
		}

		// Tipos de status validos:
		List<String> tipos = new ArrayList<>();
		tipos.add("A");
		tipos.add("I");
		tipos.add("B");
		tipos.add("P");

		if (status == null || !tipos.contains(status)) {
			return false;
		}

		// Simula tempo de resposta do serviço (entre 1 e 5 segundos)
		long wait = Math.round(Math.random() * 4000) + 1000;
		Thread.sleep(wait);

		// Simula cenario de erro no serviço (0,1% de erro)
		long randomError = Math.round(Math.random() * 1000);
		if (randomError == 500) {
			throw new RuntimeException("Error");
		}

		return true;
	}

	public static void gerarCSV(StringBuffer result) {
		try {
			FileOutputStream arquivo = new FileOutputStream("Resposta.csv");
			byte[] contentInBytes = result.toString().getBytes();
			arquivo.write(contentInBytes);
			arquivo.flush();
			if (arquivo != null) {
				arquivo.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
