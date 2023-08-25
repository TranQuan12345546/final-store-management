
document.getElementById("submitForm").addEventListener("submit",  function(event) {
    event.preventDefault();
    const fullName = document.getElementById('inputFullName').value;
    const address = document.getElementById('inputLocation').value;
    const email = document.getElementById('inputEmailAddress').value;
    const phone = document.getElementById('inputPhone').value;

    // Tạo một đối tượng
    const createSupplier = {
        name: fullName,
        email: email,
        phone: phone,
        address: address
    }


    fetch("/supplier/" + storeId + "/create", {
        method: "POST",
        headers : {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(createSupplier)
    })
        .then(response => {
            if (response.ok) {
                toastr.success("Thêm nhà cung cấp thành công");
                setTimeout(function() {
                    location.reload();
                }, 500);
            } else {
                toastr.error("Thêm nhà cung cấp không thành công");
            }
        })
        .catch(error => {
            // Xử lý khi có lỗi xảy ra
            console.log("Error:", error);
        });
});