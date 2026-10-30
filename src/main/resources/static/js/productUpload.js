
        const uploadArea = document.getElementById('uploadArea');
        const fileInput = document.getElementById('fileInput');
        const fileInfo = document.getElementById('fileInfo');
        const fileName = document.getElementById('fileName');
        const fileSize = document.getElementById('fileSize');
        const uploadBtn = document.getElementById('uploadBtn');
        const removeFileBtn = document.getElementById('removeFile');

        let selectedFile = null;

        // Click to browse
        uploadArea.addEventListener('click', () => fileInput.click());

        // File selection
        fileInput.addEventListener('change', (e) => {
            handleFile(e.target.files[0]);
        });

        // Drag and drop
        uploadArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            uploadArea.classList.add('dragover');
        });

        uploadArea.addEventListener('dragleave', () => {
            uploadArea.classList.remove('dragover');
        });

        uploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadArea.classList.remove('dragover');
            handleFile(e.dataTransfer.files[0]);
        });

        function handleFile(file) {
            hideMessage();

            if (!file) return;

            // Validate file type
            const validTypes = ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'];
            const fileExtension = file.name.split('.').pop().toLowerCase();

            if (!validTypes.includes(file.type) && !['xls', 'xlsx'].includes(fileExtension)) {
                showError('Please select a valid Excel file (.xls or .xlsx)');
                return;
            }

            // Validate file size (max 10MB)
            if (file.size > 10 * 1024 * 1024) {
                showError('File size must be less than 10MB');
                return;
            }

            selectedFile = file;
            fileName.textContent = file.name;
            fileSize.textContent = formatFileSize(file.size);
            fileInfo.style.display = 'block';
            uploadBtn.disabled = false;
        }

        function formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
        }

        removeFileBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            selectedFile = null;
            fileInput.value = '';
            fileInfo.style.display = 'none';
            uploadBtn.disabled = true;
            hideMessage();
        });

    
      uploadBtn.addEventListener('click', () => {
          if (!selectedFile) return;
          uploadProductData();
      });
	  
	  function uploadProductData() {
	            try {
	                showLoader();			  
	  			  let fileInp = document.getElementById('fileInput');
	  			  let selectFile = fileInp.files[0];

	  			  let reader = new FileReader();
	  			  reader.onload = function(e) {
	  			      let base64Data = e.target.result.split(",")[1]; // remove data: prefix
	  			      adminController.saveUploadedProductData(selectFile.name, base64Data, {
	  			          callback: function(response) {
	  			              hideLoader();
	  			              callBackSaveProductData(response);
	  			          }
	  			      });
	  			  };
	  			  reader.readAsDataURL(selectFile);
	            } catch (e) {
	                hideLoader();
	                showError("Exception: " + e);
	            }
	        }

      function callBackSaveProductData(data) {
          hideLoader();
          hideMessage();
          console.log(data);
          if (data.hasError && data.error === "error") {
              showError(data.errorList);
          } else {
              showSuccess(data.successMsg);
          }
      }


