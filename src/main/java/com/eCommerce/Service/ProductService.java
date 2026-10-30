package com.eCommerce.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eCommerce.Entity.Brand;
import com.eCommerce.Entity.ImageDetails;
import com.eCommerce.Entity.Product;
import com.eCommerce.Entity.ProductCategory;
import com.eCommerce.Entity.ProductSubCategory;
import com.eCommerce.Repository.BrandRepository;
import com.eCommerce.Repository.CategoryRepository;
import com.eCommerce.Repository.ImageRepository;
import com.eCommerce.Repository.ProductRepository;
import com.eCommerce.Repository.SubCategoryRepository;

@Service
public class ProductService {
	@Autowired
	private ProductRepository repo;
	@Autowired
	private ImageRepository imageRepo;
	@Autowired
	private CategoryRepository prodCategoryRepo;
	@Autowired
	private BrandRepository brandRepo;
	@Autowired
	private SubCategoryRepository subCategoryRepo;

	public List<Product> getAllProduct(){
		return repo.findAll();
		
	}
	
	public Product saveProduct(Product product) {
		return repo.save(product);
	}
	
	public Product getProductById(Long productId) {
	    return repo.findById(productId).orElse(null); // Return the product or null if not found
	}
	

	public Product updateProduct(Long id , Product product) {
		return repo.save(product);
	}
	
	public void deleteProduct(Long id) {
		repo.deleteById(id);
	}
	
	public Product getProductByCategoryAndId(Long category_id, Long id) {
		return repo.findByCategoryAndId(category_id, id);
	}
	
	 public List<Product> getSearchedProduct(String keyword) {
	        List<Product> products = repo.searchProducts(keyword);
	        System.out.println("Found products: " + products.size());
	        return products;
	    }
	
	  // Method to get products by category
	 public List<Product> getProductsByCategory(Long categoryId) {
		return repo.findByCategory(categoryId); // Replace with actual repository method
	}
	private List<String> getHeader(){
			String[] header = {"name","description","subCategory","brand","release date","quantity","isAvailable","price","color","size"};
			return Arrays.stream(header).toList();
	}
	public void validateHeaders(List<String> expectedHeader, List<String> excelHeader,List<String> errorList) {
		for (String expected : expectedHeader) {
			if (!excelHeader.contains(expected)) {
				errorList.add("Missing header: " + expected);
			}
		}

		for (String actual : excelHeader) {
			if (!expectedHeader.contains(actual)) {
				errorList.add("Unexpected header found in Excel: " + actual);
			}
		}
	}

	
	public List<Map<String, String>> readExcelFile(byte[] fileBytes, List<String> errorList) throws Exception {
	    List<Map<String, String>> records = new ArrayList<>();
	    Workbook workbook = null;
	    
	    try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
	        workbook = WorkbookFactory.create(inputStream);
	        Sheet sheet = workbook.getSheetAt(0);
	        
	        // Check if sheet is empty
	        if (sheet.getPhysicalNumberOfRows() == 0) {
	            errorList.add("Attached Excel File is Empty");
	            return records;
	        }
	        
	        // Read header row
	        Row headerRow = sheet.getRow(0);
	        if (headerRow == null) {
	            errorList.add("Header row is missing");
	            return records;
	        }
	        
	        Map<String, Integer> headerMap = new HashMap<>();
	        List<String> excelHeader = new ArrayList<>();
	        
	        for (Cell cell : headerRow) {
	            String headerValue = getCellValueAsString(cell).trim();
	            if (!headerValue.isEmpty()) {
	                excelHeader.add(headerValue);
	                headerMap.put(headerValue, cell.getColumnIndex());
	            }
	        }
	        
	        // Validate headers
	        validateHeaders(getHeader(), excelHeader, errorList);
	        if (!errorList.isEmpty()) {
	            return records;
	        }
	        
	        // Read data rows (starting from row 1, skipping header)
	        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	            Row dataRow = sheet.getRow(i);
	            
	            // Skip empty rows
	            if (dataRow == null || isRowEmpty(dataRow)) {
	                continue;
	            }
	            
	            Map<String, String> data = new HashMap<>();
	            for (String header : headerMap.keySet()) {
	                int colIndex = headerMap.get(header);
	                Cell cell = dataRow.getCell(colIndex);
	                String cellValue = getCellValueAsString(cell);
	                data.put(header, cellValue);
	            }
	            records.add(data);
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        errorList.add("Exception while reading Excel: " + e.getMessage());
	        throw new Exception("Exception while reading Excel file", e);
	    } finally {
	        // Close workbook to free resources
	        if (workbook != null) {
	            try {
	                workbook.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    
	    return records;
	}

	/**
	 * Helper method to convert cell value to string
	 */
	private String getCellValueAsString(Cell cell) {
	    if (cell == null) {
	        return "";
	    }
	    
	    switch (cell.getCellType()) {
	        case STRING:
	            return cell.getStringCellValue().trim();
	            
	        case NUMERIC:
	            if (DateUtil.isCellDateFormatted(cell)) {
	                // Format date as needed
	                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	                return dateFormat.format(cell.getDateCellValue());
	            } else {
	                // Handle numeric values
	                double numericValue = cell.getNumericCellValue();
	                // Check if it's a whole number
	                if (numericValue == Math.floor(numericValue)) {
	                    return String.valueOf((long) numericValue);
	                } else {
	                    return String.valueOf(numericValue);
	                }
	            }
	            
	        case BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());
	            
	        case FORMULA:
	            // Evaluate formula and get the cached value
	            try {
	                switch (cell.getCachedFormulaResultType()) {
	                    case NUMERIC:
	                        double numValue = cell.getNumericCellValue();
	                        if (numValue == Math.floor(numValue)) {
	                            return String.valueOf((long) numValue);
	                        }
	                        return String.valueOf(numValue);
	                    case STRING:
	                        return cell.getStringCellValue().trim();
	                    case BOOLEAN:
	                        return String.valueOf(cell.getBooleanCellValue());
	                    default:
	                        return "";
	                }
	            } catch (Exception e) {
	                return cell.getCellFormula();
	            }
	            
	        case BLANK:
	            return "";
	            
	        default:
	            return "";
	    }
	}

	/**
	 * Helper method to check if a row is empty
	 */
	private boolean isRowEmpty(Row row) {
	    if (row == null) {
	        return true;
	    }
	    
	    for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
	        Cell cell = row.getCell(cellNum);
	        if (cell != null && cell.getCellType() != CellType.BLANK) {
	            String cellValue = getCellValueAsString(cell);
	            if (!cellValue.isEmpty()) {
	                return false;
	            }
	        }
	    }
	    return true;
	}

	public void buildErrorResponse(Map<String, Object> response, List<String> errorList) {
		response.put("hasError", true);
		response.put("error", "error");
		response.put("errorList", errorList);
	}

	@Transactional
	public Map<String, Object> saveUploadedProductData(String fileName, String base64Data) throws Exception {
		List<String> errorList = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();
		try {
			byte[] fileBytes = Base64.getDecoder().decode(base64Data);
			String uploadDir = "src/main/resources/static/uploads/";

	        // Create directories if not exist
	        File dir = new File(uploadDir);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }

	        // 3️⃣ Save the uploaded file
	        Path filePath = Paths.get(uploadDir, fileName);
	        Files.write(filePath, fileBytes);

	        System.out.println("✅ File saved at: " + filePath.toAbsolutePath());
			List<Map<String,String>> productData = readExcelFile(fileBytes,errorList);
			if(!errorList.isEmpty()){
				errorList.add("Correct below Errors! Then Upload Again");
				buildErrorResponse(response, errorList);
				return response;
			}
			boolean isProductSaved = saveProductData(productData,errorList);
			if(isProductSaved && errorList.isEmpty()){
				response.put("successMsg", "All the Data Saved SuccessFully!");
			}else{
				errorList.add("Something went Wrong! Data not Saved Successfully");
				buildErrorResponse(response, errorList);
				throw new RuntimeException("Something went Wrong! Data not Saved Successfully");
			}
		}catch (Exception e){
			e.printStackTrace();
			errorList.add("Some Exception Occurred! " + e.getMessage());
			buildErrorResponse(response, errorList);
			e.printStackTrace();
		}
		return response;
	}
	private boolean saveProductData(List<Map<String,String>> productData,List<String> errorList){
		if(productData.isEmpty()){
			errorList.add("No data Found for Saving Product! ");
			return false;
		}
		for(Map<String,String> data : productData){
			try{
				Product product = new Product();
				product.setName(data.get("name"));
				product.setDescription(data.get("description"));
				Brand brand = brandRepo.findByName(data.get("brand"));
				if(brand == null) {
					errorList.add("Given Brand Name is not Present : " + data.get("brand"));
					return false;
				}
				product.setBrand(brand); 
				ProductSubCategory subCategory = subCategoryRepo.findByName(data.get("subCategory"));
				if(subCategory == null) {
					errorList.add("Given Sub Category Name is not Present : " + data.get("subCategory"));
					return false;
				}
				product.setSubCategory(subCategory); 
				product.setReleaseDate(Date.valueOf(data.get("release date").trim()));
				product.setQuantity(Integer.parseInt(data.get("quantity").trim()));
				product.setColor(data.get("color").trim());
				product.setSize(data.get("size").trim());
				product.generateProductNumber();
				String val = data.get("isAvailable").trim();
				int intVal = Integer.parseInt(val);  // converts "0" -> 0, "1" -> 1
				product.setAvailable(intVal == 1);   // true if 1, false if 0
				BigDecimal price = new BigDecimal(data.get("price").trim()) ;
				product.setPrice(price);
				repo.save(product);

			} catch (Exception e) {
				e.printStackTrace();
				errorList.add("Exception Occurred while Saving Data for : " + data.get("name"));
				throw new RuntimeException(e);
			}
		}
		if(!errorList.isEmpty()){
			return false;
		}
		return true;

	}
	
	public ImageDetails saveImageDetails(ImageDetails image) {
		return imageRepo.save(image);
	}
	
	public ProductCategory getProdCategoryById(Long categoryId){
		return prodCategoryRepo.findById(categoryId).orElse(null);
	}
	public List<ProductCategory> findAllProdCategory(){
		return prodCategoryRepo.findAll();
	}
	public ProductCategory getProdCategoryByName(String categoryName) {
		return prodCategoryRepo.findByName(categoryName);
		
	}
	public Map<String,Object> saveProdCategory(Long categoryId,String categoryName,String desc,boolean isForEdit) {
		Map<String,Object> response = new HashMap<String, Object>();
    	List<String> errorList = new ArrayList<String>();
	    try {
	    	ProductCategory prodCategory;
	    	if(categoryId!= null && categoryId!=0) {
	    		prodCategory = getProdCategoryById(categoryId);
	    	}else {
	    		prodCategory = getProdCategoryByName(categoryName);
	    	}
	    	if (!isForEdit) {
	    	    if (prodCategory != null) {
	    	        errorList.add(prodCategory.getName() + " already exists!");
	    	        buildErrorResponse(response, errorList);
	    	        return response;
	    	    }
	    	    prodCategory = new ProductCategory(); 
	    	}
	    	prodCategory.setName(categoryName);
	    	prodCategory.setDescription(desc);
	    	prodCategoryRepo.save(prodCategory);
	    	if(isForEdit) {
	    		response.put("successMsg", "Product Category i.e " + categoryName + " Updated successfully.");
	    	}else {
	    		response.put("successMsg", "Product Category Saved successfully.");
	    	}
	    	
	    	return response;
		} catch (Exception e) {
			e.printStackTrace();
			errorList.add("Some Exception Occured: while saving category i.e " + e.getMessage());
			buildErrorResponse(response, errorList);
			return response;
		}
	}
	 public Map<String,Object> saveSubCategory(String subCategoryName, String desc, Long categoryId,boolean isForEdit) {
			Map<String,Object> response = new HashMap<String, Object>();
	    	List<String> errorList = new ArrayList<String>();
		    try {
				ProductCategory existsProdCategory = getProdCategoryById(categoryId);
		    	if(existsProdCategory ==null) {
		    		errorList.add("Something went wrong : Selected Category not exist!");
		    		buildErrorResponse(response, errorList);
		    		return response;
		    	}
		    	ProductSubCategory subCategory;
		    	subCategory = subCategoryRepo.findByName(subCategoryName);
		    	if (!isForEdit) {
		    	    if (subCategory != null) {
		    	        errorList.add(subCategory.getName() + " already exists!");
		    	        buildErrorResponse(response, errorList);
		    	        return response;
		    	    }
		    	    subCategory = new ProductSubCategory(); 
		    	}
		    	subCategory.setName(subCategoryName);
		    	subCategory.setDescription(desc);
		    	subCategory.setProductCategory(existsProdCategory);
		    	subCategoryRepo.save(subCategory);
		    	response.put("successMsg", "Sub Category Saved successfully.");
		    	return response;
			} catch (Exception e) {
				e.printStackTrace();
				errorList.add("Some Exception Occured: while saving Sub Category i.e " + e.getMessage());
				buildErrorResponse(response, errorList);
				return response;
			}
		}
	 public ProductSubCategory getProdSubCategoryById(Long subCategoryId){
			return subCategoryRepo.findById(subCategoryId).orElse(null);
		}
	 public List<ProductSubCategory> findAllProdSubCategory(){
			return subCategoryRepo.findAll();
		}
	 public List<ProductSubCategory> findSubCategoryByIdList(List<Long> subCategoryIdList){
			return subCategoryRepo.findAllById(subCategoryIdList);
		}
	 @Transactional
	 public Map<String,Object> getSubCategoryBasedOnBrand(Long brandId) {
			Map<String,Object> response = new HashMap<String, Object>();
	    	List<String> errorList = new ArrayList<String>();
		    try {
				Brand brand = brandRepo.findById(brandId).orElse(null);
		    	if(brand ==null) {
		    		errorList.add("Something went wrong : Selected Brand not exist!");
		    		buildErrorResponse(response, errorList);
		    		return response;
		    	}
		    	List<ProductSubCategory> subCategoryList = brand.getSubCategory();
		    	List<Map<String, Object>> subCategoryMaps = new ArrayList<>();
		    	for (ProductSubCategory psb : subCategoryList) {
		    		subCategoryMaps.add(convertToMap(psb)); // works correctly for each Product object
		    	}

		    	response.put("subCategory", subCategoryMaps);
		    	response.put("successMsg", "Brand Saved successfully.");
		    	return response;
			} catch (Exception e) {
				e.printStackTrace();
				errorList.add("Some Exception Occured: While getting category i.e " + e.getMessage());
				buildErrorResponse(response, errorList);
				return response;
			}
		}
		public Map<String,Object> getCategoryBasedOnSubCategory(Long subCategoryId) {
			Map<String,Object> response = new HashMap<String, Object>();
	    	List<String> errorList = new ArrayList<String>();
		    try {
				ProductSubCategory subCategory = subCategoryRepo.findById(subCategoryId).orElse(null);
		    	if(subCategory ==null) {
		    		errorList.add("Something went wrong : Selected Brand not exist!");
		    		buildErrorResponse(response, errorList);
		    		return response;
		    	}
		    	response.put("category", convertToMap(subCategory.getProductCategory()));
		    	response.put("successMsg", "Brand Saved successfully.");
		    	return response;
			} catch (Exception e) {
				e.printStackTrace();
				errorList.add("Some Exception Occured: While getting category i.e " + e.getMessage());
				buildErrorResponse(response, errorList);
				return response;
			}
		}
		 public static Map<String, Object> convertToMap(Object obj) {
		        Map<String, Object> map = new HashMap<>();
		        try {
		            for (Field field : obj.getClass().getDeclaredFields()) {
		                field.setAccessible(true); // allows access to private fields
		                map.put(field.getName(), field.get(obj));
		            }
		        } catch (IllegalAccessException e) {
		            e.printStackTrace();
		        }
		        return map;
		}

	public List<Brand> findAllBrands() {
		return brandRepo.findAll();
	}
	
	public Brand findBrandById(Long brandId) {
		return brandRepo.findById(brandId).orElse(null);
	}

	public Map<String, Object> saveProdBrand(String brandName, String desc, List<Long> subCategoryList,
				boolean isForEdit) {
			Map<String,Object> response = new HashMap<String, Object>();
			List<String> errorList = new ArrayList<String>();
	    try {
			List<ProductSubCategory> existsProdSubCategory = findSubCategoryByIdList(subCategoryList);
	    	if(existsProdSubCategory == null) {
	    		errorList.add("Something went wrong : Selected Sub-Categories not exist!");
	    		buildErrorResponse(response, errorList);
	    		return response;
	    	}
	    	Brand prodBrand;
	    	prodBrand = brandRepo.findByName(brandName);
	    	if (!isForEdit) {
	    	    if (prodBrand != null) {
	    	        errorList.add(prodBrand.getName() + " already exists!");
	    	        buildErrorResponse(response, errorList);
	    	        return response;
	    	    }
	    	    prodBrand = new Brand(); 
	    	}
	    	prodBrand.setName(brandName);
	    	prodBrand.setDescription(desc);
	    	prodBrand.setSubCategory(existsProdSubCategory);
	    	brandRepo.save(prodBrand);
	    	response.put("successMsg", "Brand Saved successfully.");
	    	return response;
		} catch (Exception e) {
			e.printStackTrace();
			errorList.add("Some Exception Occured: while saving Brand i.e " + e.getMessage());
			buildErrorResponse(response, errorList);
			return response;
		}
	}
	
	public Map<String,Object> deleteProdCategory(Long categoryId) {
		Map<String,Object> response = new HashMap<String, Object>();
    	List<String> errorList = new ArrayList<String>();
	    try {
	    	ProductCategory existsProdCategory = getProdCategoryById(categoryId);
	    	if(existsProdCategory == null) {
	    		errorList.add("Something went wrong : Selected Category not exist!");
	    		buildErrorResponse(response, errorList);
	    		return response;
	    	}
	    	prodCategoryRepo.delete(existsProdCategory);
	    	response.put("successMsg", "Category successfully Deleted.");
	    	return response;
	    } catch (DataIntegrityViolationException ex) {
	        // This exception is thrown when a foreign key constraint prevents deletion
	        ex.printStackTrace();
	        errorList.add("Cannot delete this category as it is associated with one or more subcategories or products.");
	        buildErrorResponse(response, errorList);
	        return response;

	    } catch (ConstraintViolationException ex) {
	        // Hibernate-specific constraint violation
	        ex.printStackTrace();
	        errorList.add("Delete failed: This category is linked to other records (foreign key constraint).");
	        buildErrorResponse(response, errorList);
	        return response;

	    } catch (Exception e) {
	        e.printStackTrace();
	        errorList.add("An unexpected error occurred while deleting the product category: " + e.getMessage());
	        buildErrorResponse(response, errorList);
	        return response;
	    }
	}
	
	public Map<String,Object> deleteProdSubCategory(Long subCategoryId) {
		Map<String,Object> response = new HashMap<String, Object>();
    	List<String> errorList = new ArrayList<String>();
	    try {
	    	ProductSubCategory existsProdSubCategory = getProdSubCategoryById(subCategoryId);
	    	if(existsProdSubCategory == null) {
	    		errorList.add("Something went wrong : Selected Category not exist!");
	    		buildErrorResponse(response, errorList);
	    		return response;
	    	}
	    	subCategoryRepo.delete(existsProdSubCategory);
	    	response.put("successMsg", "Sub-Category successfully Deleted.");
	    	return response;
	    } catch (DataIntegrityViolationException ex) {
	        // This exception is thrown when a foreign key constraint prevents deletion
	        ex.printStackTrace();
	        errorList.add("Cannot delete this sub-category as it is associated with one or more Brands or products.");
	        buildErrorResponse(response, errorList);
	        return response;

	    } catch (ConstraintViolationException ex) {
	        // Hibernate-specific constraint violation
	        ex.printStackTrace();
	        errorList.add("Delete failed: This sub-category is linked to other records (foreign key constraint).");
	        buildErrorResponse(response, errorList);
	        return response;

	    } catch (Exception e) {
	        e.printStackTrace();
	        errorList.add("An unexpected error occurred while deleting the product sub-category: " + e.getMessage());
	        buildErrorResponse(response, errorList);
	        return response;
	    }
	}
	
	public Map<String,Object> deleteBrand(Long brandId) {
		Map<String,Object> response = new HashMap<String, Object>();
    	List<String> errorList = new ArrayList<String>();
	    try {
	    	Brand existsBrand = findBrandById(brandId);
	    	if(existsBrand == null) {
	    		errorList.add("Something went wrong : Selected Brand not exist!");
	    		buildErrorResponse(response, errorList);
	    		return response;
	    	}
	    	brandRepo.delete(existsBrand);
	    	response.put("successMsg", "Brand successfully Deleted.");
	    	return response;
	    } catch (DataIntegrityViolationException ex) {
	        // This exception is thrown when a foreign key constraint prevents deletion
	        ex.printStackTrace();
	        errorList.add("Cannot delete this Brand as it is associated with one or more products.");
	        buildErrorResponse(response, errorList);
	        return response;

	    } catch (ConstraintViolationException ex) {
	        // Hibernate-specific constraint violation
	        ex.printStackTrace();
	        errorList.add("Delete failed: This Brand is linked to other records (foreign key constraint).");
	        buildErrorResponse(response, errorList);
	        return response;

	    } catch (Exception e) {
	        e.printStackTrace();
	        errorList.add("An unexpected error occurred while deleting the product brand: " + e.getMessage());
	        buildErrorResponse(response, errorList);
	        return response;
	    }
	}
}
