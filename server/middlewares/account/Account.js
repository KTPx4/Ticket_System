const jwt = require('jsonwebtoken')
const SECRET_LOGIN = process.env.KEY_SECRET_LOGIN || 'px4-secret-key-login-app'
const bcrypt = require('bcrypt')
const AccountModel = require('../../models/AccountModel')

const AuthAccount = async (req, res, next) =>{
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

            account = await AccountModel.findOne({
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

            req.vars.User = account

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