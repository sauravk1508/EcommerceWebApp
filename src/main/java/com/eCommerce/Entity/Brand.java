package com.eCommerce.Entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity
public class Brand {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false)
	private String name;
	private String description;
	@OneToMany
	@JoinTable(
		name = "brand_subcategory",
		joinColumns = @JoinColumn(name = "Brand_id",unique = false),
		inverseJoinColumns = @JoinColumn(name = "subCategory_id",unique = false)
	)
	private List<ProductSubCategory> subCategory;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
	public List<ProductSubCategory> getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(List<ProductSubCategory> subCategory) {
		this.subCategory = subCategory;
	}

	
}
