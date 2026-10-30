	document.addEventListener("DOMContentLoaded", function() {
	    const params = new URLSearchParams(window.location.search);
	    const redirectMsg = params.get("redirectMsg");
	    if (redirectMsg) {
			showSuccess(decodeURIComponent(redirectMsg));
	    }
	});

	function saveProductCategory(isForEdit) {
		hideMessage();		  
  		let categoryName = document.getElementById('categoryName').value;
		let categoryDesc = document.getElementById('categoryDesc').value;
		let categoryId  = document.getElementById('categoryId').value;
		if(categoryName ===""){
			showError("Please Enter Category Name!");
			return;
		}
		if(categoryDesc ===""){
			showError("Please Enter Category Description!");
			return;
		}
		showLoader();	
		try {
  			adminController.saveProductCategory(categoryId,categoryName, categoryDesc,isForEdit, {
  			 	callback: function(response) {
  			       callBackProductCategory(response,isForEdit);
  			     }
  			  });
        }catch (e) {
            hideLoader();
            showError("Exception while Saving Category: " + e);
        }
    }

    function callBackProductCategory(data,isForEdit) {
        hideLoader();
        hideMessage();
        if (data.hasError) {
            showError(data.errorList);
        } else {
			if(isForEdit){
				window.location.href = '/admin/products/showAllProductCategories?redirectMsg='+data.successMsg;
			}else{
				showSuccess(data.successMsg);
				document.getElementById("categoryName").value = "";
				document.getElementById("categoryDesc").value = "";
			}
        }
    }
	
	function deleteCategory(element) {
		hideMessage();		  
	  	let categoryId = element.value;
		if(categoryId ===""){
			showError("Something Went Wrong! Category not came Properly.");
			return;
		}
		showLoader();	
		try {
	  		adminController.deleteProdCategory(categoryId, {
	  			callback: function(response) {
	  			   callBackDeleteProdCategory(response);
	  			   }
	  		});
	    }catch (e) {
	       hideLoader();
	       showError("Exception while Deleting Category: " + e);
	    }
	}
		
	function callBackDeleteProdCategory(data) {
		hideLoader();
		hideMessage();
		if (data.hasError) {
		   	showError(data.errorList);
		} else {
			window.location.href = '/admin/products/showAllProductCategories?redirectMsg='+data.successMsg;
		}
	}
	