document.addEventListener("DOMContentLoaded", function() {
    const params = new URLSearchParams(window.location.search);
    const redirectMsg = params.get("redirectMsg");
    if (redirectMsg) {
		showSuccess(decodeURIComponent(redirectMsg));
    }
});	

function saveProductSubCategory(isForEdit) {
		hideMessage();
				  
		let subCategoryName = document.getElementById('subCategoryName').value;
		let subCategoryDesc = document.getElementById('subCategoryDesc').value;
		let prodCategory = document.getElementById("prodCategory").value;
		if(subCategoryName.trim() ===""){
			showError("Please Enter Name!");
			return;
		}
		if(subCategoryDesc.trim() ===""){
			showError("Please Enter Description!");
			return;
		}
		if(prodCategory ===""){
			showError("Please Select Category!");
			return;
		}
		showLoader();	
		try {
			adminController.saveSubCategory(subCategoryName, subCategoryDesc, prodCategory,isForEdit,{
			 	callback: function(response) {
			       callBackSaveSubCategory(response,isForEdit);
			     }
			  });
	    }catch (e) {
	        hideLoader();
			console.error(e);
	        showError("Exception while Saving Brand: " + e);
	    }
	}

	function callBackSaveSubCategory(data,isForEdit) {
	    hideLoader();
	    hideMessage();
	    console.log(data);
	    if (data.hasError) {
	        showError(data.errorList);
	    } else {
			if(isForEdit){
				window.location.href = '/admin/products/showAllProductSubCategories?redirectMsg='+data.successMsg;
			}else{
				showSuccess(data.successMsg);
				document.getElementById("subCategoryName").value = "";
				document.getElementById("subCategoryName").value = "";
				document.getElementById("prodCategory").value = "";
			}
	    }
	}

	function deleteSubCategory(element) {
		hideMessage();		  
	  	let subCategoryId = element.value;
		if(subCategoryId ===""){
			showError("Something Went Wrong! Sub-Category not came Properly.");
			return;
		}
		showLoader();	
		try {
	  		adminController.deleteProdSubCategory(subCategoryId, {
	  			callback: function(response) {
	  			   callBackDeleteProdSubCategory(response);
	  			   }
	  		});
	    }catch (e) {
	       hideLoader();
	       showError("Exception while Deleting Sub-Category: " + e);
	    }
	}

	function callBackDeleteProdSubCategory(data) {
		hideLoader();
		hideMessage();
		if (data.hasError) {
		   	showError(data.errorList);
		} else {
			window.location.href = '/admin/products/showAllProductSubCategories?redirectMsg='+data.successMsg;
		}
	}