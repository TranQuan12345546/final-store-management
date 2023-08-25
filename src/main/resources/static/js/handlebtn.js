const registerBtn = document.getElementById("registerBtn");
const modal = document.getElementById("modal");
const closeBtn = document.getElementById("close-btn");

registerBtn.addEventListener("click", function () {
    modal.classList.add("active");
    document.body.classList.add("modal-open");
});

$(document).on('click', '.btn-buy', function () {
    modal.classList.add("active");
    document.body.classList.add("modal-open");
})


closeBtn.addEventListener("click", function () {
    modal.classList.add("fadeOut");
    setTimeout(function () {
        modal.classList.remove("active", "fadeOut");
        document.body.classList.remove("modal-open");
    }, 400);
});

// api get tỉnh, thành phố
const province = document.getElementById("province");

async function getProvince() {
    const res = await fetch("https://provinces.open-api.vn/api/p/");
    const provinceData = await res.json();

    for (let i = 0; i < provinceData.length; i++) {
        let option = document.createElement("option");
        option.innerText = provinceData[i].name;
        option.value = `${provinceData[i].code}`;
        province.appendChild(option);
    }
    province.addEventListener("change", function () {
        getDistrict(province.value);
    });
}

async function getDistrict(provinceCode) {
    const district = document.getElementById("district");
    district.innerHTML = "";

    console.log(district);
    const res = await fetch(
        `https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`
    );
    const districtData = await res.json();
    console.log(districtData);

    for (let i = 0; i < districtData.districts.length; i++) {
        let option = document.createElement("option");
        option.innerText = districtData.districts[i].name;
        option.value = `${districtData.districts[i].code}`;
        district.appendChild(option);
    }
}

$(document).ready(async function () {
    await getProvince();
})


//Modal open



let isConfirm = false;
$(document).ready(function () {
    $('.btn-action').on('click', async function (event) {
        event.preventDefault();
        const email = $("#confirm-email").val();
        console.log(email)
        await fetch('/send-resister-code?email=' + email, {
            method: 'POST'
        })
            .then(async response => {
                if (response.ok) {
                    toastr.success("Gửi email xác nhận thành công, vui lòng kiểm tra email của bạn.")
                } else {
                    const errorMessage = await response.text();
                    toastr.error(errorMessage);
                }
            })
            // .then(response => {
            //     console.log(response)
            // })

        $('#confirmCode').on('blur', async function () {
            await checkCodeValid($('#confirmCode').val());
        });

        $('#confirmCode').on('keypress', async function (event) {
            if (event.which === 13) {
                await checkCodeValid($('#confirmCode').val());
            }
        });

        async function checkCodeValid(code) {
            await fetch('/resister/' + code, {
                method: 'GET'
            })
                .then(async response => {
                    if (response.ok) {
                        toastr.success("Xác thực thành công.")
                        isConfirm = true;
                    } else {
                        let message = await response.text();
                        toastr.error(message.message);
                    }
                })
        }


        $('#resister-btn').on('click', async function (event) {
            event.preventDefault();
            if (!$('#exampleCheck1').prop('checked')) {
                toastr.warning("Bạn cần đọc và đồng ý với các điều khoản trước.")
            } else if (!isConfirm) {
                toastr.warning("Bạn cần xác thực email.")
            } else {
                let fullName = $('#fullName').val();
                let phoneNumber = $('#phoneNumber').val();
                let email = $('#confirm-email').val();
                let provinceText = $('#province option:selected').text();
                let districtText = $('#district option:selected').text();
                let storeName = $('#storeName').val();
                let username = $('#username').val();
                let password = $('#password').val();


                let address = provinceText + ' - ' + districtText;

                let uploadResisterRequest = {
                    fullName: fullName,
                    username: username,
                    password: password,
                    nameStore: storeName,
                    phone: phoneNumber,
                    email: email,
                    address: address
                }
                console.log(uploadResisterRequest)

                await fetch('/register', {
                    method: 'POST',
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(uploadResisterRequest)
                }).then(response => {
                    if (response.ok) {
                        toastr.success("Đăng ký thành công, xin mời đăng nhập");
                        setTimeout(function () {
                            window.location.href='/login';
                        }, 500)
                    }
                })
            }
        })
    })

    $('#resister').validate({
        rules: {
            fullName: {
                required: true
            },
            phoneNumber: {
                required: true,
                number: true
            },
            confirmEmail: {
                required: true,
                email: true
            },
            province: {
                required: true
            },
            district: {
                required: true
            },
            storeName: {
                required:true
            },
            username: {
                required: true
            },
            password: {
                required: true,
                minlength: 8,
                pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
            }
        },
        messages: {
            fullName: {
                required: "Vui lòng nhập đầy đủ họ và tên."
            },
            phoneNumber: {
                required: "Vui lòng nhập số điện thoại.",
                number: "Vui lòng nhập số."
            },
            confirmEmail: {
                required: "Vui lòng nhập email.",
                email: "Vui lòng nhập email hợp lệ"
            },
            province: {
                required: "Vui lòng chọn địa chỉ."
            },
            district: {
                required: "Vui lòng chọn địa chỉ."
            },
            storeName: {
                required: "Vui lòng nhập tên cửa hàng."
            },
            username: {
                required: "Vui lòng nhập tên đăng nhập"
            },
            password: {
                required: "Vui lòng nhập mật khẩu",
                minlength: "Mật khẩu cần có ít nhất 8 ký tự",
                pattern: "Mật khẩu cần chứa ít nhất 1 ký tự thường, 1 ký tự hoa, và 1 ký tự đặc biệt",
            }
        }
    });
})



