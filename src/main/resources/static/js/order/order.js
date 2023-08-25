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

// Lọc theo nhóm hàng
// Thêm sự kiện click cho các phần tử trong dropdown-menu
$('.dropdown-menu li').on('click', function() {
    const dropdownButton = document.getElementById('dropdown-menu-button');
    let selectedGroupProduct = $(this).find('span').text();
    dropdownButton.textContent = selectedGroupProduct + " ";
    filterTableByGroupProduct(selectedGroupProduct);
});

function filterTableByGroupProduct(groupProduct) {
    if (groupProduct !== "Chọn tất cả") {
        toastr.success("Hiển thị theo: " + groupProduct)
        $('table tbody tr').hide();
        $('table tbody tr').each(function() {
            let currentGroupProduct = $(this).find('td:nth-child(10)').text();
            if (currentGroupProduct === groupProduct) {
                $(this).show();
            }
        });
    } else {
        toastr.success("Hiển thị theo: Tất cả nhóm hàng")
        $('table tbody tr').show();
    }
}


// tìm kiếm
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
            fetch('/order/' + storeId + '/suggest?name=' + searchText)
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error('Error: ' + response.status);
                    }
                    return response.json();
                })
                .then(function (data) {
                    productList = data;
                    console.log(productList)
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
                                        .append($('<p>').addClass('col-md-4 m-0 p-1').text('Giá bán: ' + product.salePrice)))
                                    .append($('<div>').addClass('row ')
                                        .append($('<p>').addClass('col-md-8 m-0 p-1').html(highlightSearchText(product.codeNumber, searchText)))
                                        .append($('<p>').addClass('col-md-4 m-0 p-1').text('Tồn kho: ' + product.quantity)))
                            )
                    );


                listItem.append(content);

                listItem.on('click', function() {
                    $(".search-results-container").css("display", "none");
                    showOrderProduct(product);
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

    function showOrderProduct(product) {
        $('table tbody tr').hide();
        fetch('/order/product/' + product.id)
            .then(function (response) {
                if (!response.ok) {
                    throw new Error('Error: ' + response.status);
                }
                return response.json();
            })
            .then(function (data) {
                let orderProductList = data;
                const tableBody = document.querySelector('tbody');
                let rowHtml = '';
                if (!orderProductList || orderProductList.length === 0) {
                    const noOrderMessage = `<tr><td colspan="10" class="text-center">Không có đơn hàng nào với sản phẩm này</td></tr>`;
                    tableBody.innerHTML = noOrderMessage;
                } else {
                    orderProductList.forEach(orderProduct => {
                        rowHtml += `
                    <tr>
                <td>
                  <div class="input-group">
                    <input class="form-check-input" type="checkbox" value="" aria-label="">
                  </div>
                </td>
                <td>${orderProduct.orderNumber}</td>
                <td>${formatDate(orderProduct.orderDate)}</td>
                <td>${orderProduct.productName}</td>
                <td>
                  <span>${orderProduct.quantity}</span>
                </td>
                <td>
                  <span>${orderProduct.discount}</span>
                </td>
                <td>
                  <span>${orderProduct.totalPrice}</span>
                </td>
                <td>${orderProduct.clientName}</td>
                <td>${orderProduct.ownerName !== null ? orderProduct.ownerName : orderProduct.staffName}</td>
                <td class="group-product-column" style="display: none;">${orderProduct.groupProductName}</td>
              </tr>
                    `;
                    });
                    tableBody.innerHTML = rowHtml;
                }
            })
            .catch(function (error) {
                console.log('Error:', error);
            });
    }
});


// xử lý btn in hoá đơn
$(document).ready(function() {
    $('#printOrder').off().on('click', function () {
        let orderIds = [];
        $("tbody input[type='checkbox']:checked").each(function() {
            let orderId = $(this).closest('tr').data('order-id');
            orderIds.push(orderId);
        })
        if (orderIds.length === 0) {
            toastr.warning("Vui lòng chọn hoá đơn cần in")
        } else {
            let queryString = orderIds.map(id => `orderIds=${id}`).join('&');

            fetch(`/order/`+ storeId +`/convert?${queryString}`, {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json"
                }
            }).then(response => {
                if (response.ok) {
                    return  response.blob()

                } else {
                    toastr.error("Tải hoá đơn thất bại")
                    console.log('Error');
                }
            }).then(blob => {
                    const downloadUrl = URL.createObjectURL(blob);
                    const iframe = document.createElement('iframe');
                    iframe.style.display = 'none';
                    iframe.src = downloadUrl;
                    document.body.appendChild(iframe);
                    iframe.contentWindow.print();
                })
                .catch((e) => {
                    console.log(e);
                })

        }
    })
})

// xử lý btn trả hàng
$(document).ready(function() {
    $('#returnOrder').off().on('click', function () {
        let orderIds = [];
        console.log(orderIds)
        $("tbody input[type='checkbox']:checked").each(function() {
            let orderId = $(this).closest('tr').data('order-id');
            orderIds.push(orderId);
        })
        if (orderIds.length === 0) {
            toastr.warning("Vui lòng chọn hoá đơn cần trả")
        } else {

            let returnRequest = {
                orderIds: orderIds,
                userId: userId
            }

            $('#confirmModal').modal('show');
            $('#confirmDelete').off().on('click', function () {
                fetch('/order/return/' + storeId, {
                    method: 'PUT',
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(returnRequest)
                })
                    .then(response => {
                        if (response.ok) {
                            toastr.success("Trả hàng thành công, vui lòng xem thông tin trả hàng trong mục trả hàng")
                            $(this).closest('tr').remove();
                            $('#confirmModal').modal('hide');

                        } else {
                            toastr.error("Không thể trả hàng sau 10 ngày từ ngày tạo đơn hàng")
                            $('#confirmModal').modal('hide');
                        }
                    })
                    .catch((e) => {
                        console.log(e);
                    })
            })

        }

    })
})
