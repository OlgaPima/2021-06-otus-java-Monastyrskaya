package homework;


//import java.util.ArrayDeque;
//import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class CustomerReverseOrder {

    //todo: 2. надо реализовать методы этого класса

    private final LinkedList<Customer> list = new LinkedList<>();
    private Iterator<Customer> descIterator;
    //private final Deque stack = new ArrayDeque(); //TODO: попробовать переделать на Deque, без итератора

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
