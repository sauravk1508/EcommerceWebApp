package com.eCommerce.Entity;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;

@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false)
	private String productNumber;
	private String name;
	private String description;
	@OneToOne
	private Brand brand;
	@OneToOne
	private ProductSubCategory subCategory;
	private BigDecimal price;
	private Date releaseDate;
	private boolean available;
	private int quantity;
	@ManyToOne
	private ImageDetails imageDetails;
	private String color;
	private String size;

	public void copyValuesFrom(Product updatedProduct) {
		if (updatedProduct == null)
			return;
		this.productNumber = updatedProduct.getProductNumber();
		this.name = updatedProduct.getName();
		this.description = updatedProduct.getDescription();
		this.brand = updatedProduct.getBrand();
		this.price = updatedProduct.getPrice();
		this.releaseDate = updatedProduct.getReleaseDate();
		this.available = updatedProduct.isAvailable();
		this.quantity = updatedProduct.getQuantity();
		this.color = updatedProduct.getColor();
		this.size = updatedProduct.getSize();

		// Optional: Only set imageDetails if provided
		if (updatedProduct.getImageDetails() != null) {
			this.imageDetails = updatedProduct.getImageDetails();
		}
	}

	public Product() {
	}

	public Product(Long id, String productNumber, String name, String description, Brand brand, BigDecimal price,
			Date releaseDate, boolean available, int quantity, ImageDetails imageDetails, String color, String size) {
		super();
		this.id = id;
		this.productNumber = productNumber;
		this.name = name;
		this.description = description;
		this.brand = brand;
		this.price = price;
		this.releaseDate = releaseDate;
		this.available = available;
		this.quantity = quantity;
		this.imageDetails = imageDetails;
		this.color = color;
		this.size = size;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public ImageDetails getImageDetails() {
		return imageDetails;
	}

	public void setImageDetails(ImageDetails imageDetails) {
		this.imageDetails = imageDetails;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	public ProductSubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(ProductSubCategory subCategory) {
		this.subCategory = subCategory;
	}

	public void generateProductNumber() {
		// 1️⃣ Brand code
		String brandCode = safeCode((brand != null) ? brand.getName() : "GEN", 3);

		// 2️⃣ Product code
		String nameCode = safeCode(name, 4);

		// 4 Category from brand
		String subCategoryCode = "GEN";
		if (brand != null && subCategory != null && subCategory.getName() != null) {
			subCategoryCode = safeCode(subCategory.getName(), 4);
		}

		// 4 Category from brand
		String categoryCode = "GEN";
		if (brand != null && subCategory.getProductCategory() != null && subCategory.getProductCategory().getName() != null) {
			categoryCode = safeCode(subCategory.getProductCategory().getName(), 4);
		}

		// 5 Optional attributes
		String colorCode = safeCode(color, 3);
		String sizeCode = safeCode(size, 3);

		// 6 Combine key identifiers
		String baseString = (brandCode + nameCode + subCategoryCode + categoryCode + colorCode + sizeCode).toUpperCase();

		// 7 Deterministic short hash
		String hash = generateShortHash(baseString);

		// 7️⃣ Final product number
		this.productNumber = brandCode + "-" + nameCode + "-" + subCategoryCode + "-" + categoryCode + "-" + hash;
	}

	// ✅ Hash generator for uniqueness (same data → same hash)
	private String generateShortHash(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(input.getBytes());
			int hashCode = 0;
			for (int i = 0; i < 3 && i < digest.length; i++) {
				hashCode = (hashCode << 8) | (digest[i] & 0xFF);
			}
			return String.format("%05d", Math.abs(hashCode) % 100000);
		} catch (NoSuchAlgorithmException e) {
			return "00000";
		}
	}

	// ✅ Safely create a short uppercase code from any word
	private String safeCode(String text, int length) {
		if (text == null || text.trim().isEmpty())
			return "DEF";
		text = text.replaceAll("[^A-Za-z0-9]", ""); // remove spaces/special chars
		return text.substring(0, Math.min(length, text.length())).toUpperCase();
	}

	// Automatically generate before saving
	@PrePersist
	public void onCreate() {
		if (this.productNumber == null || this.productNumber.isEmpty()) {
			generateProductNumber();
		}
	}

}
