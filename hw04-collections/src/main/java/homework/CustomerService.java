package homework;

import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    //todo: 3. надо реализовать методы этого класса

    private final TreeMap<Customer, String> customersMap = new TreeMap<>();

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry result = customersMap.firstEntry();
        return createMyMapEntry(result);
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry result = customersMap.ceilingEntry(new Customer(customer.getId(), customer.getName(), customer.getScores()+1));
        return createMyMapEntry(result);
    }

    public void add(Customer customer, String data) {
        customersMap.put(customer, data);
    }

    private MyMapEntry<Customer, String> createMyMapEntry(Map.Entry entry) {
        if (entry == null) {
            return null;
        }
        else {
            Customer key = (Customer) entry.getKey();
            Customer keyCopy = new Customer(key.getId(), key.getName(), key.getScores());
            return new MyMapEntry<>(keyCopy, (String) entry.getValue());
        }
    }
}
