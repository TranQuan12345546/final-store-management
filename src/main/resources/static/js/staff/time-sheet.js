
console.log(shiftAssignmentList)

const datePicker = document.getElementById("datePicker");
const currentDate = new Date();
const optionList = staffList.map(staff => {
    return `<option value="${staff.id}">${staff.fullName}</option>`
}).join('');

function getMonday(d) {
    d = new Date(d);
    let day = d.getDay(),
        diff = d.getDate() - day + (day == 0 ? -6:1);
    return new Date(d.setDate(diff));
}

$(document).ready(function () {
    const mondayDate =  getMonday(new Date())

    for (let i = 1; i <= 7; i++) {
        const dayCell = $(`thead th:nth-child(${i + 1}) div`);
        // Đặt nội dung là ngày tương ứng
        dayCell.text(mondayDate.getDate() + "/" + (mondayDate.getMonth() + 1));

        // Tăng ngày cho lần lặp kế tiếp
        mondayDate.setDate(mondayDate.getDate() + 1);
    }
});


const firstTdInRow = `
                <td style="width: 200px">
                        <input class="input-name-work-shift text-uppercase fw-bolder text-center" disabled>
                        <div class="selected-time"></div>
                        <div class="choose-time">
                            <input type="text" class="timepicker start-time" placeholder="Select start time" disabled>
                            <span>-</span>
                            <input type="text" class="timepicker end-time" placeholder="Select end time" disabled>
                        </div>
                        <button class="edit-time"><i class="ti ti-pencil"></i></button>
                        <button class="check-time"><i class="ti ti-check"></i></button>
                    </td>
            `;

flatpickr(datePicker, {
    dateFormat: "d/m/Y",
    minDate: "today",
    defaultDate: currentDate,
    locale: {
        firstDayOfWeek: 1
    },
    onChange: async function(selectedDates) {
        // Lấy ngày đầu tiên trong danh sách ngày đã chọn
        const selectedDate = selectedDates[0];
        const selectedDateFormat = moment(selectedDate).format("YYYY-MM-DD");

        // Lấy dữ liệu ngày trong tuần
        await fetch("/shift-assignment/get-day-of-week?daySelected=" + selectedDateFormat, {
            method:'GET'
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
            })
            .then(response => {
                dayOfWeek = response;
            })

       // Lấy dữ liệu phân ca ứng với ngày được chọn
        await fetch('/shift-assignment/' + storeId + '/get-shift-assignment?daySelected=' + selectedDateFormat, {
            method: 'GET'
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
            })
            .then(response => {
                shiftAssignmentList = response;
            })
        console.log(shiftAssignmentList)

        // Lấy dữ liệu ca làm việc ứng với ngày được chọn
        let workShiftListByDateSelected = [];
        await fetch('/shift-assignment/' + storeId + '/get-work-shift?daySelected=' + selectedDateFormat, {
            method : 'GET'
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
            })
            .then(response => {
                workShiftListByDateSelected = response;
            })

        // tạo bảng mới chứa nội dung
        $('table').remove()
        let newTable = `
            <table class="table table-bordered text-center">
                <thead>
                <tr class="bg-light-gray">
                    <th class="text-uppercase">Thời gian
                    </th>
                    <th class="text-uppercase"  data-index="0">Thứ hai
                        <div>12/8</div>
                    </th>
                    <th class="text-uppercase" data-index="1">Thứ ba
                        <div>12/8</div></th>
                    <th class="text-uppercase" data-index="2">Thứ tư
                        <div>12/8</div></th>
                    <th class="text-uppercase" data-index="3">Thứ năm
                        <div>12/8</div></th>
                    <th class="text-uppercase" data-index="4">Thứ sáu
                        <div>12/8</div></th>
                    <th class="text-uppercase" data-index="5">Thứ bảy
                        <div>12/8</div></th>
                    <th class="text-uppercase" data-index="6">Chủ nhật
                        <div>12/8</div></th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                    <tr class="bottom-hover-trigger"></tr>
                </tbody>
            </table>
        `
        $('.table-container').prepend(newTable);

        // Tính ngày thứ hai trong tuần tương ứng với ngày đã chọn
        const mondayDate = getMonday(selectedDate);
        // Hiển thị các ngày trong tuần
        for (let i = 1; i <= 7; i++) {
            const theadColumn = $(`thead th:nth-child(${i + 1})`);
            theadColumn.attr('data-work-day', dayOfWeek[i-1]);
            const divTheadColumn = $(`thead th:nth-child(${i + 1}) div`);
            // Đặt nội dung là ngày tương ứng
            divTheadColumn.text(mondayDate.getDate() + "/" + (mondayDate.getMonth() + 1));
            // Tăng ngày cho lần lặp kế tiếp
            mondayDate.setDate(mondayDate.getDate() + 1);
        }

        function appendTbody (tableRow) {
            $('tbody').prepend(tableRow);
            $("tbody .bottom-hover-trigger").hover(
                function () {
                    $(".table-container").find(".add-row-button").show();
                },
                function () {
                    $(".table-container").find(".add-row-button").hide();
                }
            );
            handleEditTimeHover()
            setStaffColor();
            setDefaultTime();
        }

        if (shiftAssignmentList.length === 0 && workShiftListByDateSelected.length === 0) {
            const createWorkShiftRow = (workShift, index, array) => {
                const isLastElement = index === array.length - 1;
                return `
                <tr class="${isLastElement ? 'last-row' : ''}" data-work-shift-id="${workShift.id}" data-order-shift="${workShift.orderShift}">
                   <td style="width: 200px">
                        <input class="input-name-work-shift text-uppercase fw-bolder text-center" disabled value="${workShift.name}">
                        <div class="selected-time">${workShift.startShift.slice(0, 5) + '-' + workShift.endShift.slice(0, 5)}</div>
                        <div class="choose-time" style="display: none">
                            <input type="text" class="timepicker start-time" placeholder="Select start time" disabled>
                            <span>-</span>
                            <input type="text" class="timepicker end-time" placeholder="Select end time" disabled>
                        </div>
                        <button class="edit-time"><i class="ti ti-pencil"></i></button>
                        <button class="check-time"><i class="ti ti-check"></i></button>
                    </td>
                    ${Array(7).fill(`
                        <td>
                            <div class="">
                                <select class="custom-select">
                                    <option value="" selected disabled>--Chọn nhân viên--</option>
                                    ${optionList}
                                </select>
                            </div>
                        </td>`).join('')}
                    <td><button class="shift-delete"><i class="ti ti-trash"></i></button></td>
                </tr>
            `
            };
            const newTableRow = workShiftList.map(createWorkShiftRow).join('');
            appendTbody(newTableRow);
        } else {
            workShiftList = workShiftListByDateSelected;
            const createNewTbodyWithData = (workShift, index, array) => {
                const isLastElement = index === array.length - 1;


                // Tạo các ô <td> cho các ngày trong tuần
                const dayColumns = dayOfWeek.map((day, dayIndex) => {
                    const shiftAssignmentForDay = shiftAssignmentList.find(sa => sa.workShiftId === workShift.id && sa.workDay === day);
                    const selectedStaffId = shiftAssignmentForDay ? shiftAssignmentForDay.staffIds[0] : '';

                    const optionsForDay = staffList.map(staff => {
                        const isSelected = staff.id === parseInt(selectedStaffId);
                        return `<option value="${staff.id}" ${isSelected ? "selected" : ""}>${staff.fullName}</option>`;
                    }).join('');

                    return `
                        <td data-shift-assignment-id="${shiftAssignmentForDay ? shiftAssignmentForDay.id : ''}">
                            <div class="">
                                <select class="custom-select">
                                    <option value="" selected disabled>--Chọn nhân viên--</option>
                                    ${optionsForDay}
                                </select>
                            </div>
                        </td>
                    `;
                }).join('');

                return `
                    <tr class="${isLastElement ? 'last-row' : ''}" data-work-shift-id="${workShift.id}" data-order-shift="${workShift.orderShift}">
                        <td style="width: 200px">
                            <input class="input-name-work-shift text-uppercase fw-bolder text-center" disabled value="${workShift.name}">
                            <div class="selected-time">${workShift.startShift.slice(0, 5) + '-' + workShift.endShift.slice(0, 5)}</div>
                            <div class="choose-time" style="display: none">
                                <input type="text" class="timepicker start-time" placeholder="Select start time" disabled>
                                <span>-</span>
                                <input type="text" class="timepicker end-time" placeholder="Select end time" disabled>
                            </div>
                            <button class="edit-time"><i class="ti ti-pencil"></i></button>
                            <button class="check-time"><i class="ti ti-check"></i></button>
                        </td>
                        ${dayColumns}
                        <td><button class="shift-delete"><i class="ti ti-trash"></i></button></td>
                    </tr>
                    `;
            };

            const newTableRow = workShiftListByDateSelected.map(createNewTbodyWithData).join('');
            appendTbody(newTableRow);
        }
    }
});






$(document).ready(function () {
    // Xử lý hover và thêm class "last-row" cho dòng cuối cùng
    $("tbody .bottom-hover-trigger").hover(
        function () {
            $(".table-container").find(".add-row-button").show();
        },
        function () {
            $(".table-container").find(".add-row-button").hide();
        }
    );

    $(".add-row-button").hover(
        function () {
            $("tbody .bottom-hover-trigger").addClass('hovered')
            $(this).show();
        },
        function () {
            $("tbody .bottom-hover-trigger").removeClass('hovered')
            $(this).hide();
        }
    );

    // Thêm dòng mới khi ấn vào nút "dấu cộng"
    $(".add-row-button").click(function () {
        $("tbody .bottom-hover-trigger").remove()
        let orderShift = $(".last-row").closest('tr').data('order-shift');
        $(".last-row").off().removeClass("last-row hovered");
        // Tạo dòng mới (chứa các cột cần thiết)


        const newRow = `
            <tr class="last-row" style="height: 85px" data-order-shift="${orderShift + 1}">
                ${firstTdInRow}
                ${Array(7).fill(`
                        <td>
                            <div class="">
                                <select class="custom-select">
                                    <option value="" selected disabled>--Chọn nhân viên--</option>
                                    ${optionList}
                                </select>
                            </div>
                        </td>`).join('')}
                <td><button class="shift-delete"><i class="ti ti-trash"></i></button></td>
            </tr>
            <tr class="bottom-hover-trigger"></tr>
        `;
        $(".table-container .table tbody").append(newRow);
        $("tbody .bottom-hover-trigger").hover(
            function () {
                $(".table-container").find(".add-row-button").show();
            },
            function () {
                $(".table-container").find(".add-row-button").hide();
            }
        );
        handleEditTimeHover()
        setStaffColor();
        setDefaultTime();

    });
});

// Biến flag để theo dõi trạng thái nút .check
let checkButtonVisible = false;
// xử lý button chỉnh sửa giờ làm việc
function handleEditTimeHover() {
    $("tbody td:first-child").hover(
        function () {
            if (!checkButtonVisible) {
                $(this).find(".edit-time").css('display', 'block');
            }
        },
        function () {
            $(this).find(".edit-time").hide();
        }
    );
}

$(document).on("click", ".edit-time", function () {
    checkButtonVisible = true;
    const inputNameElement = $(this).closest('td').find('.input-name-work-shift');
    inputNameElement.attr('disabled', false);
    inputNameElement.focus();
    const valueLength = inputNameElement.val().length;
    inputNameElement.prop('selectionStart', valueLength);
    inputNameElement.prop('selectionEnd', valueLength);
    inputNameElement.show();
    $(this).closest('td').find('.choose-time').css('display', 'block');
    $(this).closest('td').find('.choose-time .timepicker').attr('disabled', false);
    $(this).closest('td').find('.selected-time').css('display', 'none');
    $(this).off().hide();
    $(this).closest('td').find('.check-time').css('display', 'block');
});


$(document).on("click", ".check-time", async function () {
    checkButtonVisible = false
    const workShiftRow = $(this).closest('tr');
    const startTime = $(this).closest('td').find('.timepicker:first-child').val();
    const endTime = $(this).closest('td').find('.timepicker:last-child').val();
    if(startTime === '') {
        toastr.warning("Bạn cần chọn thời gian bắt đầu ca làm việc");
    } else if (endTime === '') {
        toastr.warning("Bạn cần chọn thời gian kết thúc ca làm việc");
    }
    else {
        // Lưu thời gian đã chọn vào biến
        let selectedStartTime = startTime;
        let selectedEndTime = endTime;

        // Cập nhật giá trị vào thẻ div hiển thị thời gian đã chọn
        $(this).closest('td').find(".selected-time").text(selectedStartTime + " - " + selectedEndTime);
        $(this).closest('td').find('.selected-time').css('display', 'block');
        $(this).closest('td').find('.choose-time').css('display', 'none');
        $(this).css('display', 'none');
        const workShiftId = $(this).closest('tr').data('work-shift-id');
        if (workShiftId == null) {
            const startShiftTime = moment(startTime, "HH:mm").format("HH:mm:ss");

            const endShiftTime = moment(endTime, "HH:mm").format("HH:mm:ss");
            const name = $(this).closest('td').find('.input-name-work-shift').val();
            const orderShift = $(this).closest('tr').data('order-shift');

            let createWorkShiftRequest = {
                name: name,
                startShift: startShiftTime,
                endShift: endShiftTime,
                orderShift: orderShift
            }
            console.log(createWorkShiftRequest)

            // tạo ca làm việc
            await fetch('/work-shift/' + storeId + '/create-shift', {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(createWorkShiftRequest)
            })
                .then(response => {
                    if (response.ok) {
                        toastr.success("Thêm ca làm việc thành công");
                        return response.json();
                    }
                    else {
                        toastr.error("Thêm ca làm việc thất bại")
                    }
                })
                .then(response => {
                    console.log(response)
                    workShiftRow.attr('data-work-shift-id', response.id);
                })

            //tạo phân ca
            let createShiftAssignmentRequestList = []
            console.log(workShiftRow.data('work-shift-id'))

            for (let i = 2; i <= 8; i++) {
                const workShiftId = workShiftRow.data('work-shift-id');
                const orderShift = workShiftRow.data('order-shift');
                const workDay = $(`thead th:nth-child(${i})`).data('work-day');
                const staffIds = [''];

                const createShiftAssignmentRequest = {
                    workShiftId: workShiftId,
                    workDay: workDay,
                    orderShift: orderShift,
                    staffIds: staffIds
                }

                createShiftAssignmentRequestList.push(createShiftAssignmentRequest)
            }
            console.log(createShiftAssignmentRequestList)

            fetch('/shift-assignment/' + storeId + '/create', {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(createShiftAssignmentRequestList)
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        toastr.error("Lưu bảng thất bại")
                    }
                })
                .then(response => {
                    console.log(response)
                })

        } else {
            console.log("cập nhât")
        }
    }

});

$(document).on("click", ".shift-delete", function () {
    for (let i = 2; i <= 8; i++) {
        let shiftAssignmentId = $(this).closest('tr').find(`td:nth-child(${i})`).attr("data-shift-assignment-id");
        if (shiftAssignmentId != null) {
            toastr.error("Bạn không thể xoá hàng đã được phân ca làm việc")
            return;
        }
        let hoverAddRow = $('<td class="bottom-hover-trigger"></td>')
        $(this).closest('tr').prev().append(hoverAddRow)
        $(this).closest('tr').prev().addClass('last-row')
        $(this).closest('tr').remove()
    }

})


$(document).ready(function () {
    handleEditTimeHover();
    setDefaultTime()
})

function setDefaultTime() {
    $("table tbody tr:not(:last)").each(function () {
        // Tìm thẻ div.selected-time trong dòng hiện tại
        const selectedTimeDiv = $(this).find(".selected-time").text();
        console.log(selectedTimeDiv)
        // Lấy giá trị thời gian mặc định từ thẻ div.selected-time
        if (selectedTimeDiv != '') {
            const defaultTimeRange = selectedTimeDiv.trim().split("-");
            const defaultStartTime = defaultTimeRange[0];
            const defaultEndTime = defaultTimeRange[1];

            // Tìm thẻ input start-time và end-time trong dòng hiện tại
            const startTimeInput = $(this).find(".start-time");
            const endTimeInput = $(this).find(".end-time");

            // Áp dụng flatpickr cho từng input start-time và end-time trong dòng hiện tại
            flatpickr(startTimeInput[0], {
                enableTime: true,
                noCalendar: true,
                dateFormat: "H:i",
                defaultDate: defaultStartTime
            });

            flatpickr(endTimeInput[0], {
                enableTime: true,
                noCalendar: true,
                dateFormat: "H:i",
                defaultDate: defaultEndTime
            });
        } else {
            const startTimeInput = $(this).find(".start-time");
            const endTimeInput = $(this).find(".end-time");

            // Áp dụng flatpickr cho từng input start-time và end-time trong dòng hiện tại
            flatpickr(startTimeInput[0], {
                enableTime: true,
                noCalendar: true,
                dateFormat: "H:i"
            });

            flatpickr(endTimeInput[0], {
                enableTime: true,
                noCalendar: true,
                dateFormat: "H:i"
            });
        }
    });
}

function setStaffColor() {
    const colorCodes = [
        "#1295fc",
        "#5bbd2a",
        "#f0d001",
        "#ff48a4",
        "#9d60ff",
        "#ff5722",
        "#02c2c7",
        "#e95601"
    ];

    function assignRandomColorToStaff(staffList) {
        staffList.forEach(function (staff, index) {
            const randomColorIndex = index % colorCodes.length;
            const randomColorCode = colorCodes[randomColorIndex];
            staff.color = randomColorCode;
        });
    }

    assignRandomColorToStaff(staffList);

    $("select.custom-select").each(function () {
        const staffId = parseInt($(this).val());
        const staff = staffList.find(function (item) {
            return item.id === staffId;
        });
        if (staff) {
            $(this).css("background-color", staff.color);
        }
    });

    // Bắt sự kiện thay đổi lựa chọn nhân viên
    $("select.custom-select").change(function () {
        const staffId = parseInt($(this).val());
        const staff = staffList.find(function (item) {
            return item.id === staffId;
        });
        if (staff) {
            $(this).css("background-color", staff.color);
        }
    });
}

$(document).ready(function () {
    setStaffColor();
});

// xử lý button Lưu ca làm việc
$(document).ready(function () {
    $('#btn-save-session').on('click', function () {
        const shiftAssignmentId = $('tbody tr:nth-child(1) td:nth-child(2)').data('shift-assignment-id');
        const workShiftNumber = $('tbody tr').length;
        if (shiftAssignmentId == null) {
            let createShiftAssignmentRequestList = [];
            for (let i = 1; i <= workShiftNumber - 1; i++) {
                for (let j = 1; j <= 7; j++) {
                    const staffId = $(`tbody tr:nth-child(${i}) td:nth-child(${j+1}) .custom-select`).val();
                    const workShiftId = $(`tbody tr:nth-child(${i})`).data('work-shift-id');
                    const orderShift = $(`tbody tr:nth-child(${i})`).data('order-shift');
                    const workDay = $(`thead th:nth-child(${j+1})`).data('work-day');
                    const staffIds = [staffId];

                    const createShiftAssignmentRequest = {
                        workShiftId: workShiftId,
                        workDay: workDay,
                        staffIds: staffIds,
                        orderShift: orderShift
                    }
                    createShiftAssignmentRequestList.push(createShiftAssignmentRequest)
                }
            }
            console.log(createShiftAssignmentRequestList)
            if (createShiftAssignmentRequestList.length !== 0) {
                fetch('/shift-assignment/' + storeId + '/create', {
                    method: 'POST',
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(createShiftAssignmentRequestList)
                })
                    .then(response => {
                        if (response.ok) {
                            toastr.success("Lưu bảng thành công")
                        } else {
                            toastr.error("Lưu bảng thất bại")
                        }
                    })
            } else {
                toastr.error("Bạn chưa thêm thông tin cho bảng")
            }
        } else {
            let updateShiftAssignmentRequestList = [];
            const oldStaffIdList = getOldStaffIdList(shiftAssignmentList);
            for (let i = 1; i <= workShiftList.length; i++) {
                for (let j = 1; j <= 7; j++) {
                    let newStaffId = $(`tbody tr:nth-child(${i}) td:nth-child(${j+1}) .custom-select`).val();
                    let staffIds;
                    if (newStaffId == null) {
                        staffIds = [];
                    } else {
                        staffIds = [parseInt(newStaffId)]
                    }
                    const shiftAssignmentId = $(`tbody tr:nth-child(${i}) td:nth-child(${j+1}) .custom-select`).closest('td').data('shift-assignment-id');
                    if (!arraysEqual(staffIds, oldStaffIdList[(j-1) + (i-1)*7])) {
                        const updateShiftAssignmentRequest = {
                            shiftAssignmentId: shiftAssignmentId,
                            staffIds: staffIds
                        }

                        updateShiftAssignmentRequestList.push(updateShiftAssignmentRequest)

                    }
                }
            }
            console.log(updateShiftAssignmentRequestList)
            fetch('/shift-assignment/update', {
                method: 'PUT',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(updateShiftAssignmentRequestList)
            })
                .then(response => {
                    if (response.ok) {
                       toastr.success("Update thành công")
                    }
                })
        }
    })

    //Kiểm tra 2 mảng có bằng nhau không
    function arraysEqual(arr1, arr2) {
        if (arr1.length !== arr2.length) {
            return false;
        }

        return arr1.every((value, index) => value === arr2[index]);
    }


    function getOldStaffIdList(shiftAssignmentList) {
        let oldStaffIdList = [];
        shiftAssignmentList.forEach(shiftAssignment => {
            oldStaffIdList.push(shiftAssignment.staffIds)
        })
        return oldStaffIdList
    }

})

$(document).ready(function () {
    function updateShiftOnTime() {
        const now = new Date();
        const dayMapping = [8, 2, 3, 4, 5, 6, 7];
        let day = dayMapping[now.getDay()];


        $(".work-shift").each(function() {
            const startShift = $(this).data("start-shift");
            const endShift = $(this).data("end-shift");

            const [startHour, startMinute] = startShift.split(":");
            const [endHour, endMinute] = endShift.split(":");

            const startTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), startHour, startMinute);
            const endTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), endHour, endMinute);

            if (now > startTime && now < endTime) {
                const shiftChecked = $(this).find(`td:nth-child(${day})`);
                if (shiftChecked.data('checked') === undefined) {
                    $('td').css('background-color', 'transparent');
                    $('td:has(button)').find('button').remove();
                    shiftChecked.data('checked', 'checked')
                    shiftChecked.css("background-color", "#b8e6b6");
                    let checkInTime = shiftChecked.data('check-in-time');
                    let checkOutTime = shiftChecked.data('check-out-time');
                    if (ownerName !== userName) {
                        if (checkInTime == null ) {
                            const button =  `<button id="check-in" >check-in</button>`;
                            shiftChecked.append(button)
                        } else if (checkInTime != null && checkOutTime == null) {
                            let checkoutButton = `<button id="check-out" >check-out</button>`;
                            shiftChecked.append(checkoutButton);
                        }
                    }
                }
            }
        });
    }

    function calculateTimeToNextMinute() {
        const now = new Date();
        const secondsToNextMinute = 60 - now.getSeconds();
        return secondsToNextMinute * 1000;
    }

    function updateShiftColorsAtNextMinute() {
        const timeToNextMinute = calculateTimeToNextMinute();
        setTimeout(function() {
            updateShiftOnTime();
            // Sau khi gọi hàm updateShiftColors, tiếp tục lập lịch cho phút tiếp theo
            updateShiftColorsAtNextMinute();
        }, timeToNextMinute);
    }
    updateShiftOnTime();
    updateShiftColorsAtNextMinute();
});