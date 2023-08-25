

const productContainer = document.querySelector(".product-container")
console.log(productList)
function renderBlogs(productList) {
// Xóa hết nd đang có trước khi render
productContainer.innerHTML = "";

// Tạo nd
let productHtml = "";
productList.forEach(product => {
productHtml += `
      <button id="product ${product.id}" type="button" class="product-content" data-product-id = "${product.id}" >
    <img class="img" src="${product.images.length != 0 ? 'http://localhost:8080/' + product.images[0].id : 'http://localhost:8080/images/image-default.jpg'}" alt="">
    <div class="content">
      <span style="color: #007bff">${product.salePrice.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}</span>
      <div class="quantity">SL: ${product.quantity.toLocaleString('vi-VN')}</div>
      <p>${product.name}</p>
    </div>
  </button>`
})

// Insert nd
productContainer.innerHTML = productHtml;
}

// Hiển thị phần phân trang
function renderPagination(productList) {
$('#pagination').pagination({
    dataSource: productList,
    callback: function (data, pagination) {
        renderBlogs(data);
    }
})
}

renderPagination(productList);

// Phân trang theo nhóm hàng
$('.dropdown-menu li').on('click', function() {
    const dropdownButton = document.getElementById('dropdown-menu-button');
    let selectedGroupProduct = $(this).find('span').text();
    dropdownButton.textContent = selectedGroupProduct + " ";

    if (selectedGroupProduct == "Chọn tất cả") {
        toastr.success("Hiển thị theo: Tất cả nhóm hàng")
        renderPagination(productList);
    } else {
        toastr.success("Hiển thị theo: " + selectedGroupProduct)
        let groupProductId = $(this).find('span').data('group-product-id');
        fetch('/products/productDetail/' + storeId + '/groupProduct?groupId=' + groupProductId, {
            method: 'GET'
        }).then(function (response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Error: ' + response.status);
            }
        }).then(function (productList) {
            renderPagination(productList);
        })
    }
});



$(document).on('click', '.product-content', async function() {

    let productId = $(this).data('product-id');
    await getProductHistoryModal(productId);
});


let productHistory = [];
async function getProductHistoryModal(productId) {
    await fetch('/product-history?productId=' + productId,  {
        method: "GET"
    })
        .then(function(response) {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Error: ' + response.status);
            }
        })
        .then(function(data) {
            productHistory = data;
        })
        .catch(function(error) {
            console.error(error);
        });

    let productHistoryHtml = `
        <div class="modal fade" id="productHistory ${productId}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog modal-lg" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="myModalLabel">${productHistory[0].productName}</h5>
          <button type="button" class="close" data-bs-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
            <div id="table-product-history" class="table-responsive" style="min-height: 505px">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>
                                Giao dịch
                            </th>
                             <th>
                                Thực hiện
                            </th>
                            <th>
                                Giá vốn
                            </th>
                            <th>
                                Số lượng
                            </th>
                            <th>
                                Tồn cuối
                            </th>
                            <th>
                               Thời gian
                            </th>   
                        </tr>
                    </thead>
                    <tbody>
                       
                    </tbody>
                </table>
            </div>
            <div class="d-flex justify-content-center mt-3" id="pagination-${productId}">

            </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
        `;
    $('.product-container').append(productHistoryHtml);

    function renderProduct(productHistory) {
        let tableBody = $("#table-product-history tbody");
// Xóa hết nd đang có trước khi render
        tableBody.innerHTML = "";

// Tạo nd
        let productHtml = "";
        productHtml += productHistory.map(history => `
                           <tr style="background-color: ${history.action === 'Tạo mới' ? 'rgb(83,248,83, 0.3)' : history.action === 'Bán hàng' ? 'rgb(253,87,87,0.3)': history.action === 'Trả hàng' ? 'rgb(87,253,250,0.3)' : history.action === 'Kiểm hàng' ? 'rgb(0, 82, 250, 0.3)' : ' rgb(250,172,0,0.3)'}">
    <td>${history.action}</td>
    <td>${history.createdBy}</td>
    <td>${history.price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' })}</td>
    <td>${history.changeQuantity.toLocaleString('vi-VN')}</td>
    <td>${history.finalQuantity.toLocaleString('vi-VN')}</td>
    <td>${formatDate(history.updatedAt)}</td>
</tr>
    `)

        tableBody.html(productHtml);
    }
    function renderPagination(productList) {
        $(`#pagination-${productId}`).pagination({
            dataSource: productList,
            pageSize: 15,
            showSizeChanger: true,
            callback: function (data, pagination) {
                renderProduct(data);
            }
        })
    }
    renderPagination(productHistory);
    $(`#productHistory\\ ${productId}`).modal('show');
    $(`#productHistory\\ ${productId}`).on('hidden.bs.modal', function () {
        $(`#productHistory\\ ${productId}`).remove();
    });
}




// search product
$(document).ready(function() {
    let productList = [];
    let searchResults = $('#searchResults');

    $('#searchInput').on('input', function() {

        $(".search-results-container").css("display", "block");
        let searchText = $(this).val().toLowerCase().trim();
        if (searchText === "") {
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
                let highlightedProductName = highlightSearchText(product.name, searchText);
                let highlightedCodeNumber = highlightSearchText(product.codeNumber, searchText);
                let imgSrc = "http://localhost:8080/images/image-default.jpg";
                if (product.images.length !== 0) {
                    imgSrc = "http://localhost:8080/" + product.images[0].id;
                }
                let listItem = $('<li>').addClass('product-item');
                listItem.attr('data-product-id', product.id);
                let img = $('<img>').addClass('thumbnail').attr('src', imgSrc);
                let infoDiv = $('<div>');
                let name = $('<h4>').html(highlightedProductName);
                let price = $('<p>').text('Giá bán: ' + product.salePrice);
                let detailDiv = $('<div>');
                let codeNumber = $('<p>').html(highlightedCodeNumber);
                let quantity = $('<p>').text('Tồn kho: ' + product.quantity);

                infoDiv.append(name, price);
                detailDiv.append(codeNumber, quantity);
                listItem.append(img, infoDiv, detailDiv);

                listItem.on('click',  async function () {
                    let clickedProductId = $(this).data('product-id');
                    $(".search-results-container").css("display", "none");
                    await getProductHistoryModal(clickedProductId);
                });

                searchResults.append(listItem);
            });
        } else {
            let noResultsItem = $('<li class="no-result">').text('Không tìm thấy kết quả');
            searchResults.append(noResultsItem);
        }
    }
    function highlightSearchText(text, searchText) {
        const regex = new RegExp(searchText, 'gi');
        return text.replace(regex, '<span class="high-light">$&</span>');
    }
});

//close ProductHistory modal
