// lấy ra id cửa hàng
const path = window.location.pathname;
const parts = path.split("/");
const storeId = parts[2];
function toggleLeftPanel() {
    document.querySelector('.container').classList.toggle('minimized');
}





// phân trang
const containerWidth5 = $('.right-panel').width();
const itemWidth5 = $('.product-container').outerWidth(true);
const visibleItems5 = Math.floor(containerWidth5 / itemWidth5);
const totalItems5 = $('.product-container').length;
const totalPages5 = Math.ceil(totalItems5 / (visibleItems5 * 3));
let containerWidth10;
let itemWidth10;
let visibleItems10;
let totalItems10;
let totalPages10;
let currentPage = 1;



$('#prevBtn').prop('disabled', true);
$(document).ready(function() {
    paginationPage(currentPage, totalPages5, containerWidth5);
});

$('.toggle-button').on('click', function () {

    if ($('.container').hasClass('minimized')) {
        $('.product-container').css('width', 'calc((100% - 220px) / 10)');

        setTimeout(function () {
            containerWidth10 = $('.right-panel').width();
            itemWidth10 = $('.product-container').outerWidth(true);
            visibleItems10 = Math.floor(containerWidth10 / itemWidth10);
            totalItems10 = $('.product-container').length;
            totalPages10 = Math.ceil(totalItems10 / (visibleItems10 * 3));
            if (currentPage == totalPages5) {
                currentPage = totalPages10;
            }
            paginationPage(currentPage,totalPages10, containerWidth10);
        },300)

    } else {
        $('.product-container').css('width', 'calc((100% - 110px) / 5)');
        setTimeout(function () {
            if (currentPage ==totalPages10) {
                currentPage = totalPages5;
            }
            paginationPage(currentPage,totalPages5, containerWidth5);
        },300)
    }


})

function paginationPage(currentPage1, totalPages1, containerWidth1) {
    $('#nextBtn').off().on('click', function() {
        if (currentPage1 < totalPages1) {
            currentPage1++;
            currentPage = currentPage1;
            let translateX = -(currentPage1 - 1) * containerWidth1;
            $('.product-list').css('transform', 'translateX(' + translateX + 'px)');
            $('#prevBtn').prop('disabled', false);

            // Disable next button if on the last page
            if (currentPage1 === totalPages1) {
                $('#nextBtn').prop('disabled', true);
            }
            setTimeout(function() {
                $('.product-list').removeClass('slide-right');
            }, 300);
        }

    });

    // Handle previous button click
    $('#prevBtn').off().on('click', function() {
        if (currentPage1 > 1) {
            currentPage1--;
            currentPage = currentPage1;
            let translateX = -(currentPage1 - 1) * containerWidth1;
            $('.product-list').css('transform', 'translateX(' + translateX + 'px)');
            $('#nextBtn').prop('disabled', false);

            // Disable previous button if on the first page
            if (currentPage1 === 1) {
                $('#prevBtn').prop('disabled', true);
            }

            setTimeout(function() {
                $('.product-list').removeClass('slide-left');
            }, 300);
        }

    });
    return currentPage1;
}

// xử lý click vào btn sản phẩm
let productQuantities = {};
let initialProductQuantities = {};
let totalQuantity = 0;
let finalPrice = 0;
$(document).ready(function() {
    $('.product-container').each(function() {
        let productId = $(this).data('product-id');
        let productQuantity = parseInt($(this).find('.quantity').text().split(': ')[1]);
        initialProductQuantities[productId] = productQuantity;
    });

    $('.product-container').off().on('click', function() {
        let productId = $(this).data('product-id');
        let codeNumber = $(this).data('product-code-number');
        let productName = $(this).find('p').text();
        let productPrice = parseInt($(this).find('span').first().text().replace(/,/g, '').replace('đ', ''));
        let productQuantity = parseInt($(this).find('.quantity').text().split(': ')[1]);
        $(this).find('.quantity').text(`SL: ${productQuantity - 1}`);

        handleClick(productId, codeNumber, productName, productPrice, productQuantity);
    });

});

function handleClick(productId, codeNumber, productName, productPrice, productQuantity) {
    let totalPrice;
    let productPriceHtml = productPrice.toLocaleString();
    if (productQuantity <= 0) {
        return toastr.warning("Sản phẩm này đã hết hàng");
    }
    // Tạo HTML cho thẻ order mới
    if (!productQuantities.hasOwnProperty(productId)) {
        productQuantities[productId] = 1;
        let orderHTML = `
    <div id="order-${productId}" class="order" data-product-id="${productId}">
        <button class="icon icon-info"><i class="ti ti-info"></i></button>
        <div class="product-info">
            <div>
                <span>${codeNumber}</span>
                <span class="product-name">${productName}</span>
            </div>
            <div>
                <button id="quantity-minus">
                   <i class="fa fa-minus"></i>
                </button>
                <input class="input-quantity" type="text" value="${productQuantities[productId]}">
                <button id="quantity-plus">
                    <i class="fa fa-plus"></i>
                </button>
            </div>
        </div>
        <div class="price">
            <span>Đơn giá</span>
            <span class="input-price">${productPriceHtml}</span>
        </div>
        <div class="discount">
            <span>Giảm giá</span>
            <input class="sale" type="text" value="">
        </div>
        <div class="order-total">
            <span>Thành tiền</span>
            <span class="total" style="font-weight: 500">${productPriceHtml}</span>
        </div>
        <button class="icon icon-delete"><i class="ti ti-trash"></i></button>
    </div>
`;
        productQuantity--;
        $('.order-list').append(orderHTML);
        totalQuantity++;
        $('.left-panel-footer span:nth-child(2)').text(`Tổng SL: ${totalQuantity}`);
        getTotalOrder(finalPrice);
    } else {
        productQuantities[productId]++;
        $(`#order-${productId}`).find('.input-quantity').val(productQuantities[productId]);
        totalPrice = productQuantities[productId] *  productPrice;
        $(`#order-${productId} .total`).text(totalPrice.toLocaleString());
        productQuantity--;
        totalQuantity++;
        $('.left-panel-footer span:nth-child(2)').text(`Tổng SL: ${totalQuantity}`);
        getTotalOrder(finalPrice)
    }
    let inputPrice = $(`#order-${productId} .input-price`).text();
    inputPrice = parseInt(inputPrice.replace(/\./g, ''));

    $(`#order-${productId} .sale`).on('input', function() {
        let saleValueWithComma = $(this).val();
        console.log(saleValueWithComma)

        let saleValue = parseInt(saleValueWithComma.replace(/\./g, ''));
        if (saleValueWithComma === '') {
            saleValue = 0;
        }
        if (saleValue < 0 || isNaN(saleValue)) {
            $(this).css('color', 'red');
            toastr.warning("Giá trị giảm giá không thể nhỏ hơn 0, lớn hơn tổng giá trị sản phẩm hoặc là một số.")
            saleValue = 0
            totalPrice = inputPrice * productQuantities[productId];
        } else if (saleValue > inputPrice * productQuantities[productId]) {
            $(this).css('color', 'red');
            toastr.warning("Giá trị giảm giá không thể nhỏ hơn 0, lớn hơn tổng giá trị sản phẩm hoặc là một số.")
            saleValue = inputPrice * productQuantities[productId];
            totalPrice = 0;
        }
        else {
            $(this).css('color', '#000');
            totalPrice = inputPrice * productQuantities[productId] - saleValue;
        }
        $(`#order-${productId} .total`).text(totalPrice.toLocaleString());
        $(this).val(saleValue.toLocaleString());
        console.log(saleValue.toLocaleString())
        getTotalOrder(finalPrice);
    });

    $(`#order-${productId} .input-quantity`).on('input', function() {
        let quantityValueWithComma = $(this).val();
        let quantityValue = parseInt(quantityValueWithComma.replace(/\./g, ''));
        let saleValue = $(`#order-${productId} .sale`).val().replace(/\./g, '');
        $(`#order-${productId} .input-quantity`).val(`${quantityValue.toLocaleString()}`)
        productQuantity = initialProductQuantities[productId];
        if (isNaN(quantityValue)) {
            quantityValue = 0;
            $(`#order-${productId} .input-quantity`).val(`${quantityValue.toLocaleString()}`)
            productQuantities[productId] = 0;
        } else if (quantityValue < 0 ) {
            $(this).css('color', 'red');
            productQuantities[productId] = 0;
        } else if (quantityValue > productQuantity) {
            toastr.error("Số lượng tối đa của sản phẩm này là " + `${productQuantity.toLocaleString()}`)
            $(`#order-${productId} .input-quantity`).val(`${productQuantity.toLocaleString()}`)
            productQuantities[productId] = initialProductQuantities[productId];

            $(`[data-product-id="${productId}"]`).find('.quantity').text('SL: 0');
        } else if (quantityValue <= productQuantity && quantityValue > 0) {
            $(this).css('color', '#000');
            productQuantities[productId] = quantityValue;
            productQuantity = productQuantity - quantityValue;
            $(`[data-product-id="${productId}"]`).find('.quantity').text(`SL: ${productQuantity.toLocaleString()}`);
        }
        if (productQuantities[productId] > 0) {
            totalPrice = inputPrice * productQuantities[productId] - saleValue;
        } else {
            totalPrice = 0;
        }
        totalQuantity = Object.values(productQuantities).map(Number).reduce((total, currentValue) => total + currentValue, 0);
        $('.left-panel-footer span:nth-child(2)').text(`Tổng SL: ${totalQuantity}`);
        $(`#order-${productId} .total`).text(totalPrice.toLocaleString());
        getTotalOrder(finalPrice);
    });

    $(`#order-${productId} #quantity-minus`).off().on('click', function () {
        let saleValue = $(`#order-${productId} .sale`).val().trim().replace(/\./g, '');
        productQuantity = parseInt($(`[data-product-id="${productId}"]`).find('.quantity').text().split(': ')[1]);
        if (productQuantities[productId] > 0) {
            productQuantities[productId]--;
            $(`#order-${productId}`).find('.input-quantity').val(productQuantities[productId]);
            totalPrice = inputPrice * productQuantities[productId] - saleValue;
            if(totalPrice < 0) {
                totalPrice = 0;
            }
            $(`#order-${productId} .total`).text(totalPrice.toLocaleString());
            totalQuantity--;
            $('.left-panel-footer span:nth-child(2)').text(`Tổng SL: ${totalQuantity}`);
            getTotalOrder(finalPrice);
            $(`[data-product-id="${productId}"]`).find('.quantity').text(`SL: ${++productQuantity}`);
        } else {
            toastr.error("Sản phẩm đã đạt số lượng tối thiểu")
        }
    })

    $(`#order-${productId} #quantity-plus`).off().on('click', function () {
        let saleValue = $(`#order-${productId} .sale`).val().trim().replace(/\./g, '');
        productQuantity = parseInt($(`[data-product-id="${productId}"]`).find('.quantity').text().split(': ')[1]);
        if (productQuantities[productId] < initialProductQuantities[productId]) {
            productQuantities[productId]++;
            $(`#order-${productId}`).find('.input-quantity').val(productQuantities[productId]);
            totalPrice = inputPrice * productQuantities[productId] - saleValue;
            if(totalPrice < 0) {
                totalPrice = 0;
            }
            $(`#order-${productId} .total`).text(totalPrice.toLocaleString());
            totalQuantity++;
            $('.left-panel-footer span:nth-child(2)').text(`Tổng SL: ${totalQuantity}`);
            getTotalOrder(finalPrice);
            $(`[data-product-id="${productId}"]`).find('.quantity').text(`SL: ${--productQuantity}`);
        } else {
            toastr.error("Sản phẩm đã đạt số lượng tối đa")
        }
    })

    $(`#order-${productId} .icon-info`).off().on('click', function () {
        const product = productList.find(item => item.id === productId);
        $('.modal-header h2:first-child').text(`${productName}`)
        $('.detail p:first-child').text(`Giá bán: ${productPriceHtml}`);
        $('.detail div:nth-child(2) span:nth-child(2)').text(productQuantity);
        if (product.groupProduct != null) {
            $('.detail div:nth-child(2) .group-product').text(`Nhóm hàng: ${product.groupProduct.name}`);
        }
        $('.detail .description').text(`Mô tả: ${product.description}`);
        $('.detail p:nth-child(4)').text(`Ghi chú: ${product.note}`)
        $('#productModal').addClass('active');

        $('.btn-success').on('click', function() {
            $('#productModal').addClass('fadeOut')
            setTimeout(function () {
                $('#productModal').removeClass('active fadeOut');
            }, 400)
        });

        $('.close').click(function() {
            $('#productModal').addClass('fadeOut')
            setTimeout(function () {
                $('#productModal').removeClass('active fadeOut');
            }, 400)
        });
    })
}


$(document).on('click', '.icon-delete', function() {
    let orderId = $(this).closest('.order').attr('id');
    let totalPrice = parseInt($(`#${orderId} .total`).text().replace(/\./g, ''));
    let productId = $(this).closest('.order').data('product-id');
    let footerText = $('.left-panel-footer span:nth-child(3)').text();
    let matches = footerText.match(/\d+(,\d+)?/);
    let finalPrice;
    if (matches) {
        finalPrice = parseInt(matches[0].replace(/\./g, ''));
    }

    $(`#${orderId}`).remove();
    toastr.success("Xoá đơn hàng thành công")
    if (productQuantities.hasOwnProperty(productId)) {
        delete productQuantities[productId];
    }

    totalQuantity = Object.values(productQuantities).reduce((total, currentValue) => total + currentValue, 0);
    $('.left-panel-footer span:nth-child(2)').text(`Tổng SL: ${totalQuantity}`);

    $(`[data-product-id="${productId}"]`).find('.quantity').text(`SL: ${initialProductQuantities[productId]}`);

    finalPrice -= totalPrice;
    getTotalOrder(finalPrice);
});




//lấy ra tổng giá trị đơn hàng
function getTotalOrder(finalPrice) {
    let totalPrice = 0;
    $('.total').each(function() {
        let totalValue = parseInt($(this).text().replace(/\./g, ''));
        totalPrice  += totalValue;
    });
    finalPrice = totalPrice;
    $('.left-panel-footer span:nth-child(3)').text(`Tổng đơn hàng: ${finalPrice.toLocaleString()} VNĐ`);
}

// image handle
const wrapper = document.querySelector('.cards-wrapper');

// grab the dots
const dots = document.querySelectorAll('.dot');
let activeDotNum = 0;

dots.forEach((dot, idx) => {
    dot.setAttribute('data-num', idx);

    dot.addEventListener('click', (e) => {

        let clickedDotNum = e.target.dataset.num;

        if(clickedDotNum == activeDotNum) {
            return;
        }
        else {

            let displayArea = wrapper.parentElement.clientWidth;

            let pixels = -displayArea * clickedDotNum
            wrapper.style.transform = 'translateX('+ pixels + 'px)';

            dots[activeDotNum].classList.remove('active');
            dots[clickedDotNum].classList.add('active');
            activeDotNum = clickedDotNum;
        }

    });
})

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
            fetch('/products/' + 1 + '/suggest?name=' + searchText)
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error('Error: ' + response.status);
                    }
                    return response.json();
                })
                .then(function (data) {
                    productList = data;
                    // Hiển thị danh sách kết quả tìm kiếm
                    showSearchResults(productList, searchText);
                })
                .catch(function (error) {
                    console.log('Error:', error);
                });


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
                let productPrice = product.salePrice;
                if (product.promotionalPrice != 0 && product.isOnPromotional == true) {
                    productPrice = product.promotionalPrice;
                }
                let price = $('<p>').text('Giá bán: ' + productPrice);
                let detailDiv = $('<div>');
                let codeNumber = $('<p>').html(highlightedCodeNumber);
                let quantity = $('<p>').text('Tồn kho: ' + product.quantity);

                infoDiv.append(name, price);
                detailDiv.append(codeNumber, quantity);
                listItem.append(img, infoDiv, detailDiv);

                listItem.on('click', function() {
                    let clickedProductId = $(this).data('product-id');
                    let clickedProduct = getProductById(clickedProductId);
                    $(".search-results-container").css("display", "none");
                    // thêm sản phẩm vào order
                    addToOrder(clickedProduct);
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
    function addToOrder(product) {
        let productId = product.id;
        let codeNumber = product.codeNumber;
        let productName = product.name;
        let productPrice = product.salePrice;
        if (product.promotionalPrice != 0 && product.isOnPromotional == true) {
            productPrice = product.promotionalPrice;
        }

        let productQuantity = product.quantity;

        handleClick(productId, codeNumber, productName, productPrice, productQuantity)
    }
});

function getProductById(productId) {
    // Lấy thông tin sản phẩm từ productId, ví dụ từ danh sách productList
    let product = productList.find(function(item) {
        return item.id === productId;
    });
    return product;
}

//xử lý nút Pay
$(document).ready(function() {
    $("#pay").off().on('click', function() {
        let voucherCode = "";
        if ($(".order").length <= 0) {
            toastr.warning("Bạn cần thêm đơn hàng trước")
        } else {
            let flag = true;
            $('.input-quantity').each(function () {
                if ($(this).val() == 0) {
                    const productName = $(this).parent().prev().find('span:nth-child(2)').text();
                    toastr.error("Sản phẩm " + productName + " chưa nhập số lượng")
                    flag = false;
                    return;
                }
            })
            if (flag) {
                let productRow = "";
                let count = 1
                $(".order").each(function() {
                    const productId = $(this).data("product-id");
                    let quantity = $(this).find(".input-quantity").val();
                    let totalPrice = $(this).find(".total").text();
                    let discount = $(this).find(".sale").val();
                    let productName = $(this).find(".product-name").text();
                    let inputPrice = $(this).find(".input-price").text().replace(/\./g, '');
                    productRow += `
                <tr data-product-id="${productId}" data-sale-price-id="${inputPrice}">
                        <td>
                            ${count}
                        </td>
                        <td>
                            ${productName}
                        </td>
                        <td>
                            ${quantity}
                        </td>
                        <td>
                            ${discount != 0? discount : 0} 
                        </td>
                        <td>0</td>
                        <td>
                            ${totalPrice}
                        </td>
                    </tr>
                `

                    count++;
                })

                $('#table-result').html(productRow);
                calculateTotals();

                $('#payModal').addClass('active');
                let oldValue = "";
                $('#input-code').on('blur keypress', function (event) {
                    if (event.type === "blur" || event.which === 13) {
                        let newValue = $('#input-code').val();
                        if (newValue !== oldValue) {
                            checkValidVoucher();
                        }
                        oldValue = newValue;
                    }
                })
            }

            $('.close').on('click', function() {
                $('#voucher-active').text("")
                $('#input-code').val("");
                $('#input-code').attr('disabled', false);
                $('#voucher-active').hide();
                $('#voucher-invalid').hide();
                $("#table-result tr").remove()
                $('#payModal').addClass('fadeOut')
                setTimeout(function () {
                    $('#payModal').removeClass('active fadeOut');
                }, 400)
            });

            $('#btn-pay').off().on('click', function() {
                let products = [];
                $("#table-result tr").each(function() {
                    const productId = $(this).data("product-id");
                    let clientId = $('.choose-client').data('client-id');
                    let quantity = $(this).find("td:nth-child(3)").text().trim().replace(/\./g, '');
                    let discount = parseInt($(this).find("td:nth-child(4)").text().replace(/\./g, ''));
                    let discountVoucher = parseInt($(this).find("td:nth-child(5)").text().replace(/\./g, ''));
                    let totalPrice = $(this).find("td:nth-child(6)").text().trim().replace(/\./g, '');
                    const userId =  $('#avatar-container').data('user-id');


                    // Tạo đối tượng product
                    let product = {
                        userId: userId,
                        clientId: clientId,
                        productId: productId,
                        quantity: parseInt(quantity),
                        discount: discount + discountVoucher,
                        totalPrice: parseInt(totalPrice),
                        voucherCode: voucherCode
                    };

                    // Thêm đối tượng product vào mảng products
                    products.push(product);
                });
                // Tạo đối tượng data từ mảng products
                let data = {
                    products: products
                };

                console.log(data)

                // Gửi dữ liệu dạng JSON lên server sử dụng fetch
                fetch("/order/" + storeId + "/create", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        if (response.ok) {
                            toastr.success("Thanh toán thành công");
                            setTimeout(function() {
                                location.reload();
                            }, 1000);
                        } else {
                            toastr.error("Thanh toán thất bại");
                        }
                    })
                    .catch(error => {
                        // Xử lý khi có lỗi xảy ra
                        console.log("Error:", error);
                    });
            });

        }

        function checkValidVoucher() {
            $('#voucher-active').hide();
            $('#voucher-invalid').hide();

            const code = $("#input-code").val().trim();
            if (code !== "") {
                fetch("/voucher/" + storeId + "/check-expired?code=" + code, {
                    method: 'GET'
                })
                    .then(response => {
                        return response.json();
                    })
                    .then(data => {
                        if (data.status === true) {
                            console.log(data)
                            if ($('.choose-client span').text() !== "") {
                                const productIdList = data.products.map((product) => product.id);
                                const validQuantityVoucher = data.originalQuantity - data.usedQuantity;
                                let flag1 = false;
                                let flag2 = false;
                                let count = 0;
                                let informText = "* Áp dụng thành công cho các sản phẩm: ";
                                $('#table-result tr').each(function () {
                                    let productId = $(this).data('product-id');
                                    if (productIdList.includes(productId)) {
                                        flag1 = true;

                                        if (count <= validQuantityVoucher) {
                                            flag2 = true;
                                            let quantity = parseInt($(this).find("td:nth-child(3)").text());
                                            console.log(quantity)
                                            let inputPrice = parseInt($(this).data('sale-price-id'));
                                            let totalPrice = parseInt($(this).find("td:nth-child(6)").text().replace(/\./g, ''));
                                            console.log(totalPrice)

                                            let productName = $(this).find("td:nth-child(2)").text().trim();
                                            informText += productName + ", ";


                                            if (data.reduceType == "Theo phần trăm") {
                                                let discountVoucher = parseInt((quantity * (inputPrice * (data.reducedPrice/100))).toFixed(0));
                                                totalPrice = totalPrice - discountVoucher;
                                                $(this).find("td:nth-child(5)").text(discountVoucher.toLocaleString())
                                                $(this).find("td:nth-child(6)").text(totalPrice.toLocaleString())
                                            } else {
                                                let discountVoucher = quantity * data.reducedPrice;
                                                totalPrice = totalPrice - discountVoucher;
                                                $(this).find("td:nth-child(5)").text(discountVoucher.toLocaleString())
                                                $(this).find("td:nth-child(6)").text(totalPrice.toLocaleString())
                                            }
                                            count++;
                                        } else {
                                            let productName = $(this).find("td:nth-child(2)").text();
                                            toastr.error("Sản phẩm " + productName + " không thể áp dụng voucher do đã hết lượt sử dụng")
                                        }

                                    }
                                })
                                if (flag1 == true && flag2 == true) {
                                    voucherCode = $('#input-code').val();
                                    $('#voucher-active').text(informText.slice(0, -2));
                                    $('#voucher-active').show();
                                    $('#input-code').attr('disabled', true);
                                    calculateTotals();
                                } else if (flag1 == true && flag2 == false) {
                                    $('#input-code').attr('disabled', false);
                                } else {
                                    $('#voucher-invalid').text("* Voucher này không áp dụng cho các sản phẩm đã chọn");
                                    $('#voucher-invalid').show();
                                }
                            } else {
                                $('#voucher-invalid').text("* Voucher không áp dụng cho khách lẻ, hãy chọn khách hàng trước");
                                $('#voucher-invalid').show();
                            }
                        } else if (data.status === false) {
                            $('#voucher-invalid').text("* Voucher này chưa được kích hoạt");
                            $('#voucher-invalid').show();
                        } else {
                            $('#voucher-invalid').text("* " + data.message);
                            $('#voucher-invalid').show();
                        }
                    })
            }

        }
    });


});

// tính tổng giá trị đơn hàng


function calculateTotals() {
    const totalQuantityCell = $("#totalQuantity");
    const totalDiscountPriceCell = $('#discount-price');
    const totalDiscountVoucherCell = $('#discount-voucher');
    const totalAmountCell = $("#totalAmount");
    let totalQuantity = 0;
    let totalDiscountPrice = 0;
    let totalDiscountVoucher = 0;
    let totalAmount = 0;

    $("tbody tr").each(function () {
        const quantityCell = $(this).find("td:nth-child(3)");
        const discountPriceCell = $(this).find("td:nth-child(4)");
        const discountVoucherCell = $(this).find("td:nth-child(5)");
        console.log(discountVoucherCell.text())
        const amountCell = $(this).find("td:nth-child(6)");
        console.log(amountCell.text().trim().replace(/./g, ''))
        totalQuantity += parseInt(quantityCell.text());
        totalDiscountPrice += parseInt(discountPriceCell.text().trim().replace(/\./g, ''));
        totalDiscountVoucher += parseInt(discountVoucherCell.text().replace(/\./g, ''));
        totalAmount += parseInt(amountCell.text().trim().replace(/\./g, ''));
    });

    totalQuantityCell.text(totalQuantity);
    totalDiscountPriceCell.text(totalDiscountPrice.toLocaleString());
    totalDiscountVoucherCell.text(totalDiscountVoucher.toLocaleString());
    totalAmountCell.text(totalAmount.toLocaleString());
}



// chọn nhóm hàng

const dropdownMenu = document.getElementById("dropdownMenu");
function toggleDropdown() {
    if (dropdownMenu.style.display === "block") {
        dropdownMenu.style.display = "none";
    } else {
        dropdownMenu.style.display = "block";
    }
}

// Đóng menu khi click bên ngoài menu
window.onclick = function(event) {
    if (!event.target.matches('.dropdown-btn')) {
        if (dropdownMenu.style.display === "block") {
            dropdownMenu.style.display = "none";
        }
    }
};

$('.dropdown-menu li').on('click', function() {
    const dropdownButton = document.querySelector('.dropdown-btn');
    let selectedGroupProduct = $(this).text();
    dropdownButton.textContent = selectedGroupProduct + " ";



    if (selectedGroupProduct == "Chọn tất cả") {
        toastr.success("Hiển thị theo: Tất cả nhóm hàng")
        $('.product-container').show();
    } else {
        toastr.success("Hiển thị theo nhóm hàng: " + selectedGroupProduct)
        let groupProductId = $(this).data('group-product-id');
        $('.product-container').css('display', 'none');
        $(`button.product-container[data-group-product-id = ${groupProductId}]`).show();
    }
});


// tìm kiếm khách hàng
$(document).ready(function() {
    let clientList = [];
    let searchResults = $('#searchResults2');

    $('#searchInput2').on('input', function() {
        $('#add-client').css('display', 'none')
        $(".search-results-container-2").css("display", "block");
        let searchText = $(this).val().toLowerCase();
        if (searchText === "") {
            $('#add-client').css('display', 'flex')
            $(".search-results-container-2").css("display", "none");
        }
        if (searchText.length >= 1) {
            fetch('/client/' + storeId + '/suggest?name=' + searchText)
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error('Error: ' + response.status);
                    }
                    return response.json();
                })
                .then(function (data) {
                    clientList = data;
                    showSearchResults(clientList, searchText);
                })
                .catch(function (error) {
                    console.log('Error:', error);
                });
            // Hiển thị danh sách kết quả tìm kiếm
        }
    });
    function showSearchResults(results, searchText) {
        searchResults.empty();
        if (results.length > 0) {

            results.forEach(function(client) {
                let clientName = highlightSearchText(client.name, searchText)
                let listItem = $('<li>').addClass('client-item')
                let content = `
                <p>${clientName}</p>
                <p>${client.phone}</p>
                `;

                listItem.append(content);

                listItem.on('click', function() {
                    chooseClient(client)
                });

                searchResults.prepend(listItem);
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

});

function chooseClient(client) {
    $('.search-client').hide();
    $('.choose-client span').text(`KH: ${client.name}`);
    $('.choose-client').data('client-id', client.id);
    $('.choose-client').css('display', 'flex')

}

$(document).ready(function () {
    $('#remove-client').on('click', function () {
        $('.search-client').show();
        $('.choose-client').css('display', 'none')
    })
})

// xử lý nút thêm khách hàng
$(document).ready(function() {
    $('#add-client').on('click', function () {
        $('#addClientModal').css('display', 'flex')
    })

    $('.btn-close').on('click', function () {
        $('#addClientModal').css('display', 'none')
    })
})

// thêm khách hàng mới

$(document).ready(function() {
    $('#submitForm').on('submit', function (event) {
        event.preventDefault();

        const fullName = $('#inputFullName').value;
        const address = $('#inputLocation').value;
        const email = $('#inputEmailAddress').value;
        const phone = $('#inputPhone').value;
        const birthday = $('#inputBirthday').value;

        // Tạo một đối tượng
        const formData = new FormData();
        formData.append('fullName', fullName);
        formData.append('address', address);
        formData.append('email', email);
        formData.append('phone', phone);
        formData.append('birthday', birthday);


        fetch("/client/" + storeId + "/create", {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (response.ok) {
                    toastr.success("Thêm khách hàng thành công");
                    return response.json();

                } else {
                    toastr.error("Thêm khách hàng không thành công");
                }
            })
            .then(res => {
                $('#inputFullName').val('');
                $('#inputLocation').val('');
                $('#inputEmailAddress').val('');
                $('#inputPhone').val('');
                $('#inputBirthday').val('');
                $('#addClientModal').css('display', 'none');
                chooseClient(res)
            })
            .catch(error => {
                // Xử lý khi có lỗi xảy ra
                console.log("Error:", error);
            });
    })
})






