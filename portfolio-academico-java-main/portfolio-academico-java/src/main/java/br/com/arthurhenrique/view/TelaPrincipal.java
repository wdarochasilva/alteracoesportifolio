package br.com.arthurhenrique.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import br.com.arthurhenrique.api.Api;
import br.com.arthurhenrique.client.ApiClient;
import br.com.arthurhenrique.model.Projeto;
import br.com.arthurhenrique.service.ProjetoService;

// TO DO
//

public class TelaPrincipal extends JFrame {

    private ProjetoService service;
    private ApiClient apiClient;

    // campos de texto
    private JTextField campoNome;
    private JTextField campoDescricao;

    // tabela de opções
    private JComboBox<String> comboCategoria;
    private JComboBox<String> comboStatus;

    // tabela
    private DefaultTableModel modelo;
    private JTable tabela;

    // botões
    private JButton botaoCadastrar;
    private JButton botaoLimpar;
    private JButton botaoExcluir;
    private JButton botaoAlterar;

    private static final Color COR_FUNDO = new Color(244, 246, 248);
    private static final Color COR_TOPO = new Color(38, 50, 56);
    private static final Color COR_CARD = Color.WHITE;
    private static final Color COR_BORDA = new Color(220, 226, 232);
    private static final Color COR_TEXTO = new Color(33, 37, 41);
    private static final Color COR_MUTED = new Color(108, 117, 125);

    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_SECAO = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 13);

    public TelaPrincipal() throws Exception {
        // configurações da janela
        setTitle("Sistema de Projetos");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        add(criarCabecalho(), BorderLayout.NORTH);

        // chamada dos métodos criam os elementos da janela (modularizando as tarefas)

        service = new ProjetoService();
        apiClient = new ApiClient();

        /*
- os metodos estão sem nome pois como a classe atual herda de JFrame, os
- métodos do JFrame passam a ser
- dessa classe, sem a necessidade de instânciar um objeto JFrame
         */
        criarComponentes();
        criarEventos();

        try {
            service.carregar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + e.getMessage());
        }
        carregarTabela();
    }

    public void criarComponentes() {

        // definição e instanciação dos elementos da janela
        campoNome = new JTextField(30);

        campoDescricao = new JTextField(30);

        comboCategoria = new JComboBox<>();
        comboCategoria.addItem("Web");
        comboCategoria.addItem("Software");
        comboCategoria.addItem("Mobile");
        comboCategoria.addItem("Outro");

        comboStatus = new JComboBox<>();
        comboStatus.addItem("Planejado");
        comboStatus.addItem("Em desenvolvimento");
        comboStatus.addItem("Concluído");

        botaoCadastrar = new JButton("Cadastrar");
        botaoLimpar = new JButton("Limpar");
        botaoExcluir = new JButton("Exclur");
        botaoAlterar = new JButton("Alterar");

        modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Nome");
        modelo.addColumn("Categoria");
        modelo.addColumn("Status");
        tabela = new JTable(modelo);

        JScrollPane scrollTabela = new JScrollPane(tabela);

        estilizarCampo(campoNome);
        estilizarCampo(campoDescricao);
        estilizarCombo(comboCategoria);
        estilizarCombo(comboStatus);
        estilizarBotao(botaoCadastrar, new Color(33, 150, 243), Color.WHITE);
        estilizarBotao(botaoAlterar, new Color(255, 152, 0), Color.WHITE);
        estilizarBotao(botaoExcluir, new Color(220, 53, 69), Color.WHITE);
        estilizarBotao(botaoLimpar, new Color(108, 117, 125), Color.WHITE);
        estilizarTabela(tabela, scrollTabela);

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabela.getSelectedRow();
                if (linha != -1) {
                    Object nomeVal = modelo.getValueAt(linha, 1);
                    Object categoriaVal = modelo.getValueAt(linha, 2);
                    Object statusVal = modelo.getValueAt(linha, 3);

                    campoNome.setText(nomeVal != null ? nomeVal.toString() : "");
                    if (categoriaVal != null) {
                        comboCategoria.setSelectedItem(categoriaVal.toString());
                    }
                    if (statusVal != null) {
                        comboStatus.setSelectedItem(statusVal.toString());
                    }
                }
            }
        });

        // instanciação da janela e inserção dos componentes (em ordem)
        JPanel painelFormulario = new JPanel();
        painelFormulario.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
        painelFormulario.setBackground(COR_CARD);

        JLabel lblNome = new JLabel("Nome: ");
        lblNome.setFont(FONTE_PADRAO);
        lblNome.setForeground(COR_TEXTO);

        JLabel lblDescricao = new JLabel("Descrição: ");
        lblDescricao.setFont(FONTE_PADRAO);
        lblDescricao.setForeground(COR_TEXTO);

        JLabel lblCategoria = new JLabel("Categoria: ");
        lblCategoria.setFont(FONTE_PADRAO);
        lblCategoria.setForeground(COR_TEXTO);

        JLabel lblStatus = new JLabel("Status: ");
        lblStatus.setFont(FONTE_PADRAO);
        lblStatus.setForeground(COR_TEXTO);

        painelFormulario.add(lblNome);
        painelFormulario.add(campoNome);
        painelFormulario.add(lblDescricao);
        painelFormulario.add(campoDescricao);
        painelFormulario.add(lblCategoria);
        painelFormulario.add(comboCategoria);
        painelFormulario.add(lblStatus);
        painelFormulario.add(comboStatus);

        painelFormulario.add(botaoCadastrar);
        painelFormulario.add(botaoLimpar);
        painelFormulario.add(botaoExcluir);
        painelFormulario.add(botaoAlterar);

        // [POSIÇÃO 1] Aba Projetos com fundo COR_FUNDO, margem e dois cards (formulário + tabela)
        JPanel abaProjetos = new JPanel();
        abaProjetos.setLayout(new BoxLayout(abaProjetos, BoxLayout.Y_AXIS));
        abaProjetos.setBackground(COR_FUNDO);
        abaProjetos.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel cardFormulario = criarCard();
        cardFormulario.setLayout(new BorderLayout(10, 10));
        cardFormulario.add(painelFormulario, BorderLayout.CENTER);
        cardFormulario.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardFormulario.getPreferredSize().height));

        JPanel cardTabela = criarCard();
        cardTabela.setLayout(new BorderLayout(10, 10));
        cardTabela.add(scrollTabela, BorderLayout.CENTER);

        abaProjetos.add(cardFormulario);
        abaProjetos.add(Box.createVerticalStrut(16));
        abaProjetos.add(cardTabela);

        JLabel lblCategoriaWeb = new JLabel("Web: 0");
        JLabel lblCategoriaSoftware = new JLabel("Software: 0");
        JLabel lblCategoriaOutras = new JLabel("Outras categorias: 0");

        // [POSIÇÃO 2] Aba Categorias com fundo COR_FUNDO e margem externa
        JPanel abaCategorias = new JPanel();
        abaCategorias.setLayout(new BoxLayout(abaCategorias, BoxLayout.Y_AXIS));
        abaCategorias.setBackground(COR_FUNDO);
        abaCategorias.setBorder(new EmptyBorder(16, 20, 16, 20));
        abaCategorias.add(new JLabel("Resumo por Categoria"));
        abaCategorias.add(lblCategoriaWeb);
        abaCategorias.add(lblCategoriaSoftware);
        abaCategorias.add(lblCategoriaOutras);

        Runnable atualizarCategorias = () -> {
            int web = 0;
            int software = 0;
            int outras = 0;

            for (int i = 0; i < modelo.getRowCount(); i++) {
                Object catObj = modelo.getValueAt(i, 2);
                String categoria = catObj != null ? catObj.toString().trim() : "";

                if ("Web".equalsIgnoreCase(categoria)) {
                     web++;
                } else if ("Software".equalsIgnoreCase(categoria)) {
                    software++;
                } else if (!categoria.isEmpty()) {
                    outras++;
                }
            }

            lblCategoriaWeb.setText("Web: " + web);
            lblCategoriaSoftware.setText("Software: " + software);
            lblCategoriaOutras.setText("Outras categorias: " + outras);
        };

        JLabel lblTotalProjetos = new JLabel("Total de projetos: 0");
        JLabel lblStatusConcluido = new JLabel("Concluído: 0");
        JLabel lblStatusEmDesenvolvimento = new JLabel("Em desenvolvimento: 0");
        JLabel lblStatusPlanejado = new JLabel("Planejado: 0");

        // [POSIÇÃO 3] Aba Dashboard com fundo COR_FUNDO e margem externa
        JPanel abaDashboard = new JPanel();
        abaDashboard.setLayout(new BoxLayout(abaDashboard, BoxLayout.Y_AXIS));
        abaDashboard.setBackground(COR_FUNDO);
        abaDashboard.setBorder(new EmptyBorder(16, 20, 16, 20));
        abaDashboard.add(new JLabel("Indicadores do Sistema"));
        abaDashboard.add(lblTotalProjetos);
        abaDashboard.add(lblStatusConcluido);
        abaDashboard.add(lblStatusEmDesenvolvimento);
        abaDashboard.add(lblStatusPlanejado);

        Runnable atualizarDashboard = () -> {
            int total = modelo.getRowCount();
            int concluido = 0;
            int emDesenvolvimento = 0;
            int planejado = 0;

            for (int i = 0; i < total; i++) {
                Object statusObj = modelo.getValueAt(i, 3);
                String status = statusObj != null ? statusObj.toString().trim() : "";

                if ("Concluído".equalsIgnoreCase(status) || "Concluido".equalsIgnoreCase(status)) {
                    concluido++;
                } else if ("Em desenvolvimento".equalsIgnoreCase(status)) {
                    emDesenvolvimento++;
                } else if ("Planejado".equalsIgnoreCase(status)) {
                    planejado++;
                }
            }

            lblTotalProjetos.setText("Total de projetos: " + total);
            lblStatusConcluido.setText("Concluído: " + concluido);
            lblStatusEmDesenvolvimento.setText("Em desenvolvimento: " + emDesenvolvimento);
            lblStatusPlanejado.setText("Planejado: " + planejado);
        };

        // [POSIÇÃO 4] Aba Relatórios com fundo COR_FUNDO e margem externa
        JPanel abaRelatorios = new JPanel();
        abaRelatorios.setLayout(new BoxLayout(abaRelatorios, BoxLayout.Y_AXIS));
        abaRelatorios.setBackground(COR_FUNDO);
        abaRelatorios.setBorder(new EmptyBorder(16, 20, 16, 20));
        JButton botaoExportarCsv = new JButton("Exportar CSV");
        abaRelatorios.add(botaoExportarCsv);

        botaoExportarCsv.addActionListener(e -> {
            JFileChooser seletor = new JFileChooser();
            seletor.setSelectedFile(new java.io.File("relatorio_projetos.csv"));

            int resultado = seletor.showSaveDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                java.io.File arquivo = seletor.getSelectedFile();
                if (!arquivo.getName().toLowerCase().endsWith(".csv")) {
                    arquivo = new java.io.File(arquivo.getAbsolutePath() + ".csv");
                }

                try (java.io.PrintWriter pw = new java.io.PrintWriter(
                        new java.io.OutputStreamWriter(
                                new java.io.FileOutputStream(arquivo),
                                java.nio.charset.StandardCharsets.UTF_8))) {

                    for (int col = 0; col < modelo.getColumnCount(); col++) {
                        if (col > 0) {
                            pw.print(";");
                        }
                        pw.print(escaparCsv(modelo.getColumnName(col)));
                    }
                    pw.println();

                    for (int row = 0; row < modelo.getRowCount(); row++) {
                        for (int col = 0; col < modelo.getColumnCount(); col++) {
                            if (col > 0) {
                                pw.print(";");
                            }
                            Object valor = modelo.getValueAt(row, col);
                            pw.print(escaparCsv(valor != null ? valor.toString() : ""));
                        }
                        pw.println();
                    }

                    JOptionPane.showMessageDialog(
                            this,
                            "Relatório exportado com sucesso para:\n" + arquivo.getAbsolutePath(),
                            "Exportação concluída",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Erro ao exportar CSV: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Projetos", abaProjetos);
        abas.addTab("Categorias", abaCategorias);
        abas.addTab("Dashboard", abaDashboard);
        abas.addTab("Relatórios", abaRelatorios);

        abas.addChangeListener(e -> {
            if (abas.getSelectedComponent() == abaCategorias) {
                atualizarCategorias.run();
            } else if (abas.getSelectedComponent() == abaDashboard) {
                atualizarDashboard.run();
            }
        });

        // [POSIÇÃO 5] Painel principal/container com COR_FUNDO e montagem final mantendo o cabeçalho
        JPanel painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBackground(COR_FUNDO);
        painelConteudo.add(abas, BorderLayout.CENTER);

        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(criarCabecalho(), BorderLayout.NORTH);
        add(painelConteudo, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private String escaparCsv(String texto) {
        if (texto == null) {
            return "";
        }
        String escapado = texto.replace("\"", "\"\"");
        if (escapado.contains(";") || escapado.contains("\"") || escapado.contains("\n") || escapado.contains("\r")) {
            return "\"" + escapado + "\"";
        }
        return escapado;
    }

    public void criarEventos() {
        botaoCadastrar.addActionListener(e -> {
            try {
                cadastrar();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(this, "Erro: " + e1.getMessage());
            }
        });

        botaoLimpar.addActionListener(e -> limparFormulário());
        botaoExcluir.addActionListener(e -> excluirProjeto());
        botaoAlterar.addActionListener(e -> alterarProjeto());
    }

    public void limparFormulário() {
        campoNome.setText("");
        campoDescricao.setText("");
        comboCategoria.setSelectedIndex(0);
        comboStatus.setSelectedIndex(0);
        campoNome.requestFocus();
    }

    public void cadastrar() {
        try {
            int id = service.gerarId();
            String nome = campoNome.getText();
            String descricao = campoDescricao.getText();
            String categoria = comboCategoria.getSelectedItem().toString();
            String status = comboStatus.getSelectedItem().toString();

            if (nome.isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe o nome do projeto");
                return;
            }

            Projeto projeto = new Projeto(id, nome, descricao, categoria, status);
            if (!service.adicionar(projeto)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível cadastrar o projeto (verifique o nome ou ID)."
                );
                return;
            }

            service.salvar();

            JOptionPane.showMessageDialog(this, "Projeto Cadastrado: " + nome);

            limparFormulário();
            carregarTabela();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void excluirProjeto() {
    try {
        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada != -1) {
            int id = Integer.parseInt(modelo.getValueAt(linhaSelecionada, 0).toString());

            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Um projeto será deletado.\nEstá certo disso?",
                    "Aviso",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (option == 0) {
                apiClient.excluirProjeto(id);
                JOptionPane.showMessageDialog(
                        null,
                        "Projeto excluido com sucesso",
                        "Sucesso",
                        JOptionPane.PLAIN_MESSAGE
                );
                carregarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, selecione um projeto",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(
                this,
                "Erro ao excluir projeto: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

    public void alterarProjeto() {

        // pega o index da linha selecionada
        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada != -1) {
            try {
                int id = Integer.parseInt(modelo.getValueAt(linhaSelecionada, 0).toString());
                String nome = campoNome.getText();
                String descricao = campoDescricao.getText();
                String categoria = comboCategoria.getSelectedItem().toString();
                String status = comboStatus.getSelectedItem().toString();

                if (nome.isBlank()) {
                    JOptionPane.showMessageDialog(this, "Informe o nome do projeto", "Aviso",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int option = JOptionPane.showConfirmDialog(this, "Um projeto será alterado.\nEstá certo disso?",
                        "Aviso", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (option == 0) {
                    Projeto projetoAtualizado = new Projeto(id, nome, descricao, categoria, status);

                    apiClient.alterarProjeto(id, projetoAtualizado);
                    carregarTabela();

                    JOptionPane.showMessageDialog(this, "Projeto alterado com sucesso", "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    return;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Não foi possivel alterar o projeto", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um projeto", "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    public void carregarTabela() {
        modelo.setRowCount(0);
        for (Projeto p : service.listar()) {
            modelo.addRow(new Object[]{
                p.getId(),
                p.getNome(),
                p.getCategoria(),
                p.getStatus()
           });
        }
    }

    public void atualizarTabela(List<Projeto> projetos) {
        modelo.setRowCount(0);

        for (Projeto projeto : projetos) {
            modelo.addRow(
                    new Object[] {
                            projeto.getId(),
                            projeto.getNome(),
                            projeto.getCategoria(),
                            projeto.getStatus()
                    });
        }
    }

    public static void main(String[] args) throws Exception {
        /*
- -- essa parte parte precisa de tratamento de exceção, mas o try-catch não ta
- tendo efeito --
- SwingUtilities.invokeLater(() -> {
- Api.main(args);
- TelaPrincipal tela = new TelaPrincipal();
- tela.setVisible(true);
- });
         */

        Api.main(args);
        TelaPrincipal tela = new TelaPrincipal();
        tela.setVisible(true);

        // recarrega a tabela automaticamente a cada 5 segundos
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                tela.carregarTabela();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
    }

    private JPanel criarCard() {
        JPanel card = new JPanel();
        card.setBackground(COR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA, 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));
        return card;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(FONTE_PADRAO);
        campo.setForeground(COR_TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COR_BORDA, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private void estilizarCombo(JComboBox<?> combo) {
        combo.setFont(FONTE_PADRAO);
        combo.setForeground(COR_TEXTO);
        combo.setBackground(Color.WHITE);
    }

    private void estilizarBotao(JButton botao, Color fundo, Color texto) {
        botao.setFont(FONTE_BOTAO);
        botao.setBackground(fundo);
        botao.setForeground(texto);
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fundo.darker(), 1, true),
            new EmptyBorder(8, 16, 8, 16)
        ));
    }

    private JPanel criarCabecalho() {
        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setBackground(COR_TOPO);
        topo.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel titulo = new JLabel("Sistema de Projetos");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Gestão, indicadores e relatórios do portfólio");
        subtitulo.setFont(FONTE_SUBTITULO);
        subtitulo.setForeground(new Color(207, 216, 220));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        topo.add(titulo);
        topo.add(Box.createVerticalStrut(4));
        topo.add(subtitulo);
        return topo;
    }

    private void estilizarTabela(JTable tabela, JScrollPane scroll) {
        tabela.setFont(FONTE_PADRAO);
        tabela.setForeground(COR_TEXTO);
        tabela.setBackground(Color.WHITE);
        tabela.setRowHeight(28);
        tabela.setGridColor(COR_BORDA);
        tabela.setSelectionBackground(new Color(227, 242, 253));
        tabela.setSelectionForeground(COR_TEXTO);

        if (tabela.getTableHeader() != null) {
            tabela.getTableHeader().setFont(FONTE_BOTAO);
            tabela.getTableHeader().setBackground(new Color(248, 249, 250));
            tabela.getTableHeader().setForeground(COR_TEXTO);
        }

        if (scroll != null) {
            scroll.getViewport().setBackground(Color.WHITE);
            scroll.setBorder(BorderFactory.createLineBorder(COR_BORDA, 1, true));
        }
    }
}