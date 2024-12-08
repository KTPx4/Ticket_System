const jwt = require('jsonwebtoken')
const SECRET_LOGIN = process.env.KEY_SECRET_LOGIN || 'px4-secret-key-login-app'
const bcrypt = require('bcrypt')
const AccountModel = require('../../models/AccountModel')

module.exports.AuthAccount = async (req, res, next) =>{
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

            let userU = data.email?.toLowerCase()
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
                email: userU,
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

module.exports.Register = async(req, res, next)=>{
    let {pass , email} = req.body
    if(!pass || !email)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp đủ thông tin 'email' 'pass'"
        })
    }
    var acc = await AccountModel.findOne({email: email})
    if(acc)
    {
        return res.status(400).json({
            message: "Email này đã đăng ký tài khoản"
        })
    }
    return next()
}

module.exports.Update = async(req, res, next)=>{
    let {name ,address, email} = req.body
    var UpdateVar = {}
    if(name) UpdateVar.name = name
    if(address) UpdateVar.address = address
    if(email)
    {
        var acc = await AccountModel.findOne({email: email})
        if(acc)
        {
            return res.status(400).json({
                message: "Email đã đăng ký tài khoản"
            })
        }
        UpdateVar.email = email

    }

    req.vars.updateData = UpdateVar
    return next()
}

module.exports.SendReset = async(req, res, next)=>{
    var {email} = req.body
    if(!email)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp đủ thông tin 'email'"
        })
    }
    var acc = await AccountModel.findOne({email: email})
    if(!acc)
    {
        return res.status(400).json({
            message: "Account không tồn tại"
        })
    }
    req.vars.User = acc
    return next()
}

module.exports.GetReset = async(req, res, next)=>{
    var {token} = req.query
    if(!token)
    {
        return res.status(400).json({
            message: "Token không hợp lệ"
        })
    }

    return next()
}

module.exports.History = async (req, res, next)=>{
    var {search} = req.body
    if(!search)
    {
        return res.status(400).json({
            message:"Vui lòng cung cấp 'search'"
        })
    }
    return next()
}

module.exports.Password = async(req, res, next)=>{
    var {oldPass, newPass} = req.body
    var {User} = req.vars
    if(!oldPass || !newPass)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp đủ thông tin 'oldPass' 'newPass'"
        })
    }
    var passMatch = await bcrypt.compare(oldPass, User.pass)
    if(!passMatch)
    {
        return res.status(400).json({
            message: "Mật khẩu cũ không đúng"
        })
    }

    return next()
}

module.exports.Login = async(req, res, next)=>{
    let {email, pass} = req.body
    if(!email || !pass)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp đủ thông tin 'email' 'pass'"
        })
    }
    var acc = await AccountModel.findOne({email: email})
    if(!acc)
    {
        return res.status(400).json({
            message: "Tài khoản không tồn tại"
        })
    }

    var passMatch = await bcrypt.compare(pass, acc.pass)
    if(!passMatch)
    {
        return res.status(400).json({
            message: "Mật khẩu không đúng"
        })
    }

    req.vars.User = acc
    return next()
}