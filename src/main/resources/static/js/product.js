	function getSubCategory(selectedBrand){
		if(selectedBrand.value.trim() ===""){
			showError("Please Select Valid Brand!");
			return;
		}
		console.log(selectedBrand.value);
		try {
			adminController.getSubCategoryBasedOnBrand(selectedBrand.value,{
				 	callback: function(response) {
				       callBackSubCategoryBasedOnBrand(response);
				     }
				  });
		    }catch (e) {
				console.error(e);
		        showError("JS Exception while getting Sub Category: " + e);
		    }
	}

	function callBackSubCategoryBasedOnBrand(data) {
	    hideLoader();
	    hideMessage();
	    if (data.hasError) {
	        showError(data.errorList);
	    } else {
			let subCategory = document.getElementById('subCategory');
			let preSubCategoryValue = document.getElementById('prodSubCategoryId');
			console.log("preSubCategoryValue : " + preSubCategoryValue.value);
			let subCategoryList = data.subCategory;
			let htm = "<option value='' selected>Select Sub Category</option>"
			subCategory.innerHTML = htm; 
			for (let sub of subCategoryList) {
				let html ="";
				if(sub.id == preSubCategoryValue.value) {
					html += "<option value='" + sub.id + "' selected>" + sub.name + "</option>";
				} else {
					html += "<option value='" + sub.id + "'>" + sub.name + "</option>";
				}
				subCategory.innerHTML += html; 
			}
			subCategory.disabled = false;
	    }
	}
	
	function getCategory(selectedSubCategory){
		if(selectedSubCategory.value.trim() ===""){
			showError("Please Select Valid Sub Category!");
			return;
		}
		console.log(selectedSubCategory.value);
		try {
			adminController.getCategoryBasedOnSubCategory(selectedSubCategory.value,{
				 	callback: function(response) {
				       callBackCategoryBasedOnSubCategory(response);
				     }
				  });
		    }catch (e) {
				console.error(e);
		        showError("JS Exception while getting Category: " + e);
		    }
	}

	function callBackCategoryBasedOnSubCategory(data) {
	    hideLoader();
	    hideMessage();
	    if (data.hasError) {
	        showError(data.errorList);
	    } else {
			document.getElementById('categoryId').value = data.category.id;
			document.getElementById('prodCategory').value = data.category.name;
	    }
	}
	document.addEventListener('DOMContentLoaded', function() {
	    let brand = document.getElementById('brand');
	    let isSubCategoryLoaded = false;

	    if (brand.value.trim() !== "") {
	        getSubCategory(brand);
	        isSubCategoryLoaded = true;
	    }

	    // Wait a bit before calling the next step (so the first one finishes)
	    if (isSubCategoryLoaded) {
	        setTimeout(function() {
	            let subCategory = document.getElementById('subCategory');
	            if (subCategory && subCategory.value.trim() !== "") {
	                console.log("inside subCategory");
	                getCategory(subCategory);
	            }
	        }, 500); // wait 0.5 seconds (adjust if needed)
	    }
	});
