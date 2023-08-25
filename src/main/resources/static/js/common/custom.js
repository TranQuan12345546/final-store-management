// Khi trang được tải hoàn tất
$(document).ready(function() {
    const menuLinks = $(".sub-menu li.nav-item a.nav-link");
    const dashboard = $(".sidebar .main-category:nth-child(1) a.nav-link");
    const mainCategories = $(".sidebar li.main-category");
    const divCollapse = $("div.collapse");
    // Xóa lớp active từ tất cả các liên kết
    menuLinks.removeClass("active");
    mainCategories.removeClass("active")
    divCollapse.removeClass("show");
    const windowPath = window.location.pathname;

    if (windowPath == dashboard.attr("href")) {
        dashboard.closest("li.main-category").addClass("active")
    } else {
        menuLinks.map(function () {
            if (windowPath == $(this).attr("href")) {
                $(this).addClass("active")
                $(this).closest("li.main-category").addClass("active");
                $(this).closest("div.collapse").addClass("show")
            }
        })
    }

});

function formatDate(dateString) {
    let date = new Date(dateString);
    let day = date.getDate();
    let month = date.getMonth() + 1;
    let year = date.getFullYear();

    let hours = date.getHours().toString().padStart(2, '0');
    let minutes = date.getMinutes().toString().padStart(2, '0');

    if (day < 10) {
        day = "0" + day;
    }

    return day + "/" + month + "/" + year + " " + hours + ":" + minutes;
}

// lấy ra id cửa hàng
const path = window.location.pathname;
const parts = path.split("/");
const storeId = parts[2];

// Lấy ra id người đang đăng nhập
const userId = $('#userId').data('user-id');

$(document).ready(function () {
    $('#info-detail').on('click', function (event) {
        event.preventDefault();
        $('#userLoginDetail').modal('show')

        $('#imageUserUpdate').off().on('change', function (event) {
            let file = event.target.files[0];
            let reader = new FileReader();

            reader.onload = function(e) {
                $(".img-account-update").attr("src", e.target.result);
            };

            reader.readAsDataURL(file);

            // Tạo FormData để chứa dữ liệu của các tệp
            let formData = new FormData();
            formData.append('file', file);
            const url = 'http://localhost:8080/avatar/' + userId;

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

    })
})
