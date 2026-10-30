
function showLoader() {
    document.getElementById('loader').style.display = 'block';
}

function hideLoader() {
    document.getElementById('loader').style.display = 'none';
}

function showSuccess(message) {
        const box = document.getElementById("messageBox");
        const content = document.getElementById("messageContent");

        box.classList.remove("error");
        box.classList.add("success");
        box.style.display = "block";

        content.innerHTML = `<i class="fas fa-check-circle me-2"></i>${message}`;
    }

    // ✅ Show error message(s)
    function showError(errors) {
        const box = document.getElementById("messageBox");
        const content = document.getElementById("messageContent");

        box.classList.remove("success");
        box.classList.add("error");
        box.style.display = "block";

        if (Array.isArray(errors)) {
            const list = errors.map(err => `<li>${err}</li>`).join("");
            content.innerHTML = `<i class="fas fa-exclamation-triangle me-2"></i><ul>${list}</ul>`;
        } else {
            content.innerHTML = `<i class="fas fa-exclamation-triangle me-2"></i>${errors}`;
        }
    }

    // ✅ Hide message box
    function hideMessage() {
        const box = document.getElementById("messageBox");
        box.style.display = "none";
        document.getElementById("messageContent").innerHTML = "";
    }
