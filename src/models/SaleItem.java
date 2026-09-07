package models;


import java.math.BigDecimal;

/**
 * Simple model class matching the sale_items table (line items).
 * medicineName is a display-only convenience field for the cart UI -
 * it is NOT a column in sale_items and is never persisted directly;
 * it just avoids extra lookups while building the cart table.
 */
public class SaleItem {
    private int saleItemId;
    private int saleId;
    private int medicineId;
    private String medicineName; // display-only, not persisted
    private int quantitySold;
    private BigDecimal priceAtSale;

    public int getSaleItemId() { return saleItemId; }
    public void setSaleItemId(int saleItemId) { this.saleItemId = saleItemId; }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }

    public BigDecimal getPriceAtSale() { return priceAtSale; }
    public void setPriceAtSale(BigDecimal priceAtSale) { this.priceAtSale = priceAtSale; }

    public BigDecimal getSubtotal() {
        return priceAtSale.multiply(BigDecimal.valueOf(quantitySold));
    }
}
