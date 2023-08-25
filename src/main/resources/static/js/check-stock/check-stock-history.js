let selectedStartDate;
let selectedEndDate;


$(function() {
    let start = moment();
    let end = moment();

    function getCustomDateText(start, end) {
        let customDateText = start.format('D MMMM, YYYY') + ' - ' + end.format('D MMMM, YYYY');
        let today = moment().startOf('day');

        if (end.diff(start, 'days') === 6) {
            if (end.isSame(today, 'day')) {
                customDateText = '7 Ngày Trước';

            }
        }
        else if (start.isSame(moment().startOf('month')) && end.isSame(moment().endOf('month'))) {
            customDateText = 'Tháng Này';

        }
        else if (start.isSame(moment().subtract(1, 'month').startOf('month')) && end.isSame(moment().subtract(1, 'month').endOf('month'))) {
            customDateText = 'Tháng Trước';
        } else {
            customDateText = 'Tuỳ chọn';
        }
        changeDisplayDay(customDateText);
        selectedStartDate = start.format('YYYY-MM-DD');
        selectedEndDate = end.format('YYYY-MM-DD');

        return customDateText;
    }

    function cb(start, end) {
        if (start.isSame(end, 'day')) {
            let dateText = start.format('D MMMM, YYYY');
            if (start.isSame(moment(), 'day')) {
                dateText = 'Hôm nay';
            }
            else if (start.isSame(moment().subtract(1, 'days'), 'day')) {
                dateText = 'Hôm qua';
            }
            $('#report-range span').html(dateText);
            changeDisplayDay(dateText);
        } else {
            $('#report-range span').html(getCustomDateText(start, end));
        }

        selectedStartDate = start.format('YYYY-MM-DD');
        selectedEndDate = end.format('YYYY-MM-DD');
    }

    $('#report-range').daterangepicker({
        startDate: start,
        endDate: end,
        ranges: {
            'Hôm nay': [moment(), moment()],
            'Hôm qua': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            '7 Ngày Trước': [moment().subtract(6, 'days'), moment()],
            'Tháng Này': [moment().startOf('month'), moment().endOf('month')],
            'Tháng Trước': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        },
        locale: {
            format: 'DD/MM/YYYY',
            separator: ' - ',
            applyLabel: 'Áp dụng',
            cancelLabel: 'Hủy',
            customRangeLabel: 'Tuỳ chọn',
        },
    }, cb);

    cb(start, end);

    $('#report-range').on('apply.daterangepicker', function(ev, picker) {
        const startDate = picker.startDate.startOf('day').format('YYYY-MM-DD HH:mm:ss');
        const endDate = picker.endDate.endOf('day').format('YYYY-MM-DD HH:mm:ss');
        sendRequestToServer(startDate, endDate);
    });
});

let inventoryListRender = [];

const inventoryContainer = document.querySelector(".content")
console.log(inventoryContainer)
function sendRequestToServer(startDate, endDate) {
    fetch('/inventory/' + storeId + '?startDate=' +  encodeURIComponent(startDate) + '&endDate=' +  encodeURIComponent(endDate), {
        method: 'GET'
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
        })
        .then(response => {
            console.log(response);
            inventoryListRender = response;
            renderPagination(inventoryListRender);
        })
    function renderInventories(inventoryList) {
// Xóa hết nd đang có trước khi render
        inventoryContainer.innerHTML = "";

// Tạo nd
        let inventoryHtml = "";

        let number = 1;

        function getRandomNumber(number) {
            let numberNext = Math.floor(Math.random() * 4) + 1;
            while (numberNext == number) {
                numberNext = Math.floor(Math.random() * 4) + 1;
            }
            return numberNext;
        }

        inventoryList.forEach(inventory => {
            number = getRandomNumber(number);
            inventoryHtml += `
        <div class="history-container" data-inventory-id="${inventory.id}">
                <div class="card-box bg-${number == 1 ? 'blue' : number == 2 ? 'green' : number == 3 ? 'orange' : 'red'}">
                    <div class="inner">
                        <h4><span>${inventory.inventoryName}</span></h4>
                        <p>Ngày thực hiện: <span>${formatDate(inventory.createdAt)}</span> </p>
                        <p>Số lượng kiểm kê: <span>${inventory.inventoryProductQuantity.toLocaleString('vi-VN')}</span>sp</p>
                        <p>Người thực hiện: <span>${inventory.createdBy}</span></p>
                    </div>
                    <button href="#" class="history-detail-btn">Xem chi tiết</button>
                </div>
            </div>
      `
        })

// Insert nd
        inventoryContainer.innerHTML = inventoryHtml;
    }

// Hiển thị phần phân trang
    function renderPagination(inventoryList) {
        $('#pagination').pagination({
            dataSource: inventoryList,
            pageSize: 8,
            callback: function (data, pagination) {
                renderInventories(data);
            }
        })
    }
}




function changeDisplayDay(text) {
    $('.header h3 span').text(text);
    if (text  == "Tuỳ chọn") {
        $('.no-result span').text('');
    } else {
        $('.no-result span').text(text.toLocaleLowerCase());
    }

}


let inventoryProductList = [];
$(document).on('click', '.history-detail-btn', function () {
    const inventoryId = $(this).closest('.history-container').data('inventory-id');
    console.log(inventoryId)
    fetch("/inventory/" + storeId + "/" + inventoryId + "/inventory-product", {
        method: 'GET'
    }).then(response => {
        if (response.ok) {
            return response.json();
        }
    }).then(res => {
        inventoryProductList = res;
        console.log(inventoryProductList)

        renderPagination(inventoryProductList);

        $('#table-result').modal('show')
    })


});

function renderTableResult(inventoryProductList) {
    let tableBody = $("#table-result tbody");
// Xóa hết nd đang có trước khi render
    tableBody.innerHTML = "";

// Tạo nd
    let productHtml = "";
    productHtml += inventoryProductList.map(inventoryProduct => `
        <tr>
            <td>${inventoryProduct.codeNumber}</td>
            <td>${inventoryProduct.productName}</td>
            <td>${inventoryProduct.groupName}</td>
            <td>${inventoryProduct.productPrice}</td>
            <td>${inventoryProduct.quantityBeforeCheck}</td>
            <td>${inventoryProduct.quantityAfterCheck}</td>
        </tr>
    `)

    tableBody.html(productHtml);
}
function renderPagination(inventoryProductList) {
    $('#pagination2').pagination({
        dataSource: inventoryProductList,
        pageSize: 15,
        showSizeChanger: true,
        callback: function (data, pagination) {
            renderTableResult(data);
        }
    })
}

$('.dropdown-menu li').on('click', function() {
    const dropdownButton = document.getElementById('dropdown-menu-button');
    console.log(dropdownButton)
    let selectedGroupProduct = $(this).find('span').text();
    console.log(selectedGroupProduct)
    dropdownButton.textContent = selectedGroupProduct + " ";
    let productListByGroup = [];
    if (selectedGroupProduct == "Chọn tất cả") {
        productListByGroup = inventoryProductList;
        renderPagination(productListByGroup);
        toastr.success("Hiển thị theo tất cả nhóm hàng")
    } else {
        inventoryProductList.forEach(product => {
            if (product.groupName == selectedGroupProduct) {
                productListByGroup.push(product);
            }
        })
        renderPagination(productListByGroup);
        console.log(productListByGroup)
        toastr.success("Hiển thị theo nhóm hàng: " + selectedGroupProduct)
    }
});

// search bảng kết quả kiểm kể
$(document).ready(function() {
    $('#searchInput').on('input', function() {
        $('.table-responsive tbody tr').each(function () {
            $(this).hide();
        })
        let searchText = $(this).val().toLowerCase().trim();
        if (searchText === "") {
            $('.table-responsive tbody tr').each(function () {
                $(this).find('td span').css('background-color', 'transparent')
                $(this).show();

            })
        }
        if (searchText.length >= 1) {
            $('.table-responsive tbody tr').each(function () {
                let codeNumber = $(this).find("td:nth-child(1)").text();
                let productName = $(this).find("td:nth-child(2)").text();
                // Kiểm tra xem tên hoặc mã sản phẩm có trùng với searchText hay không
                if (productName.toLowerCase().includes(searchText)) {
                    let highlightedName = highlightSearchText(productName, searchText);
                    $(this).find("td:nth-child(2)").html(highlightedName);
                    $(this).show();
                }
                if (codeNumber.toLowerCase().includes(searchText)) {
                    let highlightedName = highlightSearchText(codeNumber, searchText);
                    $(this).find("td:nth-child(1)").html(highlightedName);
                    $(this).show();
                }
            })
        }


    });
});

function highlightSearchText(text, searchText) {
    const regex = new RegExp(searchText, 'gi');
    return text.replace(regex, '<span class="high-light">$&</span>');
}





