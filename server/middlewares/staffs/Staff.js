const jwt = require('jsonwebtoken')
const SECRET_LOGIN = process.env.KEY_SECRET_LOGIN || 'px4-secret-key-login-app'
const bcrypt = require('bcrypt')
const StaffModel = require('../../models/StaffModel')

const AuthStaff = async (req, res, next) =>{
    try{
        // Get token from header or body
        let tokenFromHeader =  req.header('Authorization')

        let token = undefined

        if(!tokenFromHeader)
            token =  req.body.token
        else
            token = tokenFromHeader.split(' ')[1]


        if(!token || token === undefined)
        {
            return res.status(401).json({
                status: "Authorization",
                message: 'Vui lòng cung cấp token mới có quyền truy cập'
            })
        }

        // verify token

        await jwt.verify(token, SECRET_LOGIN, async(err, data)=>{
            if(err)
            {
                return res.status(400).json({
                    status: 'Invalid Token',
                    message: 'Đăng nhập thất bại hoặc phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại!'
                })
            }

            let userU = data.user?.toLowerCase()
            var pass = data.pass

            let account

            if(!pass || !userU)
            {
                return res.status(401).json({
                    status: 'Invalid Token',
                    message: 'Token đăng nhập không hợp lệ'
                })
            }

            account = await StaffModel.findOne({
                user: userU,
            })

            if(!account || account.isDeleted === true)
            {
                return res.status(401).json({
                    status: 'Account not exists',
                    message: 'Tài khoản không tồn tại hoặc bị xóa, không thể đăng nhập'
                })
            }

            var old = account.pass


            if(old != pass )
            {
                return res.status(401).json({
                    status: 'Password not match',
                    message: 'Mật khẩu đã được thay đổi. Vui lòng đăng nhập lại!'
                })
            }

            req.vars.Staff = account

            return next()
        })
    }
    catch(err)
    {
        console.log("Error at Auth - AuthAccount: ", err);
        return res.status(500).json({
            status: "Error Server",
            message: "Vui lòng thử lại sau"
        })
    }



}

const AuthRole = (requiredRoles) => {
    return async (req, res, next) => {
        try {
            // Gọi AuthStaff trước để xác thực
            await AuthStaff(req, res, async (err) => {
                if (err) {
                    return res.status(401).json({
                        status: "Unauthorized",
                        message: "Xác thực không thành công."
                    });
                }

                // Lấy thông tin Staff từ AuthStaff
                const staff = req.vars?.Staff;
                if (!staff) {
                    return res.status(403).json({
                        status: "Forbidden",
                        message: "Không tìm thấy thông tin người dùng."
                    });
                }

                // Kiểm tra quyền (role)
                if (Array.isArray(requiredRoles)) {
                    // Nếu requiredRoles là một mảng, kiểm tra có trùng role nào không
                    if (!requiredRoles.includes(staff.role)) {
                        return res.status(403).json({
                            status: "Forbidden",
                            message: `Bạn không có quyền truy cập tài nguyên này. (Role yêu cầu: ${requiredRoles.join(', ')})`
                        });
                    }
                } else if (requiredRoles !== staff.role) {
                    // Nếu requiredRoles là một giá trị duy nhất
                    return res.status(403).json({
                        status: "Forbidden",
                        message: `Bạn không có quyền truy cập tài nguyên này. (Role yêu cầu: ${requiredRoles})`
                    });
                }

                // Nếu tất cả hợp lệ, tiếp tục
                next();
            });
        } catch (err) {
            console.error("Error at AuthRole middleware: ", err);
            return res.status(500).json({
                status: "Error Server",
                message: "Lỗi máy chủ. Vui lòng thử lại sau."
            });
        }
    };
};


module.exports.AuthStaff = AuthStaff
module.exports.AuthRole = AuthRole;
