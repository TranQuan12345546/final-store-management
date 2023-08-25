
$(function() {
    $('input[name="daterange"]').daterangepicker({
        opens: 'left',
        locale: {
            format: 'DD/MM/YYYY'
        },
        isInvalidDate: function(date) {
            // Kiểm tra nếu ngày đã qua thì trả về true
            return date.isBefore(moment(), 'day') || date.isSame(moment(), 'day');
        }
    }, function(start, end, label) {
        console.log("A new date selection was made: " + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD'));
    });
});

$(document).ready(function () {
    $('#discountType').on('change', function () {
        if (this.value === "1") {
            $('.percent').show();
        } else if (this.value === "2") {
            $('.percent').hide();
        }
    })

    $("#inputReducedPrice").on("blur keypress", function (event) {
        if (event.type === "blur" || event.which === 13) {
            calculateDiscountedPrices();
        }
    });

    function calculateDiscountedPrices() {
        const discountType = $("#discountType").val();
        const reducedPriceInput = $("#inputReducedPrice").val();
        const productList = $("#table-review tbody tr");

        if (reducedPriceInput !== "") {
            let reducedPriceInputNumber = parseInt(reducedPriceInput);
            productList.each(function () {
                const salePriceCell = $(this).find("td:nth-child(3)");
                const discountedPriceCell = $(this).find("td:nth-child(4)");

                if (discountType === "1") {
                    const originalPrice = parseFloat(salePriceCell.text().replace(/\./g, ""));
                    const discountedPrice = parseInt((originalPrice * (1 - reducedPriceInputNumber / 100)).toFixed(0));
                    discountedPriceCell.text(discountedPrice.toLocaleString("vi-VN"));
                } else if (discountType === "2") {
                    const originalPrice = parseFloat(salePriceCell.text().replace(/\./g, ""));
                    const discountedPrice = originalPrice - reducedPriceInputNumber;
                    discountedPriceCell.text(discountedPrice.toLocaleString("vi-VN"));
                }
            });
        }
    }

    $('#all-product').on('click', function () {
        $('.all-product-container').hide();
        $('.list-group-container').hide();
        renderTableProduct(productList);
    })

    $('#choose-group').on('click', function () {
        $('.all-product-container').hide();
        $('#table-review').hide();
        fetch('/group-product/all-group/' + storeId, {
            method: 'GET'
        }).then(response => {
            if (response.ok) {
                return response.json();
            }
        }).then(groupList => {
            $('.list-group-container').show();
            let groupContainer = $('.list-group-container')
            let groupProductHtml = "";
            groupList.forEach(groupProduct => {
                groupProductHtml += `
            <button class="choose-category" data-group-product-id="${groupProduct.id}" >${groupProduct.name}</button>
            `
            })
            groupContainer.html(groupProductHtml);
        })
    })

    $(document).on('click', '.choose-category', function () {
        const groupProductId = $(this).data('group-product-id')
        fetch("/products/productDetail/" + storeId + "/groupProduct?groupId=" + groupProductId, {
            method: 'GET'
        }).then(response => {
            if (response.ok) {
                return response.json();
            }
        }).then(productList => {
            $('.list-group-container').hide();
            renderTableProduct(productList)
        })
    })

    $('#choose-product').on('click', function () {
        $('.list-group-container').hide();
        $('#table-review').hide()
        let productHtml = "";
        if (productList.length == 0) {
            productHtml = `<tr>
                           <td colspan="4" style="text-align: center; font-size: 14px">Không có sản phẩm nào thuộc nhóm hàng này</td>
                       </tr>  `
        }
        productList.forEach(product => {
            productHtml += `
                <tr data-product-id="${product.id}">
                    <td>
                        <div class="input-group">
                            <input class="form-check-input" type="checkbox" value="" aria-label="">
                        </div>
                    </td>
                    <td>${product.codeNumber}</td>
                    <td>${product.name}</td>
                    <td>${product.salePrice.toLocaleString("vi-VN")}</td>
                </tr>
            `
        })
        $('.all-product-container tbody').html(productHtml);
        $('.all-product-container').show();


    });

    $(document).on('click', ".all-product-container tbody tr", function (event) {
        if ($(event.target).is("input[type='checkbox']")) {
            return;
        }
        const checkbox = $(this).find("input[type='checkbox']");
        checkbox.prop("checked", !checkbox.prop("checked"));
    });

    $('#confirm-product').on('click', function () {
        $('.all-product-container').hide();
        $('#table-review .table tbody').empty()
        $(".all-product-container tbody input[type='checkbox']:checked").each(function () {
            let productId = $(this).closest("tr").data('product-id');
            let codeNumber = $(this).closest("tr").find("td:nth-child(2)").text();
            let productName = $(this).closest("tr").find("td:nth-child(3)").text();
            let salePrice = $(this).closest("tr").find("td:nth-child(4)").text();
            let row = $("<tr>").data('product-id', productId);
            row.append($("<td>").text(codeNumber));
            row.append($("<td>").text(productName));
            row.append($("<td>").text(salePrice));
            row.append($("<td>").text(""))
            $('#table-review .table tbody').append(row)
        });
        calculateDiscountedPrices();
        $('#table-review').show();
    });

    function renderTableProduct(productList) {
        $('#table-review .table tbody').empty()
        let productHtml = "";
        if (productList.length == 0) {
            productHtml = `<tr>
                           <td colspan="4" style="text-align: center; font-size: 14px">Không có sản phẩm nào thuộc nhóm hàng này</td>
                       </tr>  `
        }
        productList.forEach(product => {
            productHtml += `
                <tr data-product-id="${product.id}">
                    <td>${product.codeNumber}</td>
                    <td>${product.name}</td>
                    <td>${product.salePrice.toLocaleString("vi-VN")}</td>
                    <td></td>
                </tr>
            `
        })
        $('#table-review .table tbody').html(productHtml);
        calculateDiscountedPrices();
        $('#table-review').show()
    }

    $('#send-request').on('click', function () {
        if ($('#table-review tbody tr').length > 0) {
            const title = $('#inputTitle').val();
            const reducedPrice = parseInt($('#inputReducedPrice').val());
            const reduceType = $('#discountType').val();
            const startDate = $('#inputTime').data('daterangepicker').startDate.format('YYYY-MM-DDTHH:mm:ss');
            const endDate = $('#inputTime').data('daterangepicker').endDate.format('YYYY-MM-DDTHH:mm:ss');

            const selectedProductIds = [];
            $('#table-review tbody tr').each(function () {
                const productId = $(this).data('product-id');
                selectedProductIds.push(productId);
            });

            const requestData = {
                title: title,
                reducedPrice: reducedPrice,
                reduceType: reduceType,
                startDate: startDate,
                endDate: endDate,
                productId: selectedProductIds
            };
            console.log(requestData)

            fetch('/campaign/' + storeId + '/create', {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(requestData)
            })
                .then(response => {
                    if (response.ok) {
                        toastr.success("Tạo chiến dịch thành công");
                        setTimeout(function () {
                            window.location.reload();
                        }, 1000)
                    } else {
                        return response.json();
                    }
                })
                .then(response => {
                    if (response) {
                        let productNameText= "";
                        let productNameList = response.productNames;
                        productNameList.forEach(productName => {
                            productNameText += productName + ", "
                        })
                        let p = $('<p>').text(productNameText.slice(0, -2));
                        $('.modal-body').append(p);
                        let action = $('<p style="font-weight: 500">').text("Hãy chọn sản phẩm khác hoặc chỉnh sửa chiến dịch đã có sẵn.")
                        $('.modal-body').append(action);
                        $('#product-error').modal('show');

                    }

                })
                .catch(error => {
                    console.log("Error:", error);
                });
        } else {
            toastr.error("Bạn cần chọn sản phẩm cho chương trình khuyến mãi.")
        }

    })
});





$(document).ready(function() {
    $(".all-product-container #checkAll").click(function() {
        let isChecked = $(this).prop("checked");
        $("tbody input[type='checkbox']").prop("checked", isChecked);
    });
});