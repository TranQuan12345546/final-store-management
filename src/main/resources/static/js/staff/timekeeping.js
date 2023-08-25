
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


console.log(shiftAssignmentList)
//highlight ca làm việc đang thực hiện
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

$(document).ready(function () {

    let canClick = true;
    $('#check-in').on('click', async function () {
        let td = $(this).closest('td');
      if (userName == td.find('.user-shift').text()) {
          if (!canClick) {
              $(this).attr('disabled', true);
              return;
          }

          canClick = false;
          $(this).attr('disabled', false);
          await fetch('/staff/check-in/' + userId,{
              method: 'POST'
          })
              .then( response => {
                  if (response.ok) {
                      toastr.success("Gửi email thành công")
                  } else {
                      console.log(response);
                  }
              })

          $('#checkInModal').modal('show');

          setTimeout(() => {
              canClick = true;
          }, 60000);
      } else {
          toastr.error("Bạn không được phân công cho ca làm việc này")
      }
    })

})

$(document).ready(function () {
    $('#confirm-check-in').on('click', async function () {
        let inputToken = $('#input-check-in-token').val();
        await fetch('/staff/check-in-confirm/' + inputToken, {
            method: 'GET'
        })
            .then(async response => {
                if (response.ok) {
                    let workShift = $('td:has(button)');
                    let shiftAssignmentId = workShift.data('shift-assignment-id');
                    console.log(shiftAssignmentId)
                    let timeCheckIn = new Date().toLocaleTimeString("vi-VN", { hour12: false });
                    console.log(timeCheckIn)
                    await fetch('/shift-assignment/' + shiftAssignmentId  + '/work-shift-check-in?checkInTime=' + timeCheckIn, {
                        method: 'PUT'
                    })
                        .then(response => {
                            if (!response.ok){
                                console.log(response);
                            }
                        })
                    toastr.success("Điểm danh thành công");
                    $('#checkInModal').modal('hide');
                    workShift.find('button').remove();
                    let checkInSuccessDiv = $('<div style="margin-top: 5px"></div>').text('Đã check in lúc: ' + timeCheckIn);
                    workShift.append(checkInSuccessDiv);
                    let checkoutButton = `<button id="check-out" >check-out</button>`;
                    workShift.append(checkoutButton);
                }
                else {
                    const message = await response.json();
                    toastr.error(message.message)
                }
            })


    })
})

$(document).ready(function () {
    $('#check-out').on('click', async function () {
        let shiftName = $(this).closest('td').find('div.shift').text();
        console.log(shiftName)
        console.log(userName)
        if (userName === shiftName) {
            let tdOnShift = $(this).closest('td');
            let timeCheckOut = new Date().toLocaleTimeString("vi-VN", { hour12: false });
            let shiftAssignmentId = tdOnShift.data('shift-assignment-id');
            await fetch('/shift-assignment/' + shiftAssignmentId  + '/work-shift-check-out?checkOutTime=' + timeCheckOut, {
                method: 'PUT'
            })
                .then(response => {
                    if (response.ok){
                        let checkOutSuccessDiv = $('<div></div>').text('Đã check out lúc: ' + timeCheckOut);
                        tdOnShift.append(checkOutSuccessDiv);
                        tdOnShift.find('button').remove();
                    }
                    else {
                        console.log(response);
                    }
                })

        } else {
            toastr.error("Bạn không được phân công cho ca làm việc này")
        }

    })
})

