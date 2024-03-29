package br.edu.paulista.ifpe.gui.tabelas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;

import br.edu.paulista.ifpe.core.cadastros.CadastroMedicoListener;
import br.edu.paulista.ifpe.data.MedicoDAO;
import br.edu.paulista.ifpe.gui.componentesCustomizados.PainelAcao;
import br.edu.paulista.ifpe.gui.componentesCustomizados.TabelaAcaoCellEditor;
import br.edu.paulista.ifpe.gui.componentesCustomizados.TableActionCellRender;
import br.edu.paulista.ifpe.gui.componentesCustomizados.TableActionEvent;
import br.edu.paulista.ifpe.model.entidades.Medico;
import br.edu.paulista.ifpe.model.tablemodel.MedicTableModel;

@SuppressWarnings("serial")
public class TelaMedico extends JPanel implements CadastroMedicoListener {

	private JScrollPane scrollPane;
	private JTable tabela;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaMedico frame = new TelaMedico();
					frame.atualizar();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Não foi possível exibir os médicos", "Erro",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	public TelaMedico() {
		setBounds(100, 100, 800, 500);
		setLayout(new BorderLayout());

		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(800, 500));
		add(scrollPane, BorderLayout.CENTER);

		tabela = new JTable();
		tabela.getTableHeader().setReorderingAllowed(false);
		tabela.setBorder(new LineBorder(new Color(0, 0, 0)));
		tabela.setModel(new MedicTableModel());
		tabela.setFont(new Font("Arial", Font.PLAIN, 12));
		tabela.setRowHeight(40);
		scrollPane.setViewportView(tabela);
		MedicoDAO dao = new MedicoDAO();
		TableActionEvent evento = new TableActionEvent() {

			@Override
			public void onView(int linha) {
				JOptionPane.showMessageDialog(null, "Todos os dados já estão aqui!");
			}

			@Override
			public void onEdit(int linha) {
			}

			@Override
			public void onDelete(int linha) {
				int selectedRow = tabela.getSelectedRow();
				if (selectedRow >= 0) {
					MedicTableModel model = (MedicTableModel) tabela.getModel();
					Medico medico = model.getMedico(selectedRow);

					try {
						int i = JOptionPane.showConfirmDialog(null, "Deseja excluir o médico selecionado?");
						if (i == JOptionPane.YES_OPTION) {
							boolean exclusaoBemSucedida = dao.excluir(medico);
							if (exclusaoBemSucedida) {
								JOptionPane.showMessageDialog(null, "Você excluiu o médico com sucesso");
								model.removeMedicoAt(selectedRow);
								// Atualizar a tabela
								model.fireTableDataChanged();
							}
						} else if (i == JOptionPane.NO_OPTION) {
							JOptionPane.showMessageDialog(null, "Você cancelou a exclusão com sucesso");
						}

					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "Erro ao excluir o médico", "Erro",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Selecione um médico antes de excluir.");
				}
			}

		};
		tabela.getColumnModel().getColumn(6).setCellRenderer(new TableActionCellRender());
		tabela.getColumnModel().getColumn(6).setCellEditor(new TabelaAcaoCellEditor(tabela, evento));

	}

	@Override
	public void medicoCadastrado() {
		atualizar();
	}

	public JTable getTabela() {
		return tabela;
	}

	public TableModel getModeloTabela() {
		return tabela.getModel();
	}

	@SuppressWarnings("rawtypes")
	public void atualizar() {
		try {
			MedicoDAO dao = new MedicoDAO();
			List<Medico> lista = dao.buscar(new Medico());

			MedicTableModel modelo = (MedicTableModel) tabela.getModel();
			modelo.limpar();
			modelo.adicionar(lista);

			for (int i = 0; i < modelo.getRowCount(); i++) {
				PainelAcao painelAcao = new PainelAcao();
				Medico medico = lista.get(i);
				painelAcao.setIdMedico(medico.getId());
				modelo.setValueAt(painelAcao, i, 6);
			}
			modelo.fireTableDataChanged();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Erro ao tentar buscar um Médico");
		}
	}

}
