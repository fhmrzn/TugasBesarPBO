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
public class EditSoalDosen extends javax.swing.JFrame {

    private DatabaseConnector dbConnector;
    private Question selectedQuestion;
    private String Question;
    
    public EditSoalDosen() {
        initComponents();
        dbConnector = new DatabaseConnector();
        setupListeners();
        loadQuestions();
    }
    
    public class DatabaseConnector {
    // Your database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quizdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // SQL query to retrieve all questions
    private static final String GET_ALL_QUESTIONS_QUERY = "SELECT soalid, pertanyaan, opsia, opsib, opsic, opsid, jawabanbenar FROM soal";

     // SQL query to update a question
    private static final String UPDATE_QUESTION_QUERY = "UPDATE quiz SET pertanyaan = ?, opsia = ?, opsib = ?, opsic = ?, opsid = ?, jawabanbenar = ? WHERE soalid = ?";

    // Method to get all questions from the database
    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(GET_ALL_QUESTIONS_QUERY);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("soalid");
                String questionText = resultSet.getString("pertanyaan");
                String answerOptions = String.join("|", 
                        resultSet.getString("opsia"), 
                        resultSet.getString("opsib"), 
                        resultSet.getString("opsic"), 
                        resultSet.getString("opsid"));
                String correctAnswer = resultSet.getString("jawabanbenar");

                Question question = new Question(id, questionText, answerOptions, correctAnswer);
                questionList.add(question);
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Handle the exception appropriately
        }

        return questionList;
    }
    public void updateQuestion(Question question) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUESTION_QUERY)) {

            statement.setString(1, question.getQuestionText());
            String[] answerOptions = question.getAnswerOptions().split("\\|");
            statement.setString(2, answerOptions[0]);
            statement.setString(3, answerOptions[1]);
            statement.setString(4, answerOptions[2]);
            statement.setString(5, answerOptions[3]);
            statement.setString(6, question.getCorrectAnswer());
            statement.setInt(7, question.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();  // Handle the exception appropriately
        }
    }
    }
    
      private void setupListeners() {
        jList1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                loadSelectedQuestion();
            }
        });

        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                updateQuestion();
            }
        });
    }

    private void loadQuestions() {
        List<Question> questionList = dbConnector.getAllQuestions();
        DefaultListModel<String> model = new DefaultListModel<>();

        for (Question question : questionList) {
            model.addElement(String.valueOf(question.getId()));  // Convert Question to String
        }
        jList1.setModel(model);
    }

    private void loadSelectedQuestion() {
        int selectedIndex = jList1.getSelectedIndex();

        if (selectedIndex != -1) {
            selectedQuestion = dbConnector.getAllQuestions().get(selectedIndex);

            jTextField1.setText(selectedQuestion.getQuestionText());
            String[] answerOptions = selectedQuestion.getAnswerOptions().split("\\|");

            if (answerOptions.length >= 4) {
                jTextField2.setText(answerOptions[0]);
                jTextField3.setText(answerOptions[1]);
                jTextField4.setText(answerOptions[2]);
                jTextField5.setText(answerOptions[3]);
                jComboBox1.setSelectedItem(selectedQuestion.getCorrectAnswer());
            }
        }
    }

    private void updateQuestion() {
        if (selectedQuestion != null) {
            selectedQuestion.setQuestionText(jTextField1.getText());
            String answerOptions = String.join("|", jTextField2.getText(), jTextField3.getText(), jTextField4.getText(), jTextField5.getText());
            selectedQuestion.setAnswerOptions(answerOptions);
            selectedQuestion.setCorrectAnswer(jComboBox1.getSelectedItem().toString());
            dbConnector.updateQuestion(selectedQuestion);
            loadQuestions();
            clearFields();
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

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditSoalDosen().setVisible(true);
            }
        });
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jList1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jList1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "1", "2", "3", "4", "5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jButton1.setText("Edit");

        jTextField1.setText("Pertanyaan");
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton1)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jTextField5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(42, 42, 42))
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
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables

}

class Question {
    private int id;
    private String questionText;
    private String answerOptions;
    private String correctAnswer;

    public Question(int id, String questionText, String answerOptions, String correctAnswer) {
        this.id = id;
        this.questionText = questionText;
        this.answerOptions = answerOptions;
        this.correctAnswer = correctAnswer;
    }

    public int getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(String answerOptions) {
        this.answerOptions = answerOptions;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public String toString() {
        return "Question " + id;
    }
}