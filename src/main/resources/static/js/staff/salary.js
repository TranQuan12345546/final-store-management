let selectedStartDate;
let selectedEndDate;
$(function() {
    let start = moment().startOf('month');
    let end = moment();

    function getCustomDateText(start, end) {
        let customDateText = start.format('DD/MM/YYYY') + ' - ' + end.format('DD/MM/YYYY');
        let today = moment().startOf('day');

        if (end.diff(start, 'days') === 6) {
            if (end.isSame(today, 'day')) {
                customDateText = '7 Ngày Trước';
            }
        }
        else if (start.isSame(moment().startOf('month')) && end.isSame(moment())) {
            customDateText = 'Tháng Này';

        }
        else if (start.isSame(moment().subtract(1, 'month').startOf('month')) && end.isSame(moment().subtract(1, 'month').endOf('month'))) {
            customDateText = 'Tháng Trước';
        }
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
        } else {
            $('#report-range span').html(getCustomDateText(start, end));
        }

        selectedStartDate = start.format('YYYY-MM-DD');
        selectedEndDate = end.format('YYYY-MM-DD');
    }

    $('#report-range').daterangepicker({
        startDate: start,
        endDate: end,
        maxDate: moment(),
        ranges: {
            'Hôm nay': [moment(), moment()],
            'Hôm qua': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            '7 Ngày Trước': [moment().subtract(6, 'days'), moment()],
            'Tháng Này': [moment().startOf('month'), moment()],
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

    $('#report-range').on('apply.daterangepicker', async function(ev, picker) {
        const startDate = picker.startDate.startOf('day').format('YYYY-MM-DD');
        const endDate = picker.endDate.endOf('day').format('YYYY-MM-DD');
        selectedStartDate = startDate;
        selectedEndDate = endDate;
        await sendRequestToServer(startDate, endDate);
    });
});

async function sendRequestToServer(startDate, endDate) {
    await fetch('/salary/' + storeId + '?startDate=' + startDate + '&&endDate=' + endDate, {
        method: 'GET'
    })
        .then(response => {
            if (response.ok) {
                return response.json()
            }
        })
        .then(response => {
            console.log(response)
            renderTableSalary(response);
        })
}

$(document).ready(function () {
    $('#change-hourly-salary').on('click', function (){
        console.log("hi")
        $(this).prev('input').attr('disabled', false);
    })

    $('#hourly-salary').on('blur', async function () {
        await updateHourlySalary(this);
    });

    $('#hourly-salary').on('keypress', async function (event) {
        if (event.which === 13) {
            await updateHourlySalary(this);
        }
    });

    async function updateHourlySalary(inputElement) {
        $(inputElement).attr('disabled', true);
        let salaryPerHour = $(inputElement).val();
        await fetch('/salary/' + storeId + '/change-hourly-salary?salaryPerHour=' + salaryPerHour, {
            method: 'PUT'
        })
            .then(async response => {
                if (response.ok) {
                    toastr.success("Thay đổi mức lương theo giờ thành công");
                    await sendRequestToServer(selectedStartDate, selectedEndDate);
                }
            });
    }


})

function renderTableSalary(salaryList) {
    const tbody = $("#table-body");

    tbody.empty();

    // Render dữ liệu mới
    salaryList.forEach(staffSalary => {
        const tr = $("<tr>");

        $("<td>").text(staffSalary.staffName).appendTo(tr);
        $("<td>").text(moment(staffSalary.startWork).format('DD/MM/YYYY')).appendTo(tr);
        $("<td>").text(staffSalary.totalWorkShift).appendTo(tr);
        $("<td>").text(staffSalary.totalWorkTimeByMonth).appendTo(tr);
        $("<td>").text(staffSalary.termSalary).appendTo(tr);
        $("<td>").append($("<input>").attr("type", "text").attr("name", "bonusInput")).appendTo(tr);
        $("<td>").text(staffSalary.termSalary).appendTo(tr);

        tr.appendTo(tbody);
    });
}