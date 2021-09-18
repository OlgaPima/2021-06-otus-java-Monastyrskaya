package homework;

import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    //todo: 3. надо реализовать методы этого класса

    TreeMap<Customer, String> customersMap = new TreeMap<>();

    public Map.Entry<Customer, String> getSmallest() {
        TreeMap<Customer, String> customersCopy = getCustomersCopy();
        return customersCopy.firstEntry();
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        TreeMap<Customer, String> customersCopy = getCustomersCopy();
        return customersCopy.ceilingEntry(new Customer(customer.getId(), customer.getName(), customer.getScores()+1));
    }

    public void add(Customer customer, String data) {
        customersMap.put(customer, data);
    }

    private TreeMap<Customer, String> getCustomersCopy() {
        TreeMap<Customer, String> deepCopy = new TreeMap<>();
        for(Customer key : customersMap.keySet()) {
            Customer customerCopy = new Customer(key.getId(), key.getName(), key.getScores());
            deepCopy.put(customerCopy, customersMap.get(key));
        }
        return deepCopy;
    }
}
