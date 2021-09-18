package homework;


import java.util.Iterator;
import java.util.LinkedList;

public class CustomerReverseOrder {

    //todo: 2. надо реализовать методы этого класса

    LinkedList<Customer> list = new LinkedList<>();
    Iterator<Customer> descIterator;

    public void add(Customer customer) {
        list.add(customer);
    }

    public Customer take() {
        if (descIterator == null) {
            descIterator = list.descendingIterator();
        }

        Customer result = null;
        if (descIterator.hasNext()) {
            result = descIterator.next();
        }
        return result;
    }
}
