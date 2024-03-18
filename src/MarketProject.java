import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class MarketProject {
}

class Product {
    //attributes
    private long Id;
    private String name;

    private double price;

    //construcktor

    public Product(long Id, String name, int quantity, double price) {
        this.Id = Id;
        this.name = name;
        this.price = price;
    }

    public Product(Product product) {
    }


    public long getId() {
        return Id;
    }

    public void setId(long Id) {
        this.Id = Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) throws InvalidPriceException {
        if (price < 0) {
            throw new InvalidPriceException(price);
        }
        this.price = price;
    }



    public String toString() {
        return " { "+getId()+" } "+" - { "+getName()+" } @ { "+getPrice()+" } ";
    }

    public boolean equals(Object o) {
        if (o instanceof Product) {
            Product other = (Product) o;
            return Math.abs(this.price - other.price) < 0.001;
        }
        return false;
    }


}

class FoodProduct extends Product {

    //attributes
    private int Calories;
    private boolean isContainsDairy;
    private boolean isContainsEggs;
    private boolean isContainsPeanuts;
    private boolean isContainsGluten;

    //construcktor


    public FoodProduct(long Id, String name, int quantity, double price, int calories, boolean isContainsDairy, boolean isContainsEggs, boolean isContainsPeanuts, boolean isContainsGluten) {
        super(Id, name, quantity, price);
        this.Calories = calories;
        this.isContainsDairy = isContainsDairy;
        this.isContainsEggs = isContainsEggs;
        this.isContainsPeanuts = isContainsPeanuts;
        this.isContainsGluten = isContainsGluten;
    }

    public int getCalories() {
        return Calories;
    }

    public void setCalories(int calories) throws InvalidAmountException {
        if (calories < 0) {
            throw new InvalidAmountException(calories);
        }
        this.Calories = calories;
    }

    public boolean containsDairy() {
        return isContainsDairy;
    }

    public boolean containsEgg() {
        return isContainsEggs;
    }

    public boolean containsGluten() {
        return isContainsGluten;
    }

    public boolean containsPeanuts() {
        return isContainsPeanuts;
    }

}


class CleaningProduct extends Product {

    //attributes
    private boolean Liquid;
    private String WhereToUse;


    public CleaningProduct(long Id, String name, int quantity, double price, boolean liquid, String WhereToUse) {
        super(Id, name, quantity, price);
        this.Liquid = liquid;
        this.WhereToUse = WhereToUse;
    }

    public String getWhereToUse() {
        return WhereToUse;
    }

    public void setWhereToUse(String whereToUse) {
        WhereToUse = whereToUse;
    }

    public boolean isLiquid() {
        return Liquid;
    }
}

class Customer  {
    public String name;
    private int Points;
    private Map<Store,ArrayList<Product>> carts;
    private double totalDue;


    private ArrayList<ProductCount> cart;
    private int points;


    Customer(String name) {
        this.name = name;
        this.carts=new HashMap<>();
        this.totalDue=0.0;
    }


    public int getPoints() {
        return Points;
    }
    public String getName() {
        return name;
    }



    public void setPoints(int Points) {
        this.Points=Points;
    }


    public void addToCart(Store store,Product product,int count){

        if (!carts.containsKey(store)){
            throw new StoreNotFoundException(store.getName());
        }
        boolean productAvailable =false;
        int availableQuantity=store.remaining(product);

        if (availableQuantity >=count) {
            ArrayList<Product> products = carts.get(store);
            for (int i=0;i<count ;i++){
                products.add(product);
            }
        }else {
            throw new InsufficientFundsException(count,availableQuantity);
        }


    }



    public double getTotalDue(Store store) {
        if (!cart.contains(store)) {
            throw new StoreNotFoundException(store.getName());
        }

        ArrayList<Product> products =carts.get(store);
        double total =0.0;
        for (Product product:products){
            total +=product.getPrice();
        }
        return total;
    }
    public int getPoints(Store store) {
        if (!carts.containsKey(store)) {
            throw new IllegalArgumentException("Customer does not have a cart for the store.");
        }

        return store.getCustomerPoints(this);
    }

    public double pay(Store store, double amount, boolean usePoints) {
        if (!cart.contains(store.getName())) {
            throw new StoreNotFoundException(store.getName());
        }

        double totalDue = getTotalDue(store);
        if (usePoints) {
            int customerPoints = store.getCustomerPoints(this);

            if (customerPoints > 0) {
                double pointValues = customerPoints * store.getPointValue();
                if (amount >= (totalDue - pointValues)) {
                    double remainingAmount = amount - (totalDue - pointValues);
                    return remainingAmount;
                } else {
                    throw new InsufficientFundsException(totalDue,amount);
                }
            }
        }
        else {
            throw new InsufficientFundsException(totalDue,amount);
        }
        if (amount >= totalDue) {
            System.out.println("Thank you ");
            double change = amount - totalDue;
            return change;
        } else {
            throw new InsufficientFundsException(totalDue,amount);
        }
    }

    public String receipt(Store store) throws StoreNotFoundException {
        if (!carts.containsKey(store)) {
            throw new StoreNotFoundException(store.getName());
        }

        ArrayList<Product> products = carts.get(store);
        String receipt = "Customer receipt for " + store.getName() + "\n";
        for (Product product : products) {
            receipt += product.getId() + " - " + product.getName() + " @ " + product.getPrice() + "\n";
        }
        receipt += "Total Due: " + getTotalDue(store);

        return receipt;
    }



}

class ProductCount {
    private Product product;
    private int count;

    public ProductCount(Product product, int count) {
        this.product = product;
        this.count = count;
    }

    public Product getProduct() {
        return product;
    }

    public int getCount() {
        return count;
    }
}





class Store {

    private String Name;
    private String Website;
    private int Quantity;
    private Map<Product,Integer> inventory;
    private Map<Customer, Integer> customers = new HashMap<>();
    Store(String name,String website){
        this.Name =name;
        this.Website=website;
        this.inventory=new HashMap<>();
        this.customers=new HashMap<>();

    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        this.Name = name;
    }
    public int getQuantity(){
        return  Quantity;
    }
    public void setQuantity(int quantity){
        this.Quantity=quantity;
    }
    public String getWebsite() {
        return Website;
    }
    public void setWebsite(String website) {
        this.Website = website;
    }
    public int getInventorySize(){
        return customers.size();
    }
    public int getCount() {
        return inventory.size();
    }
    public int remaining(Product product){
        if(!inventory.containsKey(product)){
            throw  new ProductNotFoundException("Product not found: " + product.getId()+ " - " + product.getName());
        }
        return inventory.get(product);
    }

    public int addToInventory(int amount){
        if (amount<0){
            throw new InvalidAmountException(amount);
        }
        if (amount>0) {
            Quantity+=amount;
            return Quantity;
        }else
            return Quantity;
    }

    public double purchase(Product product, int amount) {
        if (amount < 0 || amount > inventory.getOrDefault(product, 0)) {
            throw new InvalidAmountException(amount);
        }

        if (!inventory.containsKey(product)) {
            throw new ProductNotFoundException("Product not found: " + product.getId() + " - " + product.getName());
        }

        double totalPrice = product.getPrice() * amount;
        inventory.put(product, inventory.get(product) - amount);

        return totalPrice;
    }


    public int getProductCount(Product product) {
        if (inventory.containsKey(product)) {
            return inventory.get(product);
        } else {
            throw new ProductNotFoundException("Product not found: " + product.getId() + " - " + product.getName());
        }
    }


    public void removeProduct(Product product) {
        if (inventory.containsKey(product)) {
            inventory.remove(product);
        } else {
            throw new ProductNotFoundException("Product not found: " + product.getId() + " - " + product.getName());
        }
    }


    public void addToInventory(Product product, int amount) {
        if (amount < 0) {
            throw new InvalidAmountException(amount);
        }

        if (inventory.containsKey(product)) {
            int currentAmount = inventory.get(product);
            inventory.put(product, currentAmount + amount);
        } else {
            inventory.put(product, amount);
        }
    }


    public void addCustomer(Customer customer) {
        customers.put(customer,0);
    }

    public int getCustomerPoints(Customer customer) {
        if (customers.containsKey(customer)) {
            return customers.get(customer);
        } else {
            throw new CustomerNotFoundException("Customer not found: " + customer.getName());
        }
    }


    public int getPointValue() { return getPointValue();
    }
}


class CustomerNotFoundException extends IllegalArgumentException{
    private String phone;

    private Customer customer;

    public CustomerNotFoundException(String s ) {
    }

    @Override
    public String toString() {
        return "CustomerNotFoundException: Name-"+ customer.getName() ;
    }
}

class InsufficientFundsException extends RuntimeException {
    private double total;
    private double payment;

    public InsufficientFundsException(double total, double payment) {
        this.total = total;
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "InsufficientFundsException: " + total + " due, but only " + payment + " given";
    }
}

class InvalidAmountException extends RuntimeException {
    private int amount;
    private int quantity;

    public InvalidAmountException(int amount) {
        this.amount = amount;
    }

    public InvalidAmountException(int amount, int quantity) {
        this.amount = amount;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        if (quantity == 0) {
            return "InvalidAmountException: " + amount;
        } else {
            return "InvalidAmountException: " + amount + " was requested, but only " + quantity + " remaining";
        }
    }
}

class InvalidPriceException extends RuntimeException {
    private double price;

    public InvalidPriceException(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "InvalidPriceException: " + price;
    }
}

class ProductNotFoundException extends IllegalArgumentException {
    private Long ID;
    private String name;
    Product product;

    public ProductNotFoundException(Long ID) {
        super();
        this.ID = ID;
        this.name = null;
    }

    public ProductNotFoundException(String name) {
        super();
        this.ID = 0L;
        this.name = name;
    }
}
class StoreNotFoundException extends IllegalArgumentException {
    private String name;

    public StoreNotFoundException(String name) {
        this.name = name;

    }
    @Override
    public String toString(){
        return "StoreNotFoundException: "+name;
    }
}









