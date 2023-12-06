/**
 * Modified the ExpenseTrackerController Class to adhere to the observer design pattern 
 * (to make sure controller no longer updates the view directly to maintain the observer design pattern. model takes care of this directly)
 */
package controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.TransactionFilter;
import view.ExpenseTrackerView;

public class ExpenseTrackerController {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  /**
   * The Controller is applying the Strategy design pattern.
   * This is the has-a relationship with the Strategy class
   * being used in the applyFilter method.
   */
  private TransactionFilter filter;

  public ExpenseTrackerController(ExpenseTrackerModel model, ExpenseTrackerView view) {
    this.model = model;
    this.view = view;
    // For the MVC architecture pattern, the Observer design pattern is being
    // used to update the View after manipulating the Model.
    this.model.register(this.view);
  }

  public void setFilter(TransactionFilter filter) {
    // Sets the Strategy class being used in the applyFilter method.
    this.filter = filter;
  }

  public boolean addTransaction(double amount, String category) {
    if (!InputValidation.isValidAmount(amount)) {
      return false;
    }
    if (!InputValidation.isValidCategory(category)) {
      return false;
    }
    
    Transaction t = new Transaction(amount, category);
    model.addTransaction(t);
    //view.update(model);//controller no longer updates the view directly to maintain the observer design pattern. model takes care of this directly
    return true;
  }

  public void applyFilter() {
    //null check for filter
    if(filter!=null){
      // Use the Strategy class to perform the desired filtering
      List<Transaction> transactions = model.getTransactions();
      List<Transaction> filteredTransactions = filter.filter(transactions);
      List<Integer> rowIndexes = new ArrayList<>();
      for (Transaction t : filteredTransactions) {
        int rowIndex = transactions.indexOf(t);
        if (rowIndex != -1) {
          rowIndexes.add(rowIndex);
        }
      }
      model.setMatchedFilterIndices(rowIndexes);
      //view.update(model);////controller no longer updates the view directly to maintain the observer design pattern. model takes care of this directly
    }
    else{
      JOptionPane.showMessageDialog(view, "No filter applied");
      view.toFront();}

  }

  //for undoing any selected transaction
  public boolean undoTransaction(int rowIndex) {
    if (rowIndex >= 0 && rowIndex < model.getTransactions().size()) {
      Transaction removedTransaction = model.getTransactions().get(rowIndex);
      model.removeTransaction(removedTransaction);
      //view.update(model);//controller no longer updates the view directly to maintain the observer design pattern. model takes care of this directly
      // The undo was allowed.
      return true;
    }

    // The undo was disallowed.
    return false;
  }
}
