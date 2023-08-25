// xử lý btn thêm nhân viên mới
let image;
console.log(staffList);
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
    const username = document.getElementById('inputUsername').value;
    const password = document.getElementById('inputPassword').value;
    const address = document.getElementById('inputLocation').value;
    const email = document.getElementById('inputEmailAddress').value;
    const phone = document.getElementById('inputPhone').value;
    const birthday = document.getElementById('inputBirthday').value;
    const store = storeId;

    // Tạo một đối tượng
    const formData = new FormData();
    formData.append('fullName', fullName);
    formData.append('username', username);
    formData.append('password', password);
    formData.append('address', address);
    formData.append('email', email);
    formData.append('phone', phone);
    formData.append('birthday', birthday);
    formData.append("file", image);


    fetch("/staff/" + storeId + "/create", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (response.ok) {
                toastr.success("Thêm nhân viên thành công");
                setTimeout(function() {
                    location.reload();
                }, 1000);
            } else {
                toastr.error("Thêm nhân viên không thành công");
            }
        })
        .catch(error => {
            // Xử lý khi có lỗi xảy ra
            console.log("Error:", error);
        });
});

$(document).on('click', '.btn-view-detail', async function() {
    let staffId = $(this).closest('tr').data('staff-id');
    renderStaffDetailModal(staffId);
});

function renderStaffDetailModal(staffId) {
    let staffDetail;
    staffList.forEach(staff => {
        if (staff.id == staffId) {
            staffDetail = staff;
        }
    })

    let modalStaffDetail = `
    <div class="modal fade" id="detailStaffModal-${staffDetail.id}" tabindex="-1" aria-labelledby="detailStaffModalLabel"
       aria-hidden="true" data-bs-backdrop="static">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="detailStaffModalLabel">Thông tin nhân viên</h5>
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
                    <img class="img-account-profile-update rounded-circle mb-2" src="http://localhost:8080/${staffDetail.avatar != null ? staffDetail.avatar.id : 'images/default-user-icon-13.jpg'}"  alt="">
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
                  <div id="staff-${staffDetail.id}" class="card-body">
                      <div class="row gx-3 mb-3">
                        <div class="col-md-6">
                           <label class="small mb-1" for="fullName">Họ và tên</label>
                            <input class="form-control" id="fullName" type="text" placeholder="Nhập họ và tên" value="${staffDetail.fullName}">  
                        </div>
                        <div class="col-md-6">
                          <label class="small mb-1" for="username">Tên đăng nhập</label>
                          <input class="form-control" id="username" type="text" placeholder="Nhập tên đăng nhập" value="${staffDetail.username}">
                        </div>
                      </div>
                      <div class="mb-3">
                          <label class="small mb-1" for="address">Địa chỉ</label>
                          <input class="form-control" id="address" type="text" placeholder="Nhập địa chỉ" value="${staffDetail.address}">
                      </div>
                      <div class="row gx-3 mb-3">
                        <div class="col-md-6">
                          <label class="small mb-1" for="email">Email</label>
                          <input class="form-control" id="email" type="email" placeholder="Nhập email" value="${staffDetail.email}">
                        </div>
                        <div class="col-md-6">
                          <label class="small mb-1" for="phone">Số điện thoại</label>
                          <input class="form-control" id="phone" type="tel" placeholder="Nhập số điện thoại" value="${staffDetail.phone}">
                        </div>
                      </div>
                      <div class="row gx-3 mb-3">
                        <div class="col-md-6">
                          <label class="small mb-1" for="birthday">Ngày sinh</label>
                          <input class="form-control" id="birthday" type="text" name="birthday" placeholder="xx/xx/xxxx" value="${staffDetail.birthday}">
                        </div>
                        <div class="col-md-6">
                          <label class="small mb-1" >Ngày tạo</label>
                          <input class="form-control ignore" type="text" placeholder="xx/xx/xxxx" value="${formatDate(staffDetail.createdAt)}" disabled>
                        </div>
                      </div>
                      
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button id="btn-update-${staffDetail.id}" class="btn btn-primary float-end" type="button" >Lưu thay đổi</button>
          <button id="btn-delete-${staffDetail.id}" type="button" class="btn btn-danger btn-delete" >Xoá nhân viên</button>
        </div>
      </div>
    </div>
  </div>
                    `;
    $('.content-wrapper').append(modalStaffDetail);

    // Định vị các lớp modal
    $(`#detailStaffModal-${staffDetail.id}`).on('show.bs.modal', function () {
        $('.modal-backdrop').css('z-index', '1060');
    });

    $(`#detailStaffModal-${staffDetail.id}`).on('hidden.bs.modal', function () {
        $('.modal-backdrop').css('z-index', '1050');
    });

    $('#confirmModal').on('show.bs.modal', function () {
        $('.modal-backdrop').css('z-index', '1060');
    });

    $('#confirmModal').on('hidden.bs.modal', function () {
        $('.modal-backdrop').css('z-index', '1050');
    });

    $(`#detailStaffModal-${staffDetail.id}`).modal('show');



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
        const url = 'http://localhost:8080/staff/avatar' + staffDetail.id;

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
    function initializeFormUpdate(staffId) {
        let formValues = {};
        let formSelector = 'staff-' + staffId;
        let isFormChanged = false;
        $("#btn-update-" + staffId).prop('disabled', true);

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
                    $("#btn-update-" + staffId).prop('disabled', !isFormChanged);
                }
            });
        });
    }

// Gọi hàm initializeFormUpdate khi modal được mở
    initializeFormUpdate(staffDetail.id);

    $(`#btn-update-${staffDetail.id}`).off().on('click', function () {
        formUpdate.staffId = staffId;
        console.log(formUpdate)
        fetch("/staff/update", {
            method: 'PUT',
            headers : {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formUpdate)
        })
            .then(response => {
                if (response.ok) {
                    toastr.success("Cập nhật thông tin nhân viên thành công");
                    return response.json();
                }
                else {
                    toastr.error("Cập nhật thất bại")
                }
            })
            .then(res => {
                const staff = res;
                const staffRow = $(`tr[data-staff-id="${staff.id}"]`);
                const renderRow = `
                    <td>${staff.username}</td>
                    <td>${staff.fullName}</td>
                    <td>${staff.phone}</td>
                    <td><span class="${staff.status == false ? 'badge badge-danger' : 'badge badge-success'}">${staff.status == false ? 'Ngừng hoạt động' : 'Đang hoạt động'}</span></td>
                    <td>
                        <button class="btn btn-info btn-view-detail">Xem chi tiết
                        </button>
                        </td>
                `;

                staffRow.html(renderRow);
                $(`#detailStaffModal-${staff.id}`).modal('hide');
            })
    })



    $(`#btn-delete-${staffDetail.id}`).off().on('click', function (event) {
        event.preventDefault();
        $('#confirmModal').modal('show');
        $('#confirmDelete').off().on('click', function () {

            let deleteProductUrl = 'http://localhost:8080/staff/delete/' + staffId;
            fetch(deleteProductUrl, {
                method: 'DELETE'
            })
                .then(function (response) {
                    if (response.ok) {
                        toastr.success("Xoá nhân viên thành công");
                        $(`tr[data-staff-id="${staffId}"]`).remove();
                        $(`#detailStaffModal-${staffDetail.id}`).modal('hide');
                        // Xoá thành công, cập nhật giao diện

                    } else {
                        toastr.error("Sản phẩm cần xoá phải không phát sinh đơn hàng trong vòng 30 ngày.");
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






