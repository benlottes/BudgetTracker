import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import budgettracker.Transaction;
import budgettracker.UserAccount;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;


public class UIController implements Initializable {
    //The order these things are initilized in is: Constructor, @FXML loaded, then initilize() (Constructor can't access @FXML fields)
    //Controls from FXML, the variables are automatically assigned based on fx:id 
    @FXML
    private Button addMoney;
    @FXML
    private Button subMoney;
    @FXML
    private PieChart pieGraph;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Entertainment", 2),
                        new PieChart.Data("Food", 25),
                        new PieChart.Data("Transportation", 50),
                        new PieChart.Data("Home & Utilities", 3),
                        new PieChart.Data("Personal & Family Care", 3));
        pieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), " amount: ", data.pieValueProperty()
                        )
                )
        );
        pieGraph.getData().addAll(pieChartData);
    }

    @FXML
    private TextField item;
    @FXML
    private ChoiceBox<String> category;
    @FXML
    private TextField price;
    @FXML 
    private TableView<Transaction> transactionTable;
    @FXML
    public TableColumn<Transaction, Integer> itemCol;
    @FXML
    public TableColumn<Transaction, Double> priceCol;
    @FXML
    public TableColumn<Transaction, String> categoryCol;
    @FXML
    public TableColumn<Transaction, String> signCol;

    //Location is the location of FXML document, so sure we need it but it automacically gets loaded in
    @FXML
    private URL location;
    //This is a java object that can also be automatically loaded, but I'm not sure what it's for
    @FXML
    private ResourceBundle resources;

    private DecimalFormat moneyFormat;

    UserAccount account = new UserAccount();

    //public constructor, params must be empty
    //Even if it stays empty forever we cannot delete
    //Or it will fail to instatiate 
    public UIController(){
        moneyFormat  = new DecimalFormat("$##.00");
        moneyFormat.setRoundingMode(java.math.RoundingMode.UNNECESSARY);
    }

    //Function will be called when everything has loaded
    //Must be void, cannot have params
    @FXML
    private void initialize(){
        populateCategories();
        generatePriceFilter();
        formatTablePrice();
    }

    @FXML
    private void savePosCharge(){
        saveCharge('+');
    }

    @FXML
    private void saveNegCharge(){
        saveCharge('-');
    }
    
    @FXML
    private void saveCharge(char sign){
        String i = item.getText();
        Double p = Double.parseDouble(price.getText());
        String c = category.getValue();

        //store transaction in account
        account.newTransaction(i, p, c, sign);

        //save respective values to table
        itemCol.setCellValueFactory(new PropertyValueFactory<>("Item"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("Price"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("Category"));
        signCol.setCellValueFactory(new PropertyValueFactory<>("Sign"));

        transactionTable.setItems(account.getTransactions());
    }

    @FXML
    private void populateCategories(){
        String categories[] = { "choice 1", "choice 2", "choice 3", "choice 4", "choice 5" };
        category.setItems(FXCollections.observableArrayList(categories));
    }

    private void generatePriceFilter(){
        UnaryOperator<TextFormatter.Change> filter = c -> {
            if(Pattern.matches("[\\d]*[\\.]?[\\d]{0,2}", c.getControlNewText())){
                return c;
            }else{
                return null;
            }
        };
        TextFormatter<String> format = new TextFormatter<>(filter);
        price.setTextFormatter(format);
    }

    private void formatTablePrice(){
        priceCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double p, boolean empty){
                super.updateItem(p, empty);
                if(p == null){
                    setText(null);
                }else{
                    setText(moneyFormat.format(p.doubleValue()));
                }
            }
        });
    }
}
