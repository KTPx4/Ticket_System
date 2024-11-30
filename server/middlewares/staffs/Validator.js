const StaffModel = require('../../models/StaffModel')
const bcrypt = require('bcrypt')

module.exports.UpdateImage = async(req, res, next)=>
{
    let file = req.file
    if(!file)
    {
        return res.status(400).json({
            message: "Vui lòng cung cấp ảnh: 'image'"
        })
    }
    return next()
}

module.exports.register = async(req, res, next)=>{
    let {user, email, name}= req.body
    if(!user || !email || !name)
    {
        return res.status(400).json({
            message: `Vui lòng cung cấp đầy đủ thông tin: ${!user?"'user'":""} ${!email? "'email'" : ""} ${!name? "'name'": ""}`,
            data:{
                user: user,
                email: email,
                name: name
            }
        })
    }
    var acc = await StaffModel.findOne({user: user})
    if(acc)
    {
        return res.status(400).json({
            message: `User đã tồn tại`,
            data:{
                user: user,
                email: email,
                name: name
            }
        })
    }
    return next()
}

module.exports.login = async( req, res, next)=>{
    let {user, pass} = req.body
    if(!user || !pass)
    {
        return res.status(400).json({
            message: `Vui lòng cung cấp đầy đủ thông tin: ${!user?"'user'":""}  ${!pass? "'pass'": ""}`,
            data:{
                user: user,
                pass: pass
            }
        })
    }
    var acc = await StaffModel.findOne({user: user})
    if(!acc)
    {
        return res.status(400).json({
            message: "User không tồn tại",
            data:{
                user: user,
                pass: pass
            }
        })
    }

    var passMatch = await bcrypt.compare(pass, acc.pass)
    if(!passMatch)
    {
        return res.status(400).json({
            message: "Mật khẩu không đúng",
            data:{
                user: user,
                pass: pass
            }
        })
    }

    req.vars.Staff = acc
    return next()
}