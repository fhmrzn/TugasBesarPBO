package quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author fhmrz
 */
public class TambahSoalDosen extends javax.swing.JFrame {

    private DatabaseConnector dbConnector;
    private Question selectedQuestion;
    private String Question;
    private int kelasid;
    private int quizid;
    
    
    
    public TambahSoalDosen(int kelasid, int quizid) {
        initComponents();
        this.kelasid = kelasid;
        this.quizid = quizid;
        dbConnector = new DatabaseConnector();
        setupListeners();
    }
    
    public class DatabaseConnector {
    
        private static final String DB_URL = "jdbc:mysql://localhost:3306/quizdb";
        private static final String DB_USER = "root";
        private static final String DB_PASSWORD = "";

        private static final String GET_ALL_QUESTIONS_QUERY = "SELECT soalid, pertanyaan, opsia, opsib, opsic, opsid, jawabanbenar FROM soal WHERE kuisid = ? AND kelasid = ?";

        private static final String INSERT_QUESTION_QUERY = "INSERT INTO soal (nomorsoal, pertanyaan, opsia, opsib, opsic, opsid, jawabanbenar, kelasid, kuisid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";


        public List<Question> getAllQuestions(int quizid, int kelasid) {
            List<Question> questionList = new ArrayList<>();

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(GET_ALL_QUESTIONS_QUERY)) {

                statement.setInt(1, quizid);
                statement.setInt(2, kelasid);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("soalid");
                        String questionText = resultSet.getString("pertanyaan");
                        String answerOptions = String.join("|",
                                resultSet.getString("opsia"),
                                resultSet.getString("opsib"),
                                resultSet.getString("opsic"),
                                resultSet.getString("opsid"));
                        String correctAnswer = resultSet.getString("jawabanbenar");
                        int kelasId = resultSet.getInt("kelasid");
                        int quizId = resultSet.getInt("kuisid");
                         int nomorsoal = resultSet.getInt("nomorsoal");

                        Question question = new Question(id, questionText, answerOptions, correctAnswer, kelasId, quizId, nomorsoal);
                        questionList.add(question);
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace(); 
            }

            return questionList; 
        }

        public int insertQuestion(Question question) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement getMaxNumberStatement = connection.prepareStatement("SELECT MAX(nomorsoal) AS max_number FROM soal WHERE kuisid = ? AND kelasid = ?");
                 PreparedStatement insertStatement = connection.prepareStatement(INSERT_QUESTION_QUERY, Statement.RETURN_GENERATED_KEYS)) {
                
                getMaxNumberStatement.setInt(1, quizid);
                getMaxNumberStatement.setInt(2, kelasid);
                
                try (ResultSet maxNumberResult = getMaxNumberStatement.executeQuery()) {
                    if (maxNumberResult.next()) {
                        int maxNumber = maxNumberResult.getInt("max_number");
                        question.setNomorsoal(maxNumber + 1);
                    } else {
                        question.setNomorsoal(1);
                    }
                }
                insertStatement.setInt(1, question.getNomorsoal());
                insertStatement.setString(2, question.getQuestionText());
                String[] answerOptions = question.getAnswerOptions().split("\\|");
                insertStatement.setString(3, answerOptions[0]);
                insertStatement.setString(4, answerOptions[1]);
                insertStatement.setString(5, answerOptions[2]);
                insertStatement.setString(6, answerOptions[3]);
                insertStatement.setString(7, question.getCorrectAnswer());
                insertStatement.setInt(8, kelasid);
                insertStatement.setInt(9, quizid);

                int affectedRows = insertStatement.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating question failed, no rows affected.");
                }

                try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        question.setId(id);
                        return id;
                    } else {
                        throw new SQLException("Creating question failed, no ID obtained.");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace(); 
                return -1; 
            }
        }
    }
    
    private void setupListeners() {
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                addQuestion();
                }
            });
        jButton2.addActionListener(new ActionListener() {
                @Override
            public void actionPerformed(ActionEvent evt) {
                openEditSoalDosen();
                }
            });       
    }
     
    private void addQuestion() {
        String questionText = jTextField1.getText();
        String optionA = jTextField2.getText();
        String optionB = jTextField3.getText();
        String optionC = jTextField4.getText();
        String optionD = jTextField5.getText();
        String correctAnswer = jComboBox1.getSelectedItem().toString();

        if (questionText.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Question newQuestion = new Question(0, questionText, optionA + "|" + optionB + "|" + optionC + "|" + optionD, correctAnswer, kelasid, quizid, 0); // Nomorsoal will be set in the insertQuestion method

        int nomorsoal = dbConnector.insertQuestion(newQuestion);

        if (nomorsoal != -1) {
            newQuestion.setNomorsoal(nomorsoal); 
            newQuestion.setId(nomorsoal);
            clearFields();
        }
    }

    private void updateQuestion() {
        if (selectedQuestion != null) {
            selectedQuestion.setQuestionText(jTextField1.getText());
            String answerOptions = String.join("|", jTextField2.getText(), jTextField3.getText(), jTextField4.getText(), jTextField5.getText());
            selectedQuestion.setAnswerOptions(answerOptions);
            selectedQuestion.setCorrectAnswer(jComboBox1.getSelectedItem().toString());

            int id = dbConnector.insertQuestion(selectedQuestion);

            if (id != -1) {
                selectedQuestion.setId(id);
                clearFields();
            }
        }
    }
    
    private void clearFields() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jComboBox1.setSelectedIndex(0);
        selectedQuestion = null;
    }
    
    private void openEditSoalDosen() {
    
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TambahSoalDosen.this.dispose();
                new EditSoalDosen(kelasid, quizid).setVisible(true);
            }
        });
    }

    public static void main(String args[]) {

    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Tambah");

        jTextField1.setText("Isi pertanyaan disini");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.setText("Opsi A");

        jTextField3.setText("Opsi B");
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.setText("Opsi C");

        jTextField5.setText("Opsi D");
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Opsi A", "Opsi B", "Opsi C", "Opsi D" }));

        jButton2.setText("Back");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("TAMBAH SOAL");

        jLabel2.setText("Opsi yang benar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel1))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(96, 96, 96)
                            .addComponent(jLabel2))
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(119, 119, 119)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton2))
                .addGap(44, 44, 44)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables

}
