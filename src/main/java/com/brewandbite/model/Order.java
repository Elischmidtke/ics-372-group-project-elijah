

//import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {

    public enum Status { PENDING, IN_PROGRESS, FULFILLED }

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //@JsonProperty("orderId")
    private String orderId;

   //@JsonProperty("customerName")
    private String customerName;

    //@JsonProperty("items")
    private List<OrderItem> items = new ArrayList<>();

    //@JsonProperty("status")
    private Status status = Status.PENDING;

    //@JsonProperty("timestamp")
    private String timestamp;

    public Order() {}

    public Order(String customerName) {
        this.orderId = "ORD-" + System.currentTimeMillis();
        this.customerName = customerName;
        this.timestamp = LocalDateTime.now().format(FMT);
    }

    /** Sum of all line totals. */
    public double getTotalPrice() {
        return items.stream().mapToDouble(OrderItem::getLineTotal).sum();
    }

    public void addItem(OrderItem item) { items.add(item); }
    public void removeItem(OrderItem item) { items.remove(item); }

    public String getOrderId() { return orderId; }
    public void setOrderId(String v) { this.orderId = v; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String v){ this.customerName = v; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> v){ this.items = v; }

    public Status getStatus() { return status; }
    public void setStatus(Status v) { this.status = v; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String v) { this.timestamp = v; }
}
