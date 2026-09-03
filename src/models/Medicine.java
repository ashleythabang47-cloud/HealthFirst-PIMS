package models;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * Simple model class matching the medicines table.
 */
public class Medicine {
    private int medicineId;
    private String name;
    private String company;
    private String medicineType;
    private BigDecimal price;
    private int quantityInStock;
    private int reorderLevel;
    private Date expiryDate;
    private int supplierId;

    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getMedicineType() { return medicineType; }
    public void setMedicineType(String medicineType) { this.medicineType = medicineType; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }

    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public boolean isLowStock() { return quantityInStock <= reorderLevel; }

    @Override
    public String toString() {
        return name + " (Stock: " + quantityInStock + ", Price: R" + price + ")";
    }
}
