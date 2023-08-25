


$(document).ready(function () {
    $(".switch input").change(function () {
        const isChecked = $(this).prop("checked");
        const voucherId = $(this).closest('tr').data('voucher-id');
        console.log(voucherId);
        console.log(isChecked)
        if (isChecked) {
            toastr.success("Kích hoạt voucher thành công")
        } else {
            toastr.success("Voucher bị vô hiệu hoá")
        }

        fetch("/voucher/" + storeId + "/change-status" + "?voucherId=" + voucherId + "&status=" + isChecked, {
            method: "PUT",
        })
            .catch(error => {
                console.error("Error:", error);
            });
    });
});

$(document).on('click', '.btn-view-detail', function() {
    let voucherId = $(this).closest('tr').data('voucher-id');
    renderVoucherDetailModal(voucherId);


});

function renderVoucherDetailModal(voucherId) {
    let voucherDetail = {}
    voucherList.forEach(voucher => {
        if (voucher.id == voucherId) {
            voucherDetail = voucher;
        }
    })

    console.log(voucherDetail)
    let modalVoucherDetail = `
         <div class="modal fade" id="voucherDetailModal-${voucherDetail.id}" tabindex="-1" aria-labelledby="voucherDetailModalLabel"
       aria-hidden="true" data-bs-backdrop="static">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="voucherDetailModalLabel">Thông tin voucher</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="container-xl px-4 mt-4 d-flex justify-content-center">
              <div class="">
                <!-- Account details card-->
                <div class="card mb-4" style="width: 500px">
                  <div class="card-header">Thông tin chi tiết</div>
                  <div class="card-body">
                    <div class="row">
                      <div class="col-md-6 mb-3">
                        <label class="small mb-1" for="inputTitle">Tên voucher</label>
                        <input class="form-control" id="inputTitle" type="text" placeholder="Nhập tên voucher" value="${voucherDetail.title}">
                      </div>
                      <div class="col-md-6 mb-3">
                        <label class="small mb-1" for="inputCode">Mã voucher</label>
                        <input class="form-control" id="inputCode" type="text" placeholder="Nhập mã voucher" value="${voucherDetail.code}">
                      </div>
                    </div>
                    <div class="row"> 
                        <div class="col-md-6 mb-3">
                            <label class="small mb-1" for="inputEmailAddress">Sản phẩm áp dụng</label>
                            <input class="form-control" id="inputLocation" type="text" value="${voucherDetail.products.length} sản phẩm" disabled>
                        </div>
                        <div class="col-md-6 " style="margin-top: 7px;">
                            <button id="product-detail-${voucherDetail.id}" class="btn btn-secondary ">Chi tiết sản phẩm áp dụng</button>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                        <label class="small mb-1" for="discountType">Loại giảm giá</label>
                        <select id="discountType" class="form-control">
                            <option value="Theo phần trăm" ${voucherDetail.reduceType === 'Theo phần trăm' ? 'selected' : '' }>Theo phần trăm</option>
                            <option value="Theo giá thành" ${voucherDetail.reduceType === 'Theo giá thành' ? 'selected' : '' }>Theo giá thành</option>
                        </select>
                      </div>
                      <div class="col-md-6 mb-3">
                        <label class="small mb-1" for="inputReducedPrice">Mức giảm</label>
                        <input class="form-control" id="inputReducedPrice" type="" placeholder="Nhập mức giảm" value="${voucherDetail.reducedPrice}">
                        <div class="percent" ${voucherDetail.reduceType === 'Theo giá thành' ? 'style="display:none"' : ''}><span class=""></span>%</div> 
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-md-6 mb-3">
                        <label class="small mb-1" for="inputQuantityPerClient">Số lượt sử dụng/khách hàng</label>
                        <input class="form-control" id="inputQuantityPerClient" type="number" placeholder="Nhập số lượt sử dụng/khách hàng" value="${voucherDetail.quantityPerClient}">
                      </div>
                      <div class="col-md-6 mb-3">
                        <label class="small mb-1" for="inputQuantity">Số lượt sử dụng tối đa</label>
                        <input class="form-control" id="inputQuantity" type="number" placeholder="Nhập số lượt sử dụng tối đa" value="${voucherDetail.originalQuantity}">
                      </div>
                    </div>
                    <div class="row">
                       <div class="col-md-6 mb-3">
                        <label class="small mb-1">Số lượt voucher đã sử dụng</label>
                        <input class="form-control" value="${voucherDetail.usedQuantity}" disabled>
                      </div>
                      <div class="col-md-6 mb-3">
                        <label class="small mb-1" for="inputStatus" style="display: block;">Trạng thái</label>
                        <label class="switch">
                            <input type="checkbox" ${voucherDetail.status === true ? 'checked' : '' }>
                            <span class="slider round" >
                              <span class="switch-label on">ON</span>
                              <span class="switch-label off">OFF</span>
                            </span>
                        </label>
                      </div>
                    </div>
                     
                    <div class="mb-3">
                        <label class="small mb-1" for="inputTime">Thời gian sử dụng</label>
                         <input class="form-control" type="text" id="inputTime" name="daterange" value="" />
                    </div>
                    <button id="update-voucher-btn" class="btn btn-primary float-end">Cập nhật</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    `
    $('.content-wrapper').append(modalVoucherDetail);
    $(`#voucherDetailModal-${voucherDetail.id}`).modal('show');

    $('#discountType').on('change', function () {
        if (this.value === "Theo phần trăm") {
            $('.percent').show();
        } else {
            $('.percent').hide();
        }
    })

    $(`#voucherDetailModal-${voucherDetail.id} input[name="daterange"]`).daterangepicker({
        opens: 'left',
        startDate: new Date(voucherDetail.startDate),
        endDate: new Date(voucherDetail.endDate)
    }, function(start, end, label) {
        console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
    });



    $(`#product-detail-${voucherDetail.id}`).on('click', function () {
        let productDetailModal = `
        <div class="modal fade" id="productDetail-${voucherDetail.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="z-index: 2000">
    <div class="modal-dialog modal-lg" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="myModalLabel">Các sản phẩm được áp dụng</h5>
          <button type="button" class="close" data-bs-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
        <div class="card">
            <div class="card-body">
                <div class="table-container">
                     <div id="table-product" class="table-responsive" style="min-height: 505px">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>
                                         <div class="input-group">
                                            <input class="form-check-input" type="checkbox" value="" aria-label="">
                                         </div>
                                    </th>
                                    <th>
                                        Mã sản phẩm
                                    </th>
                                     <th>
                                        Tên sản phẩm
                                    </th>
                                    <th>
                                        Giá bán
                                    </th>
                                    <th>
                                        Nhóm hàng
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                               
                            </tbody>
                        </table>
                        </div>
                        <div class="d-flex justify-content-center mt-3" id="pagination-product">

                        </div>
                </div>
            </div>
        </div>
           
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
    `;

        $('.content-wrapper').append(productDetailModal);
        $(`#productDetail-${voucherDetail.id}`).on('show.bs.modal', function () {
            $('.modal-backdrop').css('z-index', '1060');
        });

        $(`#productDetail-${voucherDetail.id}`).modal('show');
        function renderProduct(productList) {
            let tableBody = $("#table-product tbody");
            tableBody.innerHTML = "";
            let productHtml = "";
            productHtml += productList.map(product => `
        <tr>
            <td> 
                <div class="input-group">
                    <input class="form-check-input" type="checkbox" value="" aria-label="">
                </div>
            </td>
            <td>${product.codeNumber}</td>
            <td>${product.name}</td>
            <td>${product.salePrice.toLocaleString()}</td>
            <td>${product.groupProduct.name}</td>
        </tr>
    `)

            tableBody.html(productHtml);
        }
        function renderPagination(productList) {
            $('#pagination-product').pagination({
                dataSource: productList,
                pageSize: 10,
                showSizeChanger: true,
                callback: function (data, pagination) {
                    renderProduct(data);
                }
            })
        }
        renderPagination(voucherDetail.products);


        $(`#productDetail-${voucherDetail.id}`).on('hidden.bs.modal', function () {
            $('.modal-backdrop').css('z-index', '1050');
            $(`#productDetail-${voucherDetail.id}`).remove();
        });

    })


    $(`#voucherDetailModal-${voucherDetail.id}`).on('hidden.bs.modal', function () {
        $(`#voucherDetailModal-${voucherDetail.id}`).remove();
    })

    $('#update-voucher-btn').on('click', function () {
        const title = $('#inputTitle').val();
        const code = $('#inputCode').val();
        const originalQuantity = parseInt($('#inputQuantity').val());
        const reducedPrice = parseInt($('#inputReducedPrice').val());
        const quantityPerClient = parseInt($('#inputQuantityPerClient').val());
        const status = $('#inputStatus').prop("checked");
        const startDate = $('#inputTime').data('daterangepicker').startDate.format('YYYY-MM-DDTHH:mm:ss');
        const endDate = $('#inputTime').data('daterangepicker').endDate.format('YYYY-MM-DDTHH:mm:ss');
        let reduceType = $('#discountType').val();

        if (reduceType === "Theo phần trăm") {
            reduceType = 1;
        } else {
            reduceType = 2;
        }

        const requestData = {
            voucherId: voucherDetail.id,
            title: title,
            code: code,
            originalQuantity: originalQuantity,
            reducedPrice: reducedPrice,
            quantityPerClient: quantityPerClient,
            reduceType: reduceType,
            status: status,
            startDate: startDate,
            endDate: endDate
        };
        console.log(requestData)

        fetch('/voucher/' + storeId + '/update', {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        })
            .then(response => {
                if (response.ok) {
                    toastr.success("Cập nhật voucher thành công");
                    setTimeout(function () {
                        window.location.reload();
                    }, 1000)
                } else {
                    toastr.error("Cập nhật voucher thất bại");
                }
            })
            .catch(error => {
                console.log("Error:", error);
            });
    })
}

