// xử lý btn thêm nhân viên mới

let image;
console.log(clientList)
$('#imageInput').on('change', function(event) {
    let file = event.target.files[0];
    image = file;
    let reader = new FileReader();

    reader.onload = function(e) {
        $(".img-account-profile").attr("src", e.target.result);
    };

    reader.readAsDataURL(file);
});
document.getElementById("submitForm").addEventListener("submit",  function(event) {
    event.preventDefault();
    const fullName = document.getElementById('inputFullName').value;
    const address = document.getElementById('inputLocation').value;
    const email = document.getElementById('inputEmailAddress').value;
    const phone = document.getElementById('inputPhone').value;
    const birthday = document.getElementById('inputBirthday').value;
    const store = storeId;

    // Tạo một đối tượng
    const formData = new FormData();
    formData.append('fullName', fullName);
    formData.append('address', address);
    formData.append('email', email);
    formData.append('phone', phone);
    formData.append('birthday', birthday);
    formData.append("file", image);
    console.log(formData)


    fetch("/client/" + storeId + "/create", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (response.ok) {
                toastr.success("Thêm khách hàng thành công");
                setTimeout(function() {
                    location.reload();
                }, 500);
            } else {
                toastr.error("Thêm khách hàng không thành công");
            }
        })
        .catch(error => {
            // Xử lý khi có lỗi xảy ra
            console.log("Error:", error);
        });
});

$(document).on('click', '.btn-view-detail', async function() {
    let clientId = $(this).closest('tr').data('client-id');
    renderClientDetailModal(clientId);
});

function renderClientDetailModal(clientId) {
    let clientDetail;
    clientList.forEach(client => {
        if (client.id == clientId) {
            clientDetail = client;
        }
    })

    let modalClientDetail = `
    <div class="modal fade" id="detailClientModal-${clientDetail.id}" tabindex="-1" aria-labelledby="detailClientModalLabel"
       aria-hidden="true" data-bs-backdrop="static">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="detailClientModalLabel">Thông tin nhân viên</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="container-xl px-4 mt-4">
            <div class="row">
              <div class="col-xl-4">
                <!-- Profile picture card-->
                <div class="card mb-4 mb-xl-0">
                  <div class="card-header">Avatar</div>
                  <div class="card-body text-center">
                    <!-- Profile picture image-->
                    <img class="img-account-profile-update rounded-circle mb-2" src="http://localhost:8080/${clientDetail.avatar != null ? clientDetail.avatar.id : 'images/default-user-icon-13.jpg'}"  alt="">
                    <!-- Profile picture help block-->
                    <div class="small font-italic text-muted mb-4">JPG or PNG no larger than 5 MB</div>
                    <!-- Profile picture upload button-->
                    <button id="image-update"  onclick="document.getElementById('imageUpdate').click()" class="btn btn-primary" type="button">Tải ảnh mới
                      <input type="file" id="imageUpdate" style="display: none;" />
                    </button>
                  </div>
                </div>
              </div>
              <div class="col-xl-8">
                <!-- Account details card-->
                <div class="card mb-4">
                  <div class="card-header">Thông tin nhân viên</div>
                  <div class="card-body" id="client-${clientDetail.id}">
                      <div class="mb-3">
                           <label class="small mb-1" for="fullName">Họ và tên</label>
                            <input class="form-control" id="fullName" type="text" placeholder="Nhập họ và tên" value="${clientDetail.name}">  
                      </div>
   
                      <div class="mb-3">
                          <label class="small mb-1" for="address">Địa chỉ</label>
                          <input class="form-control" id="address" type="text" placeholder="Nhập địa chỉ" value="${clientDetail.address}">
                      </div>
                      <div class="row gx-3 mb-3">
                        <div class="col-md-6">
                          <label class="small mb-1" for="email">Email</label>
                          <input class="form-control" id="email" type="email" placeholder="Nhập email" value="${clientDetail.email}">
                        </div>
                        <div class="col-md-6">
                          <label class="small mb-1" for="phone">Số điện thoại</label>
                          <input class="form-control" id="phone" type="tel" placeholder="Nhập số điện thoại" value="${clientDetail.phone}">
                        </div>
                      </div>
                      <div class="row gx-3 mb-3">
                        <div class="col-md-6">
                          <label class="small mb-1" for="birthday">Ngày sinh</label>
                          <input class="form-control" id="birthday" type="text" name="birthday" placeholder="xx/xx/xxxx" value="${clientDetail.birthday}">
                        </div>
                        <div class="col-md-6">
                          <label class="small mb-1">Ngày tạo</label>
                          <input class="form-control" type="text" name="birthday" placeholder="xx/xx/xxxx" value="${formatDate(clientDetail.createdAt)}" disabled>
                        </div>
                      </div>
                      
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button id="btn-update-${clientDetail.id}" class="btn btn-primary float-end" type="button" >Lưu thay đổi</button>
          <button id="btn-delete-${clientDetail.id}" type="button" class="btn btn-danger btn-delete" >Xoá khách hàng</button>
        </div>
      </div>
    </div>
  </div>
                    `;
    $('.content-wrapper').append(modalClientDetail);

    // Định vị các lớp modal
    $(`#detailClientModal-${clientDetail.id}`).on('show.bs.modal', function () {
        $('.modal-backdrop').css('z-index', '1060');
    });

    $(`#detailClientModal-${clientDetail.id}`).on('hidden.bs.modal', function () {
        $('.modal-backdrop').css('z-index', '1050');
    });

    $('#confirmModal').on('show.bs.modal', function () {
        $('.modal-backdrop').css('z-index', '1060');
    });

    $('#confirmModal').on('hidden.bs.modal', function () {
        $('.modal-backdrop').css('z-index', '1050');
    });

    $(`#detailClientModal-${clientDetail.id}`).modal('show');

    // Xử lý sự kiện khi nhấp vào nút "Thêm ảnh"
    $('#imageUpdate').off().on('change', function (event) {
        let file = event.target.files[0];
        let reader = new FileReader();

        reader.onload = function(e) {
            $(".img-account-profile-update").attr("src", e.target.result);
        };

        reader.readAsDataURL(file);

        // Tạo FormData để chứa dữ liệu của các tệp
        let formData = new FormData();
        formData.append('file', file);
        const url = 'http://localhost:8080/client/avatar/' + clientDetail.id;

        fetch(url, {
            method: 'POST',
            body: formData
        })
            .then(function (response) {
                if (response.ok) {
                    toastr.success("Thay đổi avatar thành công");
                } else {
                    toastr.error("Upload ảnh thất bại");
                    throw new Error('Error: ' + response.status);
                }
            })

    });

    let formUpdate = {};
    function initializeFormUpdate(clientId) {
        let formValues = {};
        let formSelector = 'client-' + clientId;
        let isFormChanged = false;
        $("#btn-update-" + clientId).prop('disabled', true);

        $('#' + formSelector + ' input:not(.ignore)').each(function () {
            let fieldId = $(this).attr('id');
            formValues[fieldId] = $(this).val();

            $(this).on('input', function () {
                let newIsFormChanged = false;

                // Kiểm tra các trường input nếu có sự thay đổi so với giá trị ban đầu
                $('#' + formSelector + ' input:not(.ignore)').each(function () {
                    let fieldId = $(this).attr('id');
                    if ($(this).val() !== formValues[fieldId]) {
                        formUpdate[fieldId] = $(this).val();
                        newIsFormChanged = true;
                    }
                });

                console.log(formUpdate)

                // Nếu trạng thái thay đổi có sự thay đổi so với trạng thái trước đó
                // thì cập nhật giá trị của isFormChanged
                if (newIsFormChanged !== isFormChanged) {
                    isFormChanged = newIsFormChanged;
                    // Đặt trạng thái của button cập nhật dựa trên giá trị của isFormChanged
                    $("#btn-update-" + clientId).prop('disabled', !isFormChanged);
                }
            });
        });
    }

// Gọi hàm initializeFormUpdate khi modal được mở
    initializeFormUpdate(clientDetail.id);

    $(`#btn-update-${clientDetail.id}`).off().on('click', function () {
        formUpdate.clientId = clientId;
        console.log(formUpdate)
        fetch("/client/update", {
            method: 'PUT',
            headers : {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formUpdate)
        })
            .then(response => {
                if (response.ok) {
                    toastr.success("Cập nhật thông tin khách hàng thành công");
                    return response.json();
                }
                else {
                    toastr.error("Cập nhật thất bại")
                }
            })
            .then(res => {
                const client = res;
                const clientRow = $(`tr[data-client-id="${client.id}"]`);
                const renderRow = `
                    <td>${client.name}</td>
                    <td>${client.email}</td>
                    <td>${client.phone}</td>
                    <td>${client.order.length}</td>
                    <td>
                        <button class="btn btn-info btn-view-detail">Xem chi tiết
                        </button>
                        </td>
                `;

                clientRow.html(renderRow);
                $(`#detailClientModal-${client.id}`).modal('hide');
            })
    })



    $(`#btn-delete-${clientDetail.id}`).off().on('click', function (event) {
        event.preventDefault();
        $('#confirmModal').modal('show');
        $('#confirmDelete').off().on('click', function () {

            let deleteProductUrl = '/client/delete/' + clientId;
            fetch(deleteProductUrl, {
                method: 'DELETE'
            })
                .then(function (response) {
                    if (response.ok) {
                        toastr.success("Xoá khách hàng thành công");
                        $(`tr[data-client-id="${clientId}"]`).remove();
                        $(`#detailClientModal-${clientId}`).modal('hide');
                        // Xoá thành công, cập nhật giao diện

                    } else {
                        toastr.error("Xoá khách hàng thất bại.");
                        throw new Error('Error: ' + response.status);
                    }
                })
                .catch(function (error) {
                    console.log(error);
                })
                .finally(function () {
                    $('#confirmModal').modal('hide');
                });
        });
    })

}

// $('input[data-toggle="toggle"]').bootstrapToggle();