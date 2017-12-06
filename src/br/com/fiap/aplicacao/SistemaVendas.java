package br.com.fiap.aplicacao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import br.com.fiap.dao.GenericDao;
import br.com.fiap.entity.Clientes;
import br.com.fiap.entity.Item;
import br.com.fiap.entity.Pedidos;

public class SistemaVendas {

	public static void main(String[] args) {

		String[] choices = { "Incluir cliente", "Incluir pedido", "Listar clientes", "Listar pedidos", "Buscar cliente",
				"Buscar pedido" };
		String input = (String) JOptionPane.showInputDialog(null, "O que deseja fazer?", "Menu",
				JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

		switch (input) {
		case "Incluir cliente":
			incluirCliente();
			break;
		case "Incluir pedido":
			incluirPedido();
			break;

		case "Listar clientes":
			listarClientes();
			break;

		case "Listar pedidos":
			listarPedidos();
			break;

		case "Buscar cliente":
			buscarCliente();
			break;

		case "Buscar pedido":
			buscarPedido();
			break;

		default:
			break;
		}

		System.exit(1);
	}

	private static void incluirCliente() {
		try {
			Clientes cliente = new Clientes();
			cliente.setNome(JOptionPane.showInputDialog("Informe o nome do cliente:"));
			cliente.setEmail(JOptionPane.showInputDialog("Informe o email do cliente:"));

			Object[] options = { "Sim", "Não" };
			int i = JOptionPane.showOptionDialog(null, "Deseja incluir pedido para este cliente?", "Pedido",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			while (i == JOptionPane.YES_OPTION) {
				Pedidos pedido = incluirPedidoCliente(cliente);
				cliente.getPedidos().add(pedido);
				i = JOptionPane.showOptionDialog(null, "Deseja incluir outros pedido para este cliente?", "Pedido",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			}

			GenericDao<Clientes> dao = new GenericDao<>(Clientes.class);
			dao.adicionar(cliente);
			JOptionPane.showMessageDialog(null, "Inclusão efetuada com sucesso!");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Houve um erro ao incluir o cliente, tente novamente.");
			System.out.println(e.getMessage());
		}
	}

	private static Pedidos incluirPedidoCliente(Clientes cliente) {
		Pedidos pedido = new Pedidos();
		pedido.setCliente(cliente);
		try {
			String retorno = JOptionPane.showInputDialog("Informe a data do pedido:");
			Date data = new Date();
			data = new SimpleDateFormat("dd/MM/yyyy").parse(retorno);
			pedido.setData(data);
			pedido.setValor(Double.parseDouble(JOptionPane.showInputDialog("Informe o valor:")));
			incluirItem(pedido);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null, "Houve um erro ao incluir o pedido, tente novamente.");
			e1.printStackTrace();
		}
		return pedido;
	}

	private static void incluirPedido() {

		try {
			int idCliente = Integer.parseInt(JOptionPane.showInputDialog(null, "Informe o código do cliente:",
					"Incluir pedido", JOptionPane.PLAIN_MESSAGE));
			Clientes cliente = buscarCliente(idCliente);

			Pedidos pedido = new Pedidos();
			pedido.setCliente(cliente);
			String retorno = JOptionPane.showInputDialog("Informe a data do pedido:");
			Date data = new Date();
			data = new SimpleDateFormat("dd/MM/yyyy").parse(retorno);
			pedido.setData(data);
			pedido.setValor(Double.parseDouble(JOptionPane.showInputDialog("Informe o valor:")));

			incluirItem(pedido);

			GenericDao<Pedidos> dao = new GenericDao<>(Pedidos.class);
			dao.adicionar(pedido);
			JOptionPane.showMessageDialog(null, "Inclusão efetuada com sucesso!");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Houve um erro ao incluir o pedido, tente novamente.", "Atenção",
					JOptionPane.ERROR_MESSAGE);
			System.out.println(e.getMessage());
		}
	}

	private static void incluirItem(Pedidos pedido) {
		Object[] options = { "Sim", "Não" };
		int i;
		do {
			Item item = new Item();
			item.setDescricao(JOptionPane.showInputDialog(null, "Informe a descrição:", "Incluir Item",
					JOptionPane.PLAIN_MESSAGE));
			item.setQuantidade(Integer.parseInt(JOptionPane.showInputDialog(null, "Informe a quantidade:",
					"Incluir Item", JOptionPane.PLAIN_MESSAGE)));
			item.setPedido(pedido);
			pedido.getItens().add(item);

			i = JOptionPane.showOptionDialog(null, "Deseja incluir mais itens a este pedido?", "Pedido",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		} while (i == JOptionPane.YES_OPTION);
	}

	private static void listarClientes() {
		String mensagem = "";
		GenericDao<Clientes> dao = new GenericDao<>(Clientes.class);
		List<Clientes> clientes = dao.listar();
		if (clientes.size() > 0) {
			mensagem += "Foram encontrados " + clientes.size() + " cliente(s)";
			for (Clientes cliente : clientes) {
				mensagem += "\n Cliente " + cliente.getId() + " Nome: " + cliente.getNome() + " E-mail: "
						+ cliente.getEmail();
			}
		} else {
			mensagem += "\n Não foram encontrados clientes.";
		}
		JOptionPane.showMessageDialog(null, mensagem, "Lista de clientes", JOptionPane.PLAIN_MESSAGE);
	}

	private static Clientes buscarCliente(Integer idCliente) {
		System.out.println("Buscando cliente de código: " + idCliente);
		GenericDao<Clientes> dao = new GenericDao<>(Clientes.class);
		Clientes cliente = dao.buscar(idCliente);
		return cliente;
	}

	private static void buscarCliente() {
		int idCliente = Integer.parseInt(JOptionPane.showInputDialog("Informe o código do cliente:"));
		String mensagem = "";
		String titulo = "Cliente ";
		try {
			Clientes cliente = buscarCliente(idCliente);
			titulo += cliente.getId();
			mensagem += "Nome: " + cliente.getNome() + " E-mail: " + cliente.getEmail();
			if (cliente.getPedidos() != null && cliente.getPedidos().size() > 0) {
				mensagem += "\n Pedido(s) do cliente: ";
				for (Pedidos pedido : cliente.getPedidos()) {
					mensagem += "\n Código " + pedido.getId() + " Data: " + dataFormatada(pedido.getData()) + " Valor: "
							+ pedido.getValor();
				}
			}

		} catch (Exception e) {
			mensagem = "Cliente não encontrado.";
		}
		JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.PLAIN_MESSAGE);
	}

	private static void buscarPedido() {
		int idPedido = Integer.parseInt(JOptionPane.showInputDialog("Informe o código do pedido:"));
		GenericDao<Pedidos> dao = new GenericDao<>(Pedidos.class);
		String mensagem = "";
		String titulo = "Pedido ";
		try {
			Pedidos pedido = dao.buscar(idPedido);
			titulo += pedido.getId();
			mensagem += "\n Data: " + dataFormatada(pedido.getData()) + " Valor: " + pedido.getValor();
			if (pedido.getItens().size() > 0) {
				for (Item item : pedido.getItens()) {
					mensagem += "\n Item " + item.getDescricao() + " Quantidade: " + item.getQuantidade();
				}
			}

		} catch (Exception e) {
			mensagem = "Pedido não encontrado.";
		}
		JOptionPane.showMessageDialog(null, mensagem, titulo, JOptionPane.PLAIN_MESSAGE);

	}

	private static void listarPedidos() {
		String mensagem = "";
		GenericDao<Pedidos> dao = new GenericDao<>(Pedidos.class);
		List<Pedidos> pedidos = dao.listar();
		if (pedidos.size() > 0) {
			mensagem += "Foram encontrados " + pedidos.size() + " pedido(s)";
			for (Pedidos pedido : pedidos) {
				mensagem += "\n Pedido " + pedido.getId() + " Data: " + dataFormatada(pedido.getData()) + " Valor: "
						+ pedido.getValor();
			}
		} else {
			mensagem += "\n Não foram encontrados pedidos.";
		}
		JOptionPane.showMessageDialog(null, mensagem, "Lista de pedidos", JOptionPane.PLAIN_MESSAGE);
	}
	
	private static String dataFormatada(Date data){
		return new SimpleDateFormat("dd/MM/yyyy").format(data);
	}

}