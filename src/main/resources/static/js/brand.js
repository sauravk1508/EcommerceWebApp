document.addEventListener("DOMContentLoaded", function() {
    const params = new URLSearchParams(window.location.search);
    const redirectMsg = params.get("redirectMsg");
    if (redirectMsg) {
		showSuccess(decodeURIComponent(redirectMsg));
    }
});	
function saveBrand(isForEdit) {
	hideMessage();
			  
	let brandName = document.getElementById('brandName').value;
	let brandDesc = document.getElementById('brandDesc').value;
	let prodSubCategory = Array.from(document.getElementById("prodSubCategory").selectedOptions)
	                         .map(option => option.value);
	if(brandName.trim() ===""){
		showError("Please Enter Brand Name!");
		return;
	}
	if(brandDesc.trim() ===""){
		showError("Please Enter Brand Description!");
		return;
	}
	if (!prodSubCategory || prodSubCategory.length === 0) {
	    showError("Please select at least one sub-category!");
	    return;
	}
	showLoader();	
	try {
		adminController.saveProdBrand(brandName, brandDesc, prodSubCategory,isForEdit,{
		 	callback: function(response) {
		       callBackSaveBrand(response,isForEdit);
		     }
		  });
    }catch (e) {
        hideLoader();
		console.error(e);
        showError("Exception while Saving Brand: " + e);
    }
}

function callBackSaveBrand(data,isForEdit) {
    hideLoader();
    hideMessage();
    console.log(data);
    if (data.hasError) {
        showError(data.errorList);
    } else {
		if(isForEdit){
			window.location.href = '/admin/products/showAllBrands?redirectMsg='+data.successMsg;
		}else{
			showSuccess(data.successMsg);
			document.getElementById("brandName").value = "";
			document.getElementById("brandDesc").value = "";
			document.getElementById("prodSubCategory").value = "";
		}
    }
}

function deleteBrand(element) {
		hideMessage();		  
	  	let brandId = element.value;
		if(brandId ===""){
			showError("Something Went Wrong! Category not came Properly.");
			return;
		}
		showLoader();	
		try {
	  		adminController.deleteBrand(brandId, {
	  			callback: function(response) {
	  			   callBackDeleteBrand(response);
	  			   }
	  		});
	    }catch (e) {
	       hideLoader();
	       showError("Exception while Deleting Category: " + e);
	    }
}

function callBackDeleteBrand(data) {
	hideLoader();
	hideMessage();
	if (data.hasError) {
		showError(data.errorList);
	} else {
		window.location.href = '/admin/products/showAllBrands?redirectMsg='+data.successMsg;
	}
}
	

