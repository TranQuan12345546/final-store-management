

$(document).ready(function () {
    $(".switch input").change(function () {
        const isChecked = $(this).prop("checked");
        const campaignId = $(this).closest('tr').data('campaign-id');


        fetch("/campaign/" + storeId + "/change-status" + "?campaignId=" + campaignId + "&isActive=" + isChecked, {
            method: "PUT",
        })
            .then(response => {
                if (response.ok) {
                    if (isChecked) {
                        toastr.success("Kích hoạt chiến dịch thành công.")
                    } else {
                        toastr.success("Chiến dịch bị vô hiệu hoá.")
                    }
                } else {
                    toastr.error("Thay đổi trạng thái thất bại.")
                }

            })
            .catch(error => {
                console.error("Error:", error);
            });
    });
});

$(document).on('click', '.btn-view-detail',  function() {
    let voucherId = $(this).closest('tr').data('campaign-id');
    renderCampaignDetailModal(voucherId);


});

function renderCampaignDetailModal(campaignId) {
    let campaignDetail = {}
    campaignList.forEach(campaign => {
        if (campaign.id == campaignId) {
            campaignDetail = campaign;
        }
    })

    console.log(campaignDetail)
    let modalCampaignDetail = `
         <div class="modal fade" id="campaignDetailModal-${campaignDetail.id}" tabindex="-1" aria-labelledby="campaignDetailModalLabel"
       aria-hidden="true" data-bs-backdrop="static">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="campaignDetailModalLabel">Thông tin chiến dịch</h5>
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
                      <div class="mb-3">
                        <label class="small mb-1" for="inputTitle">Tên chiến dịch</label>
                        <input class="form-control" id="inputTitle" type="text" placeholder="Nhập tên voucher" value="${campaignDetail.title}">
                      </div>
                    </div>
                    <div class="row"> 
                        <div class="col-md-6 mb-3">
                            <label class="small mb-1" for="inputEmailAddress">Sản phẩm áp dụng</label>
                            <input class="form-control" id="inputLocation" type="text" value="${campaignDetail.products.length} sản phẩm" disabled>
                        </div>
                        <div class="col-md-6 " style="margin-top: 7px;">
                            <button id="product-detail-${campaignDetail.id}" class="btn btn-secondary ">Chi tiết sản phẩm áp dụng</button>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                        <label class="small mb-1" for="discountType">Loại giảm giá</label>
                        <select id="discountType" class="form-control">
                            <option value="Theo phần trăm" ${campaignDetail.reduceType === 'Theo phần trăm' ? 'selected' : '' }>Theo phần trăm</option>
                            <option value="Theo giá thành" ${campaignDetail.reduceType === 'Theo giá thành' ? 'selected' : '' }>Theo giá thành</option>
                        </select>
                      </div>
                      <div class="col-md-6 mb-3">
                        <label class="small mb-1" for="inputReducedPrice">Mức giảm</label>
                        <input class="form-control" id="inputReducedPrice" type="" placeholder="Nhập mức giảm" value="${campaignDetail.reducedPrice}">
                        <div class="percent" ${campaignDetail.reduceType === 'Theo giá thành' ? 'style="display:none"' : ''}><span class=""></span>%</div> 
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-md-6 mb-3">
                        <label class="small mb-1" for="inputStatus" style="display: block;">Trạng thái</label>
                        <label class="switch">
                            <input type="checkbox" ${campaignDetail.isActive === true ? 'checked' : '' }>
                            <span class="slider round" >
                              <span class="switch-label on">ON</span>
                              <span class="switch-label off">OFF</span>
                            </span>
                        </label>
                      </div>
                    </div>
                     
                    <div class="mb-3">
                        <label class="small mb-1" for="inputTime">Thời gian tiến hành chiến dịch</label>
                         <input class="form-control" type="text" id="inputTime" name="daterange" value="" />
                    </div>
                    <button id="update-campaign-btn" class="btn btn-primary float-end">Cập nhật</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    `
    $('.content-wrapper').append(modalCampaignDetail);
    $(`#campaignDetailModal-${campaignDetail.id}`).modal('show');

    $('#discountType').on('change', function () {
        if (this.value === "Theo phần trăm") {
            $('.percent').show();
        } else {
            $('.percent').hide();
        }
    })

    $(`#campaignDetailModal-${campaignDetail.id} input[name="daterange"]`).daterangepicker({
        opens: 'left',
        startDate: new Date(campaignDetail.startDate),
        endDate: new Date(campaignDetail.endDate)
    }, function(start, end, label) {
        console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
    });



    $(`#product-detail-${campaignDetail.id}`).on('click', function () {
        let productDetailModal = `
        <div class="modal fade" id="productDetail-${campaignDetail.id}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="z-index: 2000">
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
        $(`#productDetail-${campaignDetail.id}`).on('show.bs.modal', function () {
            $('.modal-backdrop').css('z-index', '1060');
        });

        $(`#productDetail-${campaignDetail.id}`).modal('show');
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
        renderPagination(campaignDetail.products);


        $(`#productDetail-${campaignDetail.id}`).on('hidden.bs.modal', function () {
            $('.modal-backdrop').css('z-index', '1050');
            $(`#productDetail-${campaignDetail.id}`).remove();
        });

    })


    $(`#campaignDetailModal-${campaignDetail.id}`).on('hidden.bs.modal', function () {
        $(`#campaignDetailModal-${campaignDetail.id}`).remove();
    })

    $('#update-campaign-btn').on('click', function () {
        const title = $('#inputTitle').val();
        const reducedPrice = parseInt($('#inputReducedPrice').val());
        const isActive = $('#inputStatus').prop("checked");
        const startDate = $('#inputTime').data('daterangepicker').startDate.format('YYYY-MM-DDTHH:mm:ss');
        const endDate = $('#inputTime').data('daterangepicker').endDate.format('YYYY-MM-DDTHH:mm:ss');
        let reduceType = $('#discountType').val();

        if (reduceType === "Theo phần trăm") {
            reduceType = 1;
        } else {
            reduceType = 2;
        }

        const requestData = {
            campaignId: campaignDetail.id,
            title: title,
            reducedPrice: reducedPrice,
            reduceType: reduceType,
            isActive: isActive,
            startDate: startDate,
            endDate: endDate
        };
        console.log(requestData)

        fetch('/campaign/' + storeId + '/update', {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        })
            .then(response => {
                if (response.ok) {
                    toastr.success("Cập nhật chiến dịch thành công");
                    setTimeout(function () {
                        window.location.reload();
                    }, 1000)
                } else {
                    toastr.error("Cập nhật chiến dịch thất bại");
                }
            })
            .catch(error => {
                console.log("Error:", error);
            });
    })
}