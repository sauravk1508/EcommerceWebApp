package com.eCommerce.Entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;
private String name;
private String description;
private String brand;
private BigDecimal price;
private String category;
private Date releaseDate;
private boolean available;
private int quantity;
private String imageName; // Image file name
private String imageType; // Image file type (e.g., image/jpeg)

@Lob
private byte[] imageData; // Image binary data

// New field to hold Base64 image string (not mapped to the database)
private String base64Image;

public Product() {}

public Product(Integer id, String name, String description, String brand, BigDecimal price, String category,
		Date releaseDate, boolean available, int quantity, String imageName, String imageType, byte[] imageData,
		String base64Image) {
	super();
	this.id = id;
	this.name = name;
	this.description = description;
	this.brand = brand;
	this.price = price;
	this.category = category;
	this.releaseDate = releaseDate;
	this.available = available;
	this.quantity = quantity;
	this.imageName = imageName;
	this.imageType = imageType;
	this.imageData = imageData;
	this.base64Image = base64Image;
}







public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
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
public String getBrand() {
	return brand;
}
public void setBrand(String brand) {
	this.brand = brand;
}
public BigDecimal getPrice() {
	return price;
}
public void setPrice(BigDecimal price) {
	this.price = price;
}
public String getCategory() {
	return category;
}
public void setCategory(String category) {
	this.category = category;
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

// Add getters and setters for the new fields
public String getImageName() {
    return imageName;
}

public void setImageName(String imageName) {
    this.imageName = imageName;
}

public String getImageType() {
    return imageType;
}

public void setImageType(String imageType) {
    this.imageType = imageType;
}

public byte[] getImageData() {
    return imageData;
}

public void setImageData(byte[] imageData) {
    this.imageData = imageData;
}
//Getters and Setters for base64Image
public String getBase64Image() {
 return base64Image;
}

public void setBase64Image(String base64Image) {
 this.base64Image = base64Image;
}







@Override
public String toString() {
	return "Product [id=" + id + ", name=" + name + ", description=" + description + ", brand=" + brand + ", price="
			+ price + ", category=" + category + ", releaseDate=" + releaseDate + ", available=" + available
			+ ", quantity=" + quantity + ", imageName=" + imageName + ", imageType=" + imageType + ", imageData="
			+ Arrays.toString(imageData) + ", base64Image=" + base64Image + "]";
}







}
