const spinnerElement = $('<div>').addClass('spinner-border').attr('role', 'status');
spinnerElement.append('<span class="visually-hidden">Loading...</span>');
let productList = [];
let groupProductList = [];
let inventoryName;

$(document).on('blur', '#inventory-name', function () {
    inventoryName = $(this).val();
    console.log(inventoryName)
})
function showAllItems() {
    document.querySelector('.options').remove();
    document.getElementById('all-product').style.display = 'flex';
}

function showByCategory() {
    document.querySelector('.options').remove();
    document.getElementById('all-category').style.display = 'flex';
    const groupContainer = document.getElementById('category-container');
    fetch('/group-product/all-group/' + storeId, {
        method: 'GET'
    }).then(response => {
        if (response.ok) {
            return response.json();
        }
    }).then(response => {
        groupProductList = response;
        let groupProductHtml = "";
        groupProductList.forEach(groupProduct => {
            groupProductHtml += `
            <button class="choose-category btn btn-behance" data-group-product-id="${groupProduct.id}" >${groupProduct.name}</button>
            `
        })
        groupContainer.innerHTML = groupProductHtml;
    })
}

//chọn tất cả
function startInventory() {
    document.querySelector('.spin-wrapper').style.display = 'block';


    fetch("/products/store/" + storeId, {
        method: 'GET'
    }).then(response => {
        if (response.ok) {
            return response.json();
        }
    }).then(response => {
        productList = response;
        console.log(productList)
        setTimeout(function () {
            document.querySelector('.inventory-option').remove();
            document.querySelector('.spin-wrapper').remove();
            document.getElementById('inventory-table').style.display = 'block';
        }, 0)
        renderProductList(response);
    })
}

//chọn theo ngành hàng
$(document).on('click', '.choose-category', function () {
    const groupProductId = $(this).data('group-product-id');
    $('#checked-category').css('display', 'none');
    const groupProductName = $(this).text();
    console.log(groupProductName)
    const startInventory = `
        <div id="category" style="text-align: center">
            <span class="" style="display: block">Đã chọn: ${groupProductName}</span>
            <button id="start-inventory" class="option-btn btn btn-behance" data-group-product-id="${groupProductId}">Tiến hành kiểm kê</button>
        </div>`
    $('#all-category').append(startInventory);

})

// bắt đầu kiểm kê theo nhóm hàng
$(document).on('click', '#start-inventory', function () {
    const groupProductId = $(this).data('group-product-id');
    fetch("/products/productDetail/" + storeId + "/groupProduct?groupId=" + groupProductId, {
        method: 'GET'
    }).then(response => {
        if (response.ok) {
            return response.json();
        }
    }).then(response => {
        productList = response;
        setTimeout(function () {
            document.querySelector('.inventory-option').remove();
            document.querySelector('.spin-wrapper').remove();
            document.getElementById('inventory-table').style.display = 'block';
            document.getElementById('dropdown-menu-button').style.display = 'none';
        }, 0)
        renderProductList(response);
    })
})


function renderProductList(productList) {
    const productContainer =  document.getElementById('itemList');
    if (productList.length == 0) {
        productContainer.innerHTML = "<span class='text-center'>Không có sản phẩm nào với nhóm hàng này </span>"
    } else {
        productContainer.innerHTML = "";

        let productHtml = ""
        productList.forEach(product => {
            productHtml += `
    <div id="product ${product.id}" type="button" class="product-content" data-product-id = "${product.id}" data-group-product-id="${product.groupProduct.id}">
        <img class="img" src="${product.images.length != 0 ? 'http://localhost:8080/' + product.images[0].id : 'http://localhost:8080/images/image-default.jpg'}" alt="">
        <div class="content">
            <div class="product-name">${product.name}</div>
            <div class="code-number" style="display: none;">${product.codeNumber}</div>
            <div class="quantity">SL trước khi kiểm tra: ${product.quantity.toLocaleString('vi-VN')}</div>
            <div class="checked-quantity"><span>SL sau khi kiểm tra: </span> 
                <input class="input-quantity" type="text">
            </div>
            <button class="btn-edit" style="display: none">Chỉnh sửa</button>
            <button class="btn-confirm">Xác nhận</button>
        </div>
    </div>`
        })

        productContainer.innerHTML = productHtml;
    }
}

$(document).on('input', 'input-quantity', function () {
    const inputQuantity = $(this).val();
    console.log(inputQuantity)
})

$(document).on('click', '.btn-confirm', function() {
    const clickedProduct = $(this).closest('.product-content');
    const checkedQuantity = clickedProduct.find('input').val();
    if (checkedQuantity == '') {
        toastr.warning("Xin hãy nhập số lượng sau khi kiểm tra.")
    } else if (isNaN(checkedQuantity)) {
        toastr.warning("Vui lòng nhập dữ liệu kiểu số.")
    } else if (checkedQuantity < 0) {
        toastr.warning("Số lượng không thể nhỏ hơn 0.")
    } else {
        clickedProduct.appendTo('#itemList');
        clickedProduct.css('background-color', '#b5c773')
        clickedProduct.find('.btn-edit').css('display', 'inline-block');
        clickedProduct.find('.btn-confirm').css('display', 'none');
        clickedProduct.find('input').val(checkedQuantity).attr('disabled', true);

        
        const nextProduct = clickedProduct.next();
        if (nextProduct.length > 0) {
            nextProduct.insertBefore(clickedProduct);
        }
    }

    const productIdSelected = clickedProduct.data('product-id');
    let productIndex = -1;
    for (let i = 0; i < productList.length; i++) {
        if (productList[i].id === productIdSelected) {
            productIndex = i;
            break;
        }
    }

    if (productIndex !== -1) {
        const productSelected = productList.splice(productIndex, 1)[0];
        productList.push(productSelected);
    }
});


$(document).on('click', '.btn-edit', function() {
    const clickedProduct = $(this).closest('.product-content');
    clickedProduct.find('input').attr('disabled', false);
});

// phân loại theo nhóm hàng bảng Kiểm kê
$('.group-1 li').on('click', function() {
    const dropdownButton = document.getElementById('dropdown-menu-button');
    let selectedGroupProduct = $(this).find('span').text();
    let groupProductId = $(this).find('span').data('group-product-id');
    dropdownButton.textContent = selectedGroupProduct + " ";

    if (selectedGroupProduct == "Chọn tất cả") {
        $('.product-content').show();
        toastr.success("Hiển thị theo tất cả nhóm hàng")
    } else {
        $('.product-content').hide();
        $(`.product-content[data-group-product-id="${groupProductId}"]`).show();
        toastr.success("Hiển thị theo nhóm hàng: " + selectedGroupProduct)
    }
});





// search bảng kiểm kê
$(document).ready(function() {

    $('#searchInput').on('input', function() {
        let searchText = $(this).val().toLowerCase().trim();
        if (searchText === "") {
            $('.product-content').show();
            $(".search-results-container").css("display", "none");
        }
        if (searchText.length >= 1) {
            $('.product-content').hide();
            $('.product-content').each(function() {
                let name = $(this).find('.product-name').text();
                let codeNumber = $(this).find('.code-number').text();

                // Kiểm tra xem tên hoặc mã sản phẩm có trùng với searchText hay không
                if (name.toLowerCase().includes(searchText) || codeNumber.includes(searchText)) {
                    let highlightedName = highlightSearchText(name, searchText);
                    $(this).find('div.product-name').html(highlightedName);
                    $(this).show();
                }
            });
        }


    });
});

function highlightSearchText(text, searchText) {
    const regex = new RegExp(searchText, 'gi');
    return text.replace(regex, '<span class="high-light">$&</span>');
}

// search bảng kết quả kiểm kê
$(document).ready(function() {

    $('#searchInput2').on('input', function() {
        let productListBySearch = [];
        let searchText = $(this).val().toLowerCase().trim();
        if (searchText === "") {
            productListBySearch = productList;
            renderPagination(productListBySearch);
        }
        if (searchText.length >= 1) {
            productList.forEach(product => {
                // Kiểm tra xem tên hoặc mã sản phẩm có trùng với searchText hay không
                if (product.name.toLowerCase().includes(searchText) || product.codeNumber.includes(searchText)) {
                    productListBySearch.push(product);
                }
            });
            renderPagination(productListBySearch)
            // $('table tbody tr').each(function() {
            //     // let currentName = $(this).find('td:nth-child(3)').text();
            //     // let currentCodeNumber = $(this).find('td:nth-child(3)').text();
            //     // if (currentGroupProduct === groupProduct) {
            //     //
            //     // }
            // });
        }
    });
});

// Xác nhận hoàn thành kiểm kê
$(document).on('click', '#btn-done', function() {
    if ($('.product-content').length == 0) {
        $('#inventory-table').css('display', 'none');
        $('#table-result').css('display', 'block');
    } else {
        let count = 0;
        $('.product-content').each(function () {
            let input = $(this).find('input').val();

            if (input == "") {
              count++;
            }
        });


        if (count != 0) {
            $('#confirmModal').modal('show');
        } else {
            showResultTable();
            window.scrollTo(0, 0);
            $('#inventory-table').css('display', 'none');
        }
    }
});

$(document).on('click', '#confirmDone', function() {
    showResultTable();
    $('#inventory-table').css('display', 'none');
    window.scrollTo(0, 0);
    $('#confirmModal').modal('hide');
});

let productListRender = [];
function showResultTable() {
    $('#table-result').css('display', 'block');
    let currentProduct = 0;

    let products = [];
    $(".product-content").each(function() {
        let productId = $(this).data('product-id');
        let quantityBeforeCheck = parseInt($(this).find(".quantity").text().split(":")[1].trim().replace(/\./g, ""));
        let quantityAfterCheck = $(this).find(".input-quantity").val().trim().replace(/\./g, "");
        let quantityAfterCheckNumber;
        if (quantityAfterCheck !== '' && !isNaN(quantityAfterCheck)) {
            quantityAfterCheckNumber = parseInt(quantityAfterCheck);
            productList.forEach(product => {
                if (product.id == productId) {
                    productListRender.push(product);
                }
            })
            productListRender[currentProduct].quantityBeforeCheck = quantityBeforeCheck;
            productListRender[currentProduct].quantityAfterCheck = quantityAfterCheckNumber;
            currentProduct++;
            let product = {
                productId: productId,
                quantityBeforeCheck: quantityBeforeCheck,
                quantityAfterCheck: quantityAfterCheck
            }
            products.push(product);
        }
    });
    currentProduct = 0;
    let data = {
        inventoryName: inventoryName,
        userId: userId,
        products: products
    };
    console.log(data)
    if (products.length !== 0) {
        fetch("/inventory/" + storeId +"/create", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) {
                    toastr.success("Đã lưu lại kết quả kiểm kê");

                } else {
                    toastr.error("Thất bại");
                }
            })
            .catch(error => {
                // Xử lý khi có lỗi xảy ra
                console.log("Error:", error);
            });
    }

    renderPagination(productListRender);
}

function renderBlogs(productList) {
    let tableBody = $("#table-result tbody");
// Xóa hết nd đang có trước khi render
    tableBody.innerHTML = "";

// Tạo nd
    let productHtml = "";
    productList.forEach((product) => {
        let row = $(`<tr data-product-id='${product.id}'>`);
        row.append($("<td>").append($('<div class="input-group">' +
            '<input class="form-check-input" type="checkbox" value="" aria-label="">' +
            '</div>')));

        row.append($(`<td>${product.codeNumber}</td>`));

        row.append($(`<td>${product.name}</td>`));

        row.append($(`<td>${product.groupProduct.name}</td>`));

        row.append($(`<td>${product.initialPrice}</td>`));

        row.append($(`<td>${product.quantityBeforeCheck.toLocaleString('vi-VN')}</td>`));

        row.append($(`<td ${product.quantityAfterCheck !== product.quantityBeforeCheck ? 'class="red-text"' : ''} >${product.quantityAfterCheck.toLocaleString('vi-VN')}</td>`));

        productHtml += row.prop('outerHTML');;
    })
    tableBody.html(productHtml);
}
function renderPagination(productList) {
    $('#pagination').pagination({
        dataSource: productList,
        pageSize: 20,
        showSizeChanger: true,
        callback: function (data, pagination) {
            renderBlogs(data);
        }
    })
}

//phân loại theo nhóm hàng bảng kết quả kiểm kê
$('.group-2 li').on('click', function() {
    const dropdownButton = document.getElementById('dropdown-menu-button-2');
    let selectedGroupProduct = $(this).find('span').text();
    console.log(selectedGroupProduct)
    let groupProductId = $(this).find('span').data('group-product-id');
    console.log(groupProductId)
    dropdownButton.textContent = selectedGroupProduct + " ";
    let productListByGroup = [];
    if (selectedGroupProduct == "Chọn tất cả") {
        productListByGroup = productListRender;
        console.log(productListByGroup)
        renderPagination(productListByGroup);
        toastr.success("Hiển thị theo tất cả nhóm hàng")
    } else {
        productListRender.forEach(product => {
            if (product.groupProduct.id == groupProductId) {
                productListByGroup.push(product);
            }
        })
        renderPagination(productListByGroup);
        console.log(productListByGroup)
        toastr.success("Hiển thị theo nhóm hàng: " + selectedGroupProduct)
    }
});

// xử lý checkbox
$(document).ready(function() {
    $('#table-result').on('click change', 'tbody tr', function(event) {
        const checkbox = $(this).find("input[type='checkbox']");
        checkbox.prop("checked", !checkbox.prop("checked"));

        let checkedCount = $("tbody input[type='checkbox']:checked").length;
        if (checkedCount >= 1) {
            $("#update-inventory").show();
        } else {
            $("#update-inventory").hide();
        }
    });
});

$(document).ready(function() {
    $("#checkAll").click(function() {
        let isChecked = $(this).prop("checked");
        $("tbody input[type='checkbox']").prop("checked", isChecked);
        if (isChecked) {
            $("#update-inventory").show();
        } else {
            $("#update-inventory").hide();
        }
    });
});

// xử lý nút update-inventory
let isChanged = false;
$('#table-result').ready(function() {
    $('#update-inventory').on('click', function () {
        isChanged = true;
        $('#confirm-update').modal('show');
    })

    $('#confirm-update-btn').on('click', function () {
        let products = [];
        $("tbody input[type='checkbox']:checked").each(function() {
            let productId = $(this).closest('tr').data('product-id');
            let quantityBeforeCheck = $(this).closest('tr').find("td:nth-child(6)").text().trim().replace(/\./g, "");
            let quantityAfterCheck = $(this).closest('tr').find("td:nth-child(7)").text().trim().replace(/\./g, "");

            if (quantityBeforeCheck !== quantityAfterCheck) {
                let product = {
                    productId: productId,
                    quantityBeforeCheck: quantityBeforeCheck,
                    quantityAfterCheck: quantityAfterCheck
                };
                products.push(product);
            }
        })
        console.log(products)
        let data = {
            products: products
        };

        fetch("/inventory/" + storeId +"/update-quantity", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) {
                    toastr.success("Cập nhật sản phẩm thành công");
                    window.location.reload();
                } else {
                    toastr.error("Thất bại");
                }
            })
            .catch(error => {
                // Xử lý khi có lỗi xảy ra
                console.log("Error:", error);
            });


        $('#confirm-update').modal('hide');
    })

})





