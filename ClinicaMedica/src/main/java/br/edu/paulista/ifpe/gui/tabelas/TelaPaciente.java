package br.edu.paulista.ifpe.gui.tabelas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;

import br.edu.paulista.ifpe.core.cadastros.CadastroPacienteListener;
import br.edu.paulista.ifpe.data.PacienteDAO;
import br.edu.paulista.ifpe.gui.Home;
import br.edu.paulista.ifpe.gui.componentesCustomizados.PainelAcao;
import br.edu.paulista.ifpe.gui.componentesCustomizados.TabelaAcaoCellEditor;
import br.edu.paulista.ifpe.gui.componentesCustomizados.TableActionCellRender;
import br.edu.paulista.ifpe.gui.componentesCustomizados.TableActionEvent;
import br.edu.paulista.ifpe.gui.dialogos.DetalhesPacienteDialog;
import br.edu.paulista.ifpe.model.entidades.Paciente;
import br.edu.paulista.ifpe.model.tablemodel.PacienteTableModel;

@SuppressWarnings("serial")
public class TelaPaciente extends JPanel implements CadastroPacienteListener {
    private JScrollPane scrollPane;
    protected JTable tabela;
    @SuppressWarnings("unused")
	private Home home;
    private PacienteTableModel pacienteTableModel = new PacienteTableModel();

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TelaPaciente frame = new TelaPaciente();
                    frame.atualizar();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Não foi possível exibir os pacientes", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public TelaPaciente() {
        this.home = null;

        setBounds(100, 100, 800, 500);
        setLayout(new BorderLayout());

        scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(800, 500));
        add(scrollPane, BorderLayout.CENTER);

        tabela = new JTable();
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setBorder(new LineBorder(new Color(0, 0, 0)));
        tabela.setModel(pacienteTableModel);
        tabela.setFont(new Font("Arial", Font.PLAIN, 12));
        tabela.setRowHeight(40);
        scrollPane.setViewportView(tabela);
        PacienteDAO dao = new PacienteDAO();
		TableActionEvent evento = new TableActionEvent() {
			
			@Override
			public void onView(int linha) {
				int selectedRow = tabela.getSelectedRow();
			    if (selectedRow >= 0) {
			        PacienteTableModel model = (PacienteTableModel) tabela.getModel();
			        Paciente paciente = model.getPaciente(selectedRow);
			        int id = Integer.parseInt(paciente.getId());
			        try {
			            int i = JOptionPane.showConfirmDialog(null, "Deseja ver detalhes do paciente selecionado?");
			            if (i == JOptionPane.YES_OPTION) {
			            	Paciente pacienteComDetalhes = dao.buscarTodos(id);
			                DetalhesPacienteDialog detalhesDialog = new DetalhesPacienteDialog(pacienteComDetalhes);
			                model.fireTableDataChanged();
			                detalhesDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			                detalhesDialog.setLocationRelativeTo(null);
			                detalhesDialog.setVisible(true);			                
			            }
			        } catch (Exception ex) {
			            JOptionPane.showMessageDialog(null, "Erro ao exibir detalhes do Paciente", "Erro",
			                    JOptionPane.ERROR_MESSAGE);
			        }
			    } else {
			        JOptionPane.showMessageDialog(null, "Selecione um Paciente antes de visualizar.");
			    }
			}
			    
			
			@Override
			public void onEdit(int linha) {
			}
			
			@Override
			public void onDelete(int linha) {
			    int selectedRow = tabela.getSelectedRow();
			    if (selectedRow >= 0) {
			        PacienteTableModel model = (PacienteTableModel) tabela.getModel();
			        Paciente paciente = model.getPaciente(selectedRow);

			        try {
			            int i = JOptionPane.showConfirmDialog(null, "Deseja excluir o paciente selecionado?");
			            if (i == JOptionPane.YES_OPTION) {
			                boolean exclusaoBemSucedida = dao.excluir(paciente);
			                if (exclusaoBemSucedida) {
			                	JOptionPane.showMessageDialog(null, "Você excluiu o paciente com sucesso");
			                    model.removePacienteAt(selectedRow);
			                    // Atualizar a tabela
			                    model.fireTableDataChanged();
			                }
			            } else if (i == JOptionPane.NO_OPTION) {
			                JOptionPane.showMessageDialog(null, "Você cancelou a exclusão com sucesso");
			            }

			        } catch (Exception ex) {
			            JOptionPane.showMessageDialog(null, "Erro ao excluir o Paciente", "Erro",
			                    JOptionPane.ERROR_MESSAGE);
			            
			        }
			    } else {
			        JOptionPane.showMessageDialog(null, "Selecione um Paciente antes de excluir.");
			    }
			}
	        
	    };
		tabela.getColumnModel().getColumn(6).setCellRenderer(new TableActionCellRender());
		tabela.getColumnModel().getColumn(6).setCellEditor(new TabelaAcaoCellEditor(tabela, evento));
    }

    public JTable getTabela() {
        return tabela;
    }

    public TableModel getModeloTabela() {
        return tabela.getModel();
    }

    public void atualizar() {
        try {
            PacienteDAO dao = new PacienteDAO();
            List<Paciente> lista = dao.buscar(new Paciente());

            PacienteTableModel modelo = (PacienteTableModel) tabela.getModel();

            modelo.limpar();

            modelo.adicionar(lista);
            for (int i = 0; i < modelo.getRowCount(); i++) {
	            @SuppressWarnings("rawtypes")
				PainelAcao painelAcao = new PainelAcao();
	            Paciente paciente = lista.get(i);
	            painelAcao.setIdPaciente(paciente.getId());
	            modelo.setValueAt(painelAcao, i, 6);
	        }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao tentar buscar um Paciente");
        }
    }
    @Override
    public void pacienteCadastrado() {
        atualizar();
    }
    

    public void setHome(Home home) {
        this.home = home;
    }
}