import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mysql.jdbc.PreparedStatement;

public class Utils {

	static Scanner teclado = new Scanner(System.in);

	public static Connection conectar() {

		String CLASSE_DRIVER = "com.mysql.jdbc.Driver";
		String USUARIO = "geek";
		String SENHA = "university";
		String URL_SERVIDOR = "jdbc:mysql://localhost:3306/jmysql?useSSL=false";

		try {
			Class.forName(CLASSE_DRIVER);
			return DriverManager.getConnection(URL_SERVIDOR, USUARIO, SENHA);

		} catch (Exception ex) {

			if (ex instanceof ClassNotFoundException) {
				System.out.println("Verifique o Driver de conex�o.");
			} else {
				System.out.println("Verifique se o servidor est� ativo.");
			}
		}
		System.exit(-42);
		return null;
	}

	public static void desconectar(Connection conn) {

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("N�o foi poss�vel fechar a conex�o.");
				e.printStackTrace();
			}
		}

	}

	public static void listar() {
		String BUSCAR_TODOS = "SELECT * FROM produtos";

		try {
			Connection conn = conectar();
			PreparedStatement produtos = (PreparedStatement) conn.prepareStatement(BUSCAR_TODOS);
			ResultSet res = produtos.executeQuery();

			res.last();
			int quantidade = res.getRow();
			res.beforeFirst();

			if (quantidade > 0) {
				System.out.println("Listando produtos:");
				System.out.println("------------------");

				while (res.next()) {
					System.out.println("ID: " + res.getInt(1));
					System.out.println("Produto: " + res.getString(2));
					// ...
					System.out.println("Pre�o: " + res.getFloat(3));
					System.out.println("Estoque: " + res.getInt(4));
					System.out.println("----------------------");

				}
			} else {
				System.out.println("N�o existem produtos cadastradros.");
			}

			produtos.close();
			desconectar(conn);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro buscando produtos");

			System.exit(-42);
		}
	}

	public static void inserir() {
		System.out.println("Informe o nome do produto: ");
		String nome = teclado.nextLine();

		System.out.println("Informe o pre�o do produto: ");
		float preco = teclado.nextFloat();

		System.out.println("Informa a quantidade em estoque: ");
		int estoque = teclado.nextInt();

		String INSERIR = "INSERT INTO produtos (nome, preco, estoque)" + "VALUES (?, ?, ?)";

		try {
			Connection conn = conectar();
			PreparedStatement salvar = (PreparedStatement) conn.prepareStatement(INSERIR);

			salvar.setNString(1, nome);
			salvar.setFloat(2, preco);
			salvar.setInt(3, estoque);

			salvar.executeUpdate();
			salvar.close();

			desconectar(conn);
			System.out.println("O produto " + nome + " foi inserido com sucesso!");

		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao salvar o produto");
			System.exit(-42);

		}
	}

	public static void atualizar() {

		System.out.println("Informe o c�digo do produto: ");

		int id = Integer.parseInt(teclado.nextLine());

		String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id = ?";

		try {
			Connection conn = conectar();
			PreparedStatement produto = (PreparedStatement) conn.prepareStatement(BUSCAR_POR_ID);
			produto.setInt(1, id);
			ResultSet res = produto.executeQuery();

			res.last();
			int quantidade = res.getRow();
			res.beforeFirst();

			if (quantidade > 0) {
				System.out.println("Informe o nome do produto: ");
				String nome = teclado.nextLine();

				System.out.println("Informe o pre�o do produto: ");
				float preco = teclado.nextFloat();

				System.out.println("Informe a quantidade em estoque: ");
				int estoque = teclado.nextInt();

				String ATUALIZAR = "UPDATE produtos SET nome=?, preco=?, estoque=? WHERE id=?";
				PreparedStatement upd = (PreparedStatement) conn.prepareStatement(ATUALIZAR);

				upd.setNString(1, nome);
				upd.setFloat(2, preco);
				upd.setInt(3, estoque);
				upd.setInt(4, id);

				upd.executeUpdate();
				upd.close();
				desconectar(conn);
				System.out.println("O produto " + nome + " foi atualizado com sucesso.");

			} else {
				System.out.println("N�o existe produto com o ID informado.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao atualizar produto");
			System.exit(-42);
		}
	}

	public static void deletar() {

		System.out.println("Informe o c�digo do produto: ");
		int id = Integer.parseInt(teclado.nextLine());

		String DELETAR = "DELETE * FROM produtos WHERE id = ?";
		String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id = ?";

		try {
			Connection conn = conectar();
			PreparedStatement produto = (PreparedStatement) conn.prepareStatement(BUSCAR_POR_ID);
			produto.setInt(1, id);
			ResultSet res = produto.executeQuery();

			res.last();
			int qnt = res.getRow();
			res.beforeFirst();

			if (qnt > 0) {
				PreparedStatement del = (PreparedStatement) conn.prepareStatement(DELETAR);
				del.setInt(1, id);
				del.executeUpdate();
				del.close();
				desconectar(conn);

				System.out.println("Produto deletado com sucesso.");
			} else {
				System.out.println("N�o existe o produto com o id informado.");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Erro ao deletar o produto.");
			System.exit(-42);
		}
	}

	public static void menu() {
		System.out.println("====================== Gerenciamento de Produtos =====================");
		System.out.println("Selecione uma op��o: ");
		System.out.println("1 - Listar produtos.");
		System.out.println("2 - Inserir produtos.");
		System.out.println("3 - Atualizar produtos.");
		System.out.println("4 - Deletar produtos.");

		int opcao = Integer.parseInt(teclado.nextLine());
		if (opcao == 1) {
			listar();
		} else if (opcao == 2) {
			inserir();
		} else if (opcao == 3) {
			atualizar();
		} else if (opcao == 4) {
			deletar();
		} else {
			System.out.println("Op��o inv�lida.");
		}

	}

}
