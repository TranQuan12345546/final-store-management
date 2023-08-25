const spinnerElement = $('<div>').addClass('spinner-border').attr('role', 'status');
spinnerElement.append('<span class="visually-hidden">Loading...</span>');

// xử lý checkbox
$("tbody tr").click(function(event) {
    if ($(event.target).is("input[type='checkbox']")) {
        return;
    }
    const checkbox = $(this).find("input[type='checkbox']");
    checkbox.prop("checked", !checkbox.prop("checked"));
});

$(document).ready(function() {
    $("#checkAll").click(function() {
        let isChecked = $(this).prop("checked");

        $("tbody input[type='checkbox']").prop("checked", isChecked);
    });
});

// Hover vào thumbnail
$(document).ready(function() {
    $(".thumbnail").hover(
        function() {
            $(this).siblings(".overlay").fadeIn();
        },
        function() {
            $(this).siblings(".overlay").hide();
        }
    );
    let rowCount = $("table tr").length;
    if (rowCount > 5) {
        let lastTwoRow =  $("table tr:last-child, table tr:nth-last-child(2)");
        lastTwoRow.find(".thumbnail").siblings(".overlay").css("transform", "translate(0, -100%)");
    }
});

// Xử lý Modal addProduct
// Hiển thị ảnh lên giao diện
const imageInputs = document.querySelectorAll('.image-input');
const images = [];
imageInputs.forEach(function(input) {
    input.addEventListener('change', function(event) {
        let file = event.target.files[0];
        if (file) {
            images.push(file);
        }
        let reader = new FileReader();
        let imagePreview = input.nextElementSibling.querySelector('.image-preview');

        reader.onload = function(e) {
            imagePreview.style.backgroundImage = 'url(' + e.target.result + ')';
        };

        reader.readAsDataURL(file);
    });
});

// upload dữ liệu product lên server



// add Product
$(document).ready(function () {
    $('#submitForm').validate({
        rules: {
            insertName: {
                required: true,
                minlength: 3
            },
            insertInitialPrice: {
                required: true,
                number: true
            },
            insertSalePrice: {
                required: true,
                number: true
            },
            insertQuantity: {
                required: true,
                number: true
            },
            insertGroupProduct: {
                required: true
            }
        },
        messages: {
            insertName: {
                required: "Vui lòng nhập tên sản phẩm.",
                minlength: "Tên sản phẩm phải chứa ít nhất 3 ký tự."
            },
            insertInitialPrice: {
                required: "Vui lòng nhập giá vốn sản phẩm.",
                number: "Vui lòng nhập số."
            },
            insertSalePrice: {
                required: "Vui lòng nhập giá bán sản phẩm.",
                number: "Vui lòng nhập số."
            },
            insertQuantity: {
                required: "Vui lòng nhập số lượng sản phẩm.",
                number: "Vui lòng nhập số."
            },
            insertGroupProduct: {
                required: "Bạn cần chọn nhóm hàng cho sản phẩm."
            },
            imageInput: {
                accept: "Vui lòng chọn một tệp tin hình ảnh đúng định dạng"
            },
        }
    });



    $('#submitForm').on('submit', async function(event) {
        event.preventDefault();
        const codeNumber = $('#insertCodeNumber').val();
        const name = $('#insertName').val();
        const initialPrice = $('#insertInitialPrice').val();
        const salePrice = $('#insertSalePrice').val();
        const quantity = $('#insertQuantity').val();
        const description = $('#insertProductDescription').val();
        const note = $('#insertProductNote').val();
        const supplier = $('#insertSupplierSelect').val();
        const groupProduct = $('#insertGroupProduct').val();



        const formData = new FormData();
        formData.append('codeNumber', codeNumber);
        formData.append('name', name);
        formData.append('initialPrice', initialPrice);
        formData.append('salePrice', salePrice);
        formData.append('quantity', quantity);
        formData.append('description', description);
        formData.append('note', note);
        if (supplier != null) {
            formData.append('supplier', supplier);
        }
        formData.append('groupProduct', groupProduct);
        formData.append("userId", userId);
        // Thêm tệp tin hình ảnh vào FormData
        images.forEach(function(image) {
            formData.append("file", image);
        });

        formData.forEach(function(value, key) {
            console.log(key, value);
        });


        await fetch("/products/" + storeId + "/create", {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    toastr.success("Tạo sản phẩm thành công");
                    setTimeout(function() {
                        location.reload();
                    }, 1000);
                    console.log("Request successful");
                } else {
                    toastr.error("Tạo sản phẩm không thành công");
                    console.log("Request failed");
                }
            })
            .catch(error => {
                // Xử lý khi có lỗi xảy ra
                console.log("Error:", error);
            });
    })
})

// xử lý productDetail mới
let productHandling;
$(document).on('click', '.btn-view-detail', async function() {
    let productId = $(this).closest('tr').data('product-id');
    await renderProductDetailModal(productId);
});

async function renderProductDetailModal(productId) {
    await fetch('http://localhost:8080/products/productDetail/' + productId)
        .then(function (response) {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Error: ' + response.status);
        })
        .then(function (product) {
            productHandling = product;
            let groupProductOptions = groupProductList.map(groupProduct => {
                return `<option value="${groupProduct.id}" ${groupProduct.name === product.groupProduct.name ? 'selected' : ''}>${groupProduct.name}</option>`;
            }).join('');

            let supplierOptions = supplierList.map(supplier => {
                return `<option value="${supplier.id}" ${supplier.name === product.supplier.name ? 'selected' : ''}>${supplier.name}</option>`;
            }).join('');

            let modalProductDetail = `
                    <div id="productModal-${product.id}" class="modal fade modal-detail" tabindex="-1" aria-labelledby="productModalLabel"
                         aria-hidden="false" data-product-id="${product.id}">
                      <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                          <form id="form-update${product.id}">
                          <div class="modal-header">
                            <h5 class="modal-title fw-bolder" id="productModalLabel2">Thông tin chi tiết sản phẩm</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                          </div>
                          <div class="modal-body">
                            <!-- Thông tin chi tiết sản phẩm -->
                            <div class="row">
                              <div class="col-xl-4">
                                <div class="card mb-4 mb-xl-0" style="height: 100%">
                                  <div class="card-header fw-bolder">Hình ảnh sản phẩm</div>
                                  <div class="card-body text-center position-relative" style="min-width: 250px; min-height: 250px">
                                    <div class="image-container-detail">
                                      <button class="remove-image-btn"  title="Xoá ảnh">
                                        <i class="typcn typcn-delete"></i>
                                      </button>
                                      <img class="img-account-profile rounded-2 mb-2"
                                           src="/images/image-default.jpg" alt="product image">
                                      <div class="image-dots">
                                      </div>
                                        <button id="prevImage" class="btn btn-previous position-absolute" type="button"><i class="typcn typcn-arrow-left-thick"></i></button>
                                        <button id="nextImage" class="btn btn-next position-absolute" type="button"><i class="typcn typcn-arrow-right-thick"></i></button>
                                    </div>
                                    <div class="small font-italic text-muted mb-4">JPG or PNG no larger than 5 MB
                                    </div>
                                    
                                  
                                    
                                    <button  onclick="document.getElementById('imageInput').click()" id="btn-update-image" class="btn btn-primary" type="button">Tải ảnh mới
                                      <input type="file" id="imageInput" style="display: none;" />
                                    </button>
                
                                  </div>
                                </div>
                              </div>
                              <div class="col-xl-8">
                                <div class="card mb-4">
                                  <div class="card-header fw-bolder">Chi tiết sản phẩm</div>
                                  <div class="card-body">
                
                                      <div class="mb-3"><label class="small mb-1 fw-bolder" for="name">Tên sản phẩm</label>
                                        <input class="form-control p-2" id="name" type="text"
                                              placeholder="Nhập tên sản phẩm" value="${product.name}"></div>
                                      <div class="row gx-3 mb-3">
                                        <div class="col-md-4">
                                          <label class="small mb-1 fw-bolder" for="codeNumber">Mã sản phẩm</label>
                                          <span class="form-control p-2" id="codeNumber"
                                                placeholder="code"
                                                data-product-code="${product.codeNumber}">${product.codeNumber}</span> </div>
                                        <div class="col-md-4"><label class="small mb-1 fw-bolder" for="initialPrice">Giá vốn</label>
                                          <input class="form-control p-2" id="initialPrice"
                                                              type="text" placeholder=""
                                                              value="${product.initialPrice}"></div>
                                        <div class="col-md-4">
                                          <label class="small mb-1 fw-bolder" for="salePrice">Giá bán</label>
                                          <input class="form-control p-2" id="salePrice"
                                                 type="text"
                                                 placeholder=""
                                                 value="${product.salePrice}"></div>
                                      </div>
                                      <div class="row gx-3 mb-3">
                                        <div class="col-md-4">
                                          <label class="small mb-1 fw-bolder" for="quantity">Tồn kho</label>
                                          <input class="form-control p-2" id="quantity" type="number"
                                                 placeholder="" value="${product.quantity}">
                                        </div>
                                        <div class="col-md-4" style="position: relative">
                                          <label class="small mb-1 fw-bolder" for="groupProduct">Nhóm hàng</label>
                                          <select class="form-control p-2" id="groupProduct">
                                              ${groupProductOptions}
                                          </select>
                                           <i class="typcn typcn-arrow-sorted-down"></i>
                                        </div>
                                        <div class="col-md-4" style="position: relative">
                                          <label class="small mb-1 fw-bolder" for="supplier">Nhà cung cấp</label>
                                          <select class="form-control p-2" id="supplier"
                                                  type="text"
                                                  placeholder="">
                                              ${supplierOptions}
                                          </select>
                                           <i class="typcn typcn-arrow-sorted-down"></i>
                                        </div>
                                      </div>
                                      <div class="mb-3"><label class="small mb-1 fw-bolder" for="description">Mô tả</label>
                                        <textarea class="form-control p-2" id="description"
                                                  placeholder="Nhập mô tả sản phẩm" text="${product.description}">
                                        </textarea>
                                      </div>
                                      <div class="row gx-3 mb-3">
                                        <div class="col-md-6">
                                          <label class="small mb-1 fw-bolder" for="note">Ghi chú</label>
                                          <input class="form-control form-control-plaintext p-2" id="note"
                                                                type="tel"
                                                                placeholder=""
                                                                value="${product.note}"></div>
                                        <div class="col-md-6"><label class="small mb-1 fw-bolder" >Ngày tạo</label>
                                          <span class="form-control-plaintext py-1">${formatDate(product.createdAt)}</span>
                                        </div>
                                      </div>
                
                
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                          <div class="modal-footer">
                            <button id="btn-update-${product.id}" class="btn btn-primary float-end" type="button" >Lưu thay đổi</button>
                            <button id="btn-delete-${product.id}" type="button" class="btn btn-danger btn-delete" >Xoá mặt hàng</button>
                          </div>
                          </form>
                        </div>
                      </div>
                    </div>
                    `;
            $('.content-wrapper').append(modalProductDetail);
            $(`#productModal-${product.id}`).modal('show');
        })

    const imagesDelete = [];
    $(`#productModal-${productHandling.id}`).on('shown.bs.modal', function () {
        let product = productHandling;
        let productId = productHandling.id;
        let productImage = $(this).find('.img-account-profile');
        let prevButton = $(this).find('#prevImage');
        let nextButton = $(this).find('#nextImage');
        let imagesPath = [];
        updateDeleteButtonStatus();
        $(product.images).each(function (index, element) {
            imagesDelete.push(element.id);
            let path1 = "http://localhost:8080/" + element.id;
            imagesPath.push(path1);
        });
        let currentIndex = 0; // Chỉ số ban đầu của hình ảnhz
        // Hiển thị hình ảnh đầu tiên
        productImage.attr('src', imagesPath[currentIndex]);
        updateDeleteButtonStatus();

        if (imagesPath.length <= 1) {
            prevButton.prop('disabled', true);
            nextButton.prop('disabled', true);
        } else {
            prevButton.prop('disabled', false);
            nextButton.prop('disabled', false);
        }

        let dotsContainer = $(".image-dots");
        if (dotsContainer.children().length === 0) {
            if (imagesPath.length > 0) {
                const dot = $("<span class='dot'></span>");
                dotsContainer.append(dot);
                $(".dot").addClass("active");
            }

            for (let i = 0; i < product.images.length - 1; i++) {
                let dot = $("<span class='dot'></span>");
                dotsContainer.append(dot);
            }
        }
        // Xử lý sự kiện khi ấn nút "Previous"
        prevButton.on('click', function () {
            currentIndex = (currentIndex - 1 + imagesPath.length) % imagesPath.length;
            productImage.attr('src', imagesPath[currentIndex]);
            updateImageDots();
        });

        // Xử lý sự kiện khi ấn nút "Next"
        nextButton.on('click', function () {
            currentIndex = (currentIndex + 1) % imagesPath.length;
            productImage.attr('src', imagesPath[currentIndex]);
            updateImageDots();
        });


        //update dot
        function updateImageDots() {
            $(".dot").removeClass("active");
            $(".dot").eq(currentIndex).addClass("active");
        }


        //upload ảnh
        // Xử lý sự kiện khi nhấp vào nút "Thêm ảnh"
        $('#imageInput').off().on('change', function (event) {
            event.preventDefault();
            let files = $(this)[0].files;

            // Tạo FormData để chứa dữ liệu của các tệp
            let formData = new FormData();
            for (let i = 0; i < files.length; i++) {
                formData.append('files', files[i]);
            }
            const url = 'http://localhost:8080/product' + productId;

            fetch(url, {
                method: 'POST',
                body: formData
            })
                .then(function (response) {
                    if (response.ok) {
                        toastr.success("Upload ảnh thành công");
                        updateDeleteButtonStatus();
                        return response.json();
                    }
                    toastr.error("Upload ảnh không thành công");
                    throw new Error('Error: ' + response.status);
                })
                .then(function (data) {
                    // Nhận thông tin về các ảnh đã tải lên từ phản hồi
                    $(data).each(function (index, element) {
                        let path = "http://localhost:8080/" + element.id;
                        imagesPath.push(path);
                        imagesDelete.push(element.id);
                    });
                    console.log(imagesPath);
                    // Cập nhật giao diện với ảnh mới
                    let dotsContainer = $('.image-dots');

                    // Tạo một dot mới cho ảnh mới
                    let dot = $('<span class="dot"></span>');
                    dotsContainer.append(dot);

                    // Cập nhật đường dẫn ảnh và chỉ số hiện tại
                    let currentIndex = imagesPath.length - 1;
                    productImage.attr('src', imagesPath[currentIndex]);

                    // Cập nhật dot hiện tại
                    updateImageDots();
                })
        });


        // xử lý btn xoá ảnh
        $(document).on('click', '.remove-image-btn', function (event) {
            event.preventDefault();
            $('#confirmModal').modal('show');
            $('#confirmDelete').off().on('click', function () {
                let index = currentIndex;
                // Gọi API để xoá ảnh từ server, dựa trên chỉ số của ảnh
                let imageId = imagesDelete[index];
                let deleteImageUrl = 'http://localhost:8080/' + imageId;
                fetch(deleteImageUrl, {
                    method: 'DELETE'
                })
                    .then(function (response) {
                        if (response.ok) {
                            toastr.success("Xoá ảnh thành công");
                            // Xoá thành công, cập nhật giao diện
                            // Xoá dot cuối cùng
                            $('.dot:last').remove();

                            // Xoá ảnh khỏi mảng imagesPath
                            imagesPath.splice(index, 1);

                            // Xoá ảnh khỏi sản phẩm
                            imagesDelete.splice(index, 1);

                            // Cập nhật chỉ số hiện tại
                            currentIndex = (currentIndex - 1 + imagesPath.length) % imagesPath.length;

                            // Cập nhật giao diện với ảnh mới và dot hiện tại
                            productImage.attr('src', imagesPath[currentIndex]);
                            updateDeleteButtonStatus();
                            updateImageDots();

                            if (imagesPath.length === 0) {
                                productImage.attr('src', "/images/image-default.jpg");
                                updateDeleteButtonStatus();
                            }
                        } else {
                            toastr.error("Xoá ảnh thất bại " + response.status);
                            throw new Error('Error: ' + response.status);
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
                $('#confirmModal').modal('hide');
            });
        });

        function updateDeleteButtonStatus() {
            let imageCount = imagesPath.length; // Số lượng ảnh còn lại
            let deleteButton = $('.remove-image-btn');

            if (imageCount > 0) {
                deleteButton.prop('disabled', false);
                deleteButton.removeClass('disabled-button'); // Xóa lớp disabled-button nếu cần thiết
            } else {
                deleteButton.prop('disabled', true);
                deleteButton.addClass('disabled-button'); // Thêm lớp disabled-button để ẩn hiệu ứng hover
            }
        }

    })

// xử lý btn xoá sản phẩm
    $("#btn-delete-" + productId).on('click', function (event) {
        event.preventDefault();
        $('#confirmModal').modal('show');
        $('#confirmDelete').off().on('click', function () {
            // Gọi API để xoá sản phẩm

            let deleteProductUrl = 'http://localhost:8080/products/delete/' + productId;
            fetch(deleteProductUrl, {
                method: 'DELETE'
            })
                .then(function (response) {
                    if (response.ok) {
                        toastr.success("Xoá sản phẩm thành công");
                        $(`tr[data-product-id="${productId}"]`).remove();
                        $('.modal-detail').modal('hide');
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
    });

// handle btn update product enabled/disabled when input change
    function initializeFormUpdate() {
        let formValues = {};

        let formSelector = 'form-update' + productId;
        $("#btn-update-" + productId).prop('disabled', true);

        $('#' + formSelector + ' input, #' + formSelector + ' textarea, #' + formSelector + ' select').each(function () {
            let fieldId = $(this).attr('id');
            formValues[fieldId] = $(this).val();
        });

        $('#' + formSelector + ' input, #' + formSelector + ' textarea, #' + formSelector + ' select').on('input', function () {
            let isFormChanged = false;

            $('#' + formSelector + ' input, #' + formSelector + ' textarea, #' + formSelector + ' select').each(function () {
                let fieldId = $(this).attr('id');
                if ($(this).val() !== formValues[fieldId]) {
                    isFormChanged = true;
                }
            });

            if (isFormChanged) {
                $("#btn-update-" + productId).prop('disabled', false);
            } else {
                $("#btn-update-" + productId).prop('disabled', true);
            }
        });
    }

// Gọi hàm initializeFormUpdate khi modal được mở
    initializeFormUpdate();

    $("#btn-update-" + productId).off().on('click', function (event) {
        event.preventDefault();
        let formSelector = 'form-update' + productId;
        let updateProductRequest = {
            store: storeId,
            userId: userId
        }

        $('#' + formSelector + ' input, #' + formSelector + ' textarea').each(function() {
            let fieldId = $(this).attr('id');
            let fieldValue = $(this).val();
            if (fieldValue == "") {
                fieldValue = 0;
            }
            if ($(this).attr('type') === 'file') {
                delete updateProductRequest[fieldId];
            } else {
                // Đưa dữ liệu vào modifiedData
                updateProductRequest[fieldId] = fieldValue;
            }
        });
        $('#' + formSelector + ' select').each(function () {

            let key = $(this).attr('id');
            let idValue = $(this).val();
            updateProductRequest[key] = {
                id: idValue
            };
        })
        console.log(updateProductRequest)

        let updateProductUrl = 'http://localhost:8080/products/update/' + productId;
        fetch(updateProductUrl, {
            method: 'PATCH',
            headers : {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updateProductRequest)
        })
            .then(function (response) {
                if (response.ok) {
                    toastr.success("Cập nhật sản phẩm thành công");
                    $('.modal-detail').modal('hide');
                    // Xoá thành công, cập nhật giao diện

                    return response.json();
                } else {
                    toastr.error("Cập nhật sản phẩm thất bại " + response.status);
                    throw new Error('Error: ' + response.status);
                }
            })
            .then((res) => {
                const product = res;
                const productRow = $(`tr[data-product-id="${product.id}"]`);
                const renderRow = `
                <td>
                    <div class="input-group">
                        <input class="form-check-input" type="checkbox" value="" aria-label="">
                    </div>
                    </td>
                    <td class="py-1">${product.codeNumber}</td>
                    <td class="image-td">
                        <div class="image-container" ${product.images != null && product.images.length > 0 ? '' : 'style="display: none;"'}>
                            <img class="thumbnail" src="http://localhost:8080/${product.images.length > 0 ? product.images[0].id : 'images/image-default.jpg'}" alt="Thumbnail Image">
                            <div class="overlay">
                                <img class="large-image" src="http://localhost:8080/${product.images.length > 0 ? product.images[0].id : 'images/image-default.jpg'}" alt="Large Image">
                            </div>
                        </div>
                        <div class="image-container" ${product.images == null || product.images.length === 0 ? '' : 'style="display: none;"'}>
                            <img class="thumbnail" src="http://localhost:8080/images/image-default.jpg" alt="Thumbnail Image">
                            <div class="overlay">
                                <img class="large-image" src="http://localhost:8080/images/image-default.jpg" alt="Large Image">
                            </div>
                        </div>
                        <span>${product.name}</span>
                    </td>
                    <td>${product.groupProduct.name}</td>
                    <td><span>${product.salePrice.toLocaleString()}</span></td>
                    <td><span>${product.initialPrice.toLocaleString()}</span></td>
                    <td><span>${product.quantity.toLocaleString()}</span></td>
                    <td>
                        <label class="${product.status === 'Đang kinh doanh' ? 'badge badge-success' : 'badge badge-danger'}">${product.status}</label>
                    </td>
                    <td>
                        <button class="btn btn-info btn-view-detail">Xem chi tiết</button>
                    </td>
                `;

                productRow.html(renderRow);
            })
            .catch(function (error) {
                console.log(error);
            })
        $(`#productModal-${productHandling.id}`).on('hidden.bs.modal', function () {
            let dotsContainer = $(".image-dots");
            dotsContainer.empty();
            $(this).remove();
        });


    });

// xử lý sự kiện button remove được bấm, hiện pop up xác nhận
    $('.remove-image-btn').on('click', function() {
        $('#confirmModal').modal('show');
    });
}










// Xử lý button thêm nhóm hàng
$('.btn-add-group').on('click', function(event) {
    event.preventDefault();
    $('#addGroupProduct').modal('show');
        $('#btnAddGroupProduct').off('click').on('click', async () => {
            let groupName = $('#groupName').val();
            console.log('Tên nhóm hàng:', groupName);
            const requestAddGroup = {
                name: groupName,
                storeId
            }

            await fetch('/group-product/create', {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(requestAddGroup)
            })
                .then(function (response) {
                    if (response.ok) {
                        toastr.success("Thêm nhóm hàng thành công");
                        $('#addGroupProduct').modal('hide');
                        return response.json();

                    } else {
                        return response.json().then(function (data) {
                            const errorMessage = data.message.name;
                            toastr.error(errorMessage);
                            throw new Error('Error: ' + response.status);
                        });
                    }
                })
                .then(function (data) {
                    const optionText = data.name;
                    const optionValue = data.id;
                    $("#insertGroupProduct").append("<option value='" + optionValue + "'>" + optionText + "</option>");
                })
                .catch(function (error) {
                    console.log(error);
                })
        })
});

// Xử lý button thêm nhà cung cấp
$('.btn-add-supplier').on('click', function(event) {
    event.preventDefault();
    $('#addSupplierModal').modal('show');
    $('#add-supplier-btn').off('click').on('click', async () => {
        let supplierName = $('#inputFullName').val();
        let address = $('#inputLocation').val();
        let email = $('#inputEmailAddress').val();
        let phone = $('#inputPhone').val();

        const requestAddSupplier= {
            name: supplierName,
            address: address,
            email: email,
            phone: phone
        }

        await fetch('/supplier/' + storeId +'/create', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestAddSupplier)
        })
            .then(function (response) {
                if (response.ok) {
                    toastr.success("Thêm nhà cung cấp thành công");
                    $('#addSupplierModal').modal('hide');
                    return response.json();

                } else {
                    return response.json().then(function (data) {
                        const errorMessage = data.message.name;
                        toastr.error(errorMessage);
                        throw new Error('Error: ' + response.status);
                    });
                }
            })
            .then(function (data) {
                // Xoá thành công, cập nhật giao diện
                const optionText = data.name;
                const optionValue = data.id;
                $("#insertSupplierSelect").append("<option value='" + optionValue + "'>" + optionText + "</option>");
            })
            .catch(function (error) {
                console.log(error);
            })
    })
});


// định vị các lớp modal
$('#addGroupProduct').on('show.bs.modal', function () {
    $('.modal-backdrop').css('z-index', '1060');
});

$('#addGroupProduct').on('hidden.bs.modal', function () {
    $('.modal-backdrop').css('z-index', '1050');
    $('#groupName').val('');
});

$('#addSupplier').on('show.bs.modal', function () {
    $('.modal-backdrop').css('z-index', '1060');
});

$('#addSupplier').on('hidden.bs.modal', function () {
    $('.modal-backdrop').css('z-index', '1050');
    $('#supplierName').val('');
});

$('#confirmModal').on('show.bs.modal', function () {
    $('.modal-backdrop').css('z-index', '1060');
});

$('#confirmModal').on('hidden.bs.modal', function () {
    $('.modal-backdrop').css('z-index', '1050');
});


// Lọc theo nhóm hàng
$(document).ready(function() {
    let parsedUrl = new URL(window.location.href);

    let groupProductIdValue = parsedUrl.searchParams.get("groupProductId");
    console.log(groupProductIdValue)
    if (groupProductIdValue === null) {
        $('#dropdown-menu-button').text('Tất cả nhóm hàng');
        toastr.success("Hiện thị theo tất cả nhóm hàng")
    } else {
        let groupProductName = $('#list-group').find("a[data-group-product-id='" + groupProductIdValue + "']").text();
        console.log(groupProductName)
        $('#dropdown-menu-button').text(groupProductName);
        toastr.success("Hiển thị theo nhóm hàng: " + groupProductName);
    }

});



// search product
$(document).ready(function() {
    let productList = [];
    let searchResults = $('#searchResults');

    $('#searchInput').on('input', function() {

        $(".search-results-container").css("display", "block");
        let searchText = $(this).val().toLowerCase().trim();
        if (searchText.value === "") {
            $(".search-results-container").css("display", "none");
        }
        if (searchText.length >= 1) {
            fetch('/products/' + storeId + '/suggest?name=' + searchText)
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error('Error: ' + response.status);
                    }
                    return response.json();
                })
                .then(function (data) {
                    productList = data;
                    showSearchResults(productList, searchText);
                })
                .catch(function (error) {
                    console.log('Error:', error);
                });
            // Hiển thị danh sách kết quả tìm kiếm

        } else {
            searchResults.empty();
        }
    });
    function showSearchResults(results, searchText) {
        searchResults.empty();
        if (results.length > 0) {
            results.forEach(function(product) {
                let imgSrc = "http://localhost:8080/images/image-default.jpg";
                if (product.images.length !== 0) {
                    imgSrc = "http://localhost:8080/" + product.images[0].id;
                }
                let listItem = $('<li>').addClass('list-group-item')
                let content = $('<div>').addClass('product-item')
                    .append(
                        $('<div>').addClass('row')
                            .append($('<img>').addClass('thumbnail col-md-3').attr('src', imgSrc))
                            .append(
                                $('<div>').addClass('col-md-9 ')
                                    .append($('<div>').addClass('row ')
                                        .append($('<h4>').addClass('col-md-8 m-0 p-1').html(highlightSearchText(product.name, searchText)))
                                        .append($('<p>').addClass('col-md-4 m-0 p-1').text('Giá bán: ' + product.salePrice.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }))))
                                    .append($('<div>').addClass('row ')
                                        .append($('<p>').addClass('col-md-8 m-0 p-1').html(highlightSearchText(product.codeNumber, searchText)))
                                        .append($('<p>').addClass('col-md-4 m-0 p-1').text('Tồn kho: ' + product.quantity)))
                            )
                    );


                listItem.append(content);

                listItem.on('click', async function() {
                    await renderProductDetailModal(product.id)
                });

                searchResults.append(listItem);
            });
        } else {
            let noResultsItem = $('<li class="fw-semibold fs-6">').text('Không tìm thấy kết quả');
            searchResults.append(noResultsItem);
        }
    }

    function highlightSearchText(text, searchText) {
        const regex = new RegExp(searchText, 'gi');
        return text.replace(regex, '<span class="high-light">$&</span>');
    }

    // Chuyển tới modal chi tiết sản phẩm
});

// xử lý button sửa nhóm hàng
$(document).ready(function() {
    $('.modal-update-group' ).on('shown.bs.modal', function () {
        let modal = $(this);
        let groupProductId = modal.data('group-product-id');
        console.log(groupProductId);

        $('#btnUpdateGroupProduct' + groupProductId).on('click', async () => {
            let groupName = $('#inputUpdateGroupName' + groupProductId).val();
            console.log('Tên nhóm hàng:', groupName);
            const requestUpdateGroup = {
                id: groupProductId,
                name: groupName,
                storeId
            }
            console.log(requestUpdateGroup)

            await fetch('/group-product/update', {
                method: 'PUT',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(requestUpdateGroup)
            })
                .then(function (response) {
                    if (response.ok) {
                        toastr.success("Sửa nhóm hàng thành công");
                        $('.modal-update-group').modal('hide');
                        return response.json();
                    } else {
                        toastr.error("Sửa nhóm hàng thất bại " + response.status);
                        throw new Error('Error: ' + response.status);
                    }
                })
        })
    })
})


// xử lý btn Import
$(document).ready(function() {
    // Xử lý sự kiện khi người dùng chọn file
    $("#importExcel").off().on("change", async function() {
        let file = $(this)[0].files[0];
        if (file) {
            let formData = new FormData();
            formData.append('file', file);

            $('#fileHandle').modal('show');
            $("#fileHandle .modal-body").html(`<p class="me-2">${file.name}</p>`);
            $("#fileHandle .modal-body").append(spinnerElement);

            const url = 'http://localhost:8080/products/' + storeId + '/upload';

            await fetch(url, {
                method: 'POST',
                body: formData
            })
                .then(function (res) {
                    setTimeout(function () {
                        spinnerElement.remove()
                        if (res.ok) {
                            $("#fileHandle .modal-body").append(`<div class="row" style="position: relative"><p class="success pt-2 col-md-6" style="color: green">Nhập hàng thành công</p>
                                                                    <div class="checkmark draw col-md-6 float-start"></div></div>`);
                            $('.checkmark').toggle();
                            setTimeout(function () {
                                location.reload();
                            }, 1000);
                        } else {
                            $("#fileHandle .modal-body").append(`<p class="error pt-2" style="color: red">Xảy ra lỗi khi xử lý file, vui lòng kiểm tra lại</p>`);
                            toastr.error("Nhập hàng thất bại")
                        }
                    }, 1000)
                })
        }
    });
});


// xử lý xuất file
$(document).ready(function() {
    $('#btn-export').on('click', async function() {
        $('#fileHandle').modal('show');
        $("#fileHandle .modal-body").append(`<span class="me-2">Đang xử lý xuất file</span>`);
        $("#fileHandle .modal-body").append(spinnerElement);
        const url = 'http://localhost:8080/products/' + storeId + '/download';
        await fetch(url, {
            method: 'GET'
        })
            .then(response => response.blob())
            .then(blob => {
                const downloadUrl = URL.createObjectURL(blob);
                $("#fileHandle .modal-body").html(`<div class="row" style="position: relative"><p class="success pt-2 col-md-6" style="color: green">Xuất file thành công</p>
                                                                    <div class="checkmark draw col-md-6 float-start"></div></div>`)
                spinnerElement.remove()
                $('.checkmark').toggle();
                // Tạo một phần tử a tạm thời để tải xuống
                const link = document.createElement('a');
                link.href = downloadUrl;
                link.download = 'products.xlsx';
                link.textContent = 'products.xlsx'
                $("#fileHandle .modal-body").append(link);
                link.click();

                // Xóa URL tạm thời
                URL.revokeObjectURL(downloadUrl);
            })
            .catch(error => console.error(error));
    })
})

// xử lý btn in tem mã
$(document).ready(function() {
  $('#printStamp').on('click', function () {
      let table = $(".modal-print");
      let tbody = table.find("tbody");
      tbody.empty();
      let checkedItem = $("tbody input[type='checkbox']:checked");
      if (checkedItem.length !== 0) {
          checkedItem.each(function() {
              let codeNumber = $(this).closest("tr").find("td:nth-child(2)").text();
              let productName = $(this).closest("tr").find("td:nth-child(3)").text().trim().replace(/\./g, "");
              console.log(productName)
              let salePrice = $(this).closest("tr").find("td:nth-child(5)").text().trim().replace(/\./g, "");
              let quantity = $(this).closest("tr").find("td:nth-child(7)").text();
              let row = $("<tr>");
              row.append($("<td>").text(codeNumber));
              row.append($("<td>").text(productName));
              row.append($("<td>").text(parseInt(salePrice).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })));
              let input = $('<input>').attr('type', 'text').addClass('input-quantity').val(quantity.trim());
              row.append($('<td>').append(input));
              row.append($("<td>").append('<button class="btn-print"><i class="ti ti-trash"></i></button>'))
              tbody.append(row);
          });
          $('#print-stamp').modal('show');

          $('.btn-print').on('click', function () {
              $(this).closest("tr").remove();
          })

          $("#print").off().on("click",function() {
              $("#previewModal .modal-body").empty()
              let selectedRows = $(".modal-print tbody tr");
              selectedRows.each(function() {
                  let codeNumber = $(this).find("td:nth-child(1)").text();
                  let productName = $(this).find("td:nth-child(2)").text().trim();
                  let salePrice = $(this).find("td:nth-child(3)").text().trim();
                  salePrice = salePrice.replace(/,/g, ".");
                  // Tạo phần tử <svg> để chứa mã barcode và tên sản phẩm
                  let svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
                  svg.setAttribute("class", "barcode");
                  svg.setAttribute("width", "222px");
                  svg.setAttribute("height", "150px");
                  svg.setAttribute("x", "0px");
                  svg.setAttribute("y", "0px");
                  svg.setAttribute("viewBox", "0 0 222 150");
                  svg.setAttribute("xmlns", "http://www.w3.org/2000/svg");
                  svg.setAttribute("version", "1.1");
                  svg.setAttribute("style", "transform: translate(0,0)");

                  // Tạo phần tử <g> để chứa cả mã barcode và tên sản phẩm
                  let groupElement = document.createElementNS("http://www.w3.org/2000/svg", "g");

                  // Tạo phần tử <text> để hiển thị tên sản phẩm
                  let nameEl = document.createElementNS("http://www.w3.org/2000/svg", "text");
                  nameEl.setAttribute("x", "50%");
                  nameEl.setAttribute("y", "5");
                  nameEl.setAttribute("text-anchor", "middle");
                  nameEl.setAttribute("style", "font-size: 16px; fill: #000000;");
                  nameEl.textContent = productName;

                  let priceEl = document.createElementNS("http://www.w3.org/2000/svg", "text");
                  priceEl.setAttribute("x", "50%");
                  priceEl.setAttribute("y", "130");
                  priceEl.setAttribute("text-anchor", "middle");
                  priceEl.setAttribute("style", "font-size: 18px; fill: #000000; font-weight:500");
                  priceEl.textContent = "Giá: " + salePrice + "VNĐ";

                  // Thêm phần tử <text> vào phần tử <g>
                  groupElement.appendChild(nameEl);
                  groupElement.appendChild(priceEl);

// Tạo barcode bằng JsBarcode
                  JsBarcode(svg, codeNumber, {
                      format: "CODE128",
                      textMargin: 0,
                      fontSize: 16,
                      font: "Roboto",
                      height: 80,
                      width: 3
                  });

// Thêm phần tử <g> vào phần tử <svg>
                  svg.appendChild(groupElement);

                  let divContainer = $('<div>').addClass('barcode-container').append(svg);

// Thêm phần tử <svg> vào nơi bạn muốn hiển thị (ví dụ: trong modal)
                  $("#previewModal .modal-body").append(divContainer);

                  // $("#previewModal .modal-body").append(divContainer);
              });
              // Mở modal
              $("#previewModal").modal("show");
          });
      } else {
          toastr.warning("Bạn phải chọn sản phẩm cần in trước.")
      }
  })

})

$(document).ready(function () {
    let parsedUrl = new URL(window.location.href);

    let groupProductIdValue = parsedUrl.searchParams.get("groupProductId");
    console.log(groupProductIdValue)

    let currentPage = parseInt($('#pagination li.active a').text());
    console.log(currentPage)
    $('.page').on('click', function (event) {
        event.preventDefault();
        let page = $(this).text()
        if (groupProductIdValue  === null) {
            window.location.href = '/products/' + storeId + '/inventory?page=' + page;
        } else {
            window.location.href = '/products/' + storeId + '/inventory?page=' + page + '&groupProductId=' + groupProductIdValue;
        }


    })
    $('#previous-page').on('click', function (event) {
        event.preventDefault();
        if (groupProductIdValue !== null) {
            window.location.href = '/products/' + storeId + '/inventory?page=' + (currentPage - 1) + '&groupProductId=' + groupProductIdValue;
        } else if (groupProductIdValue === null) {
            window.location.href = '/products/' + storeId + '/inventory?page=' + (currentPage - 1);
        }
    })

    $('#next-page').on('click', function (event) {
        event.preventDefault();
        if (groupProductIdValue === null) {
            window.location.href = '/products/' + storeId + '/inventory?page=' + (currentPage + 1);
        } else {
            window.location.href = '/products/' + storeId + '/inventory?page=' + (currentPage + 1) + '&groupProductId=' + groupProductIdValue;
        }

    })
})


